"use client";

import {useEffect, useMemo, useRef} from "react";
import {Canvas, useFrame, useThree} from "@react-three/fiber";
import {OrbitControls} from "@react-three/drei";
import * as THREE from "three";
import gsap from "gsap";

const BACKGROUND_COLOR = "#FAFAF7";
const SURFACE_COLOR = "#FAFAF7";
const WIREFRAME_COLOR = "#727478";

const R = 1.5;
const w = 0.45;
const uCount = 65;
const vCount = 10;

function point(u: number, v: number, target: THREE.Vector3) {
    const t = u * Math.PI * 2;
    const s = (v - 0.5) * 2 * w;
    const halfT = t / 2;

    const x = (R + s * Math.cos(halfT)) * Math.cos(t);
    const y = (R + s * Math.cos(halfT)) * Math.sin(t);
    const z = s * Math.sin(halfT);
    target.set(x, y, z);
}

function createMobiusGrid(): THREE.Vector3[] {
    const points: THREE.Vector3[] = [];
    for (let i = 0; i < uCount; i++) {
        const u = i / uCount;
        for (let j = 0; j < vCount; j++) {
            const v = j / (vCount - 1);
            const p = new THREE.Vector3();
            point(u, v, p);
            points.push(p);
        }
    }
    return points;
}

function createMobiusLineGeometry() {
    const points = createMobiusGrid();
    const positions: number[] = [];

    // Horizontal lines along the width
    for (let i = 0; i < uCount; i++) {
        for (let j = 0; j < vCount - 1; j++) {
            const a = points[i * vCount + j];
            const b = points[i * vCount + j + 1];
            positions.push(a.x, a.y, a.z, b.x, b.y, b.z);
        }
    }

    // Vertical lines along the length
    for (let i = 0; i < uCount - 1; i++) {
        for (let j = 0; j < vCount; j++) {
            const a = points[i * vCount + j];
            const b = points[(i + 1) * vCount + j];
            positions.push(a.x, a.y, a.z, b.x, b.y, b.z);
        }
    }

    // Wrap-around seam with the Möbius half-twist (v index flips)
    for (let j = 0; j < vCount; j++) {
        const a = points[(uCount - 1) * vCount + j];
        const b = points[vCount - 1 - j];
        positions.push(a.x, a.y, a.z, b.x, b.y, b.z);
    }

    const geometry = new THREE.BufferGeometry();
    geometry.setAttribute(
        "position",
        new THREE.Float32BufferAttribute(positions, 3)
    );
    return geometry;
}

function createMobiusSurfaceGeometry() {
    const points = createMobiusGrid();
    const positions: number[] = [];

    const pushTriangle = (a: THREE.Vector3, b: THREE.Vector3, c: THREE.Vector3) => {
        positions.push(a.x, a.y, a.z, b.x, b.y, b.z, c.x, c.y, c.z);
    };

    const idx = (i: number, j: number) => i * vCount + j;

    // Internal quads
    for (let i = 0; i < uCount - 1; i++) {
        for (let j = 0; j < vCount - 1; j++) {
            const a = points[idx(i, j)];
            const b = points[idx(i, j + 1)];
            const c = points[idx(i + 1, j)];
            const d = points[idx(i + 1, j + 1)];
            pushTriangle(a, b, c);
            pushTriangle(b, d, c);
        }
    }

    // Wrap-around quads with the Möbius half-twist
    for (let j = 0; j < vCount - 1; j++) {
        const a = points[idx(uCount - 1, j)];
        const b = points[idx(uCount - 1, j + 1)];
        const c = points[idx(0, vCount - 1 - j)];
        const d = points[idx(0, vCount - 2 - j)];
        pushTriangle(a, b, c);
        pushTriangle(b, d, c);
    }

    const geometry = new THREE.BufferGeometry();
    geometry.setAttribute(
        "position",
        new THREE.Float32BufferAttribute(positions, 3)
    );
    return geometry;
}

function createBackgroundDotGeometry(count = 2500) {
    const positions = new Float32Array(count * 3);
    const sizes = new Float32Array(count);

    // Use a large symmetric sphere so the background stays full-screen
    // regardless of the camera angle.
    const range = 10;

    let i = 0;
    while (i < count) {
        const x = (Math.random() * 2 - 1) * range;
        const y = (Math.random() * 2 - 1) * range;
        const z = (Math.random() * 2 - 1) * range;

        const r = Math.sqrt(x * x + y * y + z * z);
        if (r > range) continue;

        // Sparser toward the edges of the sphere (screen edges).
        const edge = r / range;
        if (Math.random() > 1 - edge * edge) continue;

        positions[i * 3] = x;
        positions[i * 3 + 1] = y;
        positions[i * 3 + 2] = z;

        sizes[i] = 0.012 * (1 - 0.5 * edge) + 0.004;
        i++;
    }

    const geometry = new THREE.BufferGeometry();
    geometry.setAttribute("position", new THREE.BufferAttribute(positions, 3));
    geometry.setAttribute("size", new THREE.BufferAttribute(sizes, 1));
    return geometry;
}

function createAccentDotGeometry(count = 6) {
    const positions = new Float32Array(count * 3);
    const sizes = new Float32Array(count);
    const center = new THREE.Vector3();
    const radial = new THREE.Vector3();
    const normal = new THREE.Vector3();

    for (let i = 0; i < count; i++) {
        const u = i / count;
        const t = u * Math.PI * 2;
        const halfT = t / 2;
        const cosT = Math.cos(t);
        const sinT = Math.sin(t);
        const cosHT = Math.cos(halfT);
        const sinHT = Math.sin(halfT);

        // Point on the Möbius strip centerline.
        center.set(R * cosT, R * sinT, 0);

        // Surface normal at the centerline (s = 0).
        radial.set(cosT, sinT, 0);
        normal.set(cosHT * cosT, cosHT * sinT, sinHT);

        // Offset outward from the ring so the accent dots sit around it.
        const r = w + 0.4 + Math.random() * 0.4;
        const angle = Math.random() * Math.PI * 2;
        const cosA = Math.cos(angle);
        const sinA = Math.sin(angle);

        positions[i * 3] =
            center.x + r * (radial.x * cosA + normal.x * sinA);
        positions[i * 3 + 1] =
            center.y + r * (radial.y * cosA + normal.y * sinA);
        positions[i * 3 + 2] =
            center.z + r * (radial.z * cosA + normal.z * sinA);

        sizes[i] = 0.03 + Math.random() * 0.02;
    }

    const geometry = new THREE.BufferGeometry();
    geometry.setAttribute("position", new THREE.BufferAttribute(positions, 3));
    geometry.setAttribute("size", new THREE.BufferAttribute(sizes, 1));
    return geometry;
}

const DOT_VERTEX_SHADER = `
    attribute float size;
    varying float vAlpha;
    uniform float uScale;
    uniform float uPixelRatio;
    uniform float uFadeStart;
    uniform float uFadeEnd;

    void main() {
        vec4 mvPosition = modelViewMatrix * vec4(position, 1.0);
        gl_Position = projectionMatrix * mvPosition;

        float screenSize = size * (uScale / -mvPosition.z) * uPixelRatio;
        gl_PointSize = screenSize;

        vAlpha = 1.0 - smoothstep(uFadeStart, uFadeEnd, screenSize);
    }
`;

const DOT_FRAGMENT_SHADER = `
    varying float vAlpha;

    void main() {
        vec2 coord = gl_PointCoord - vec2(0.5);
        if (length(coord) > 0.5) discard;

        gl_FragColor = vec4(0.0, 0.0, 0.0, 0.85 * vAlpha);
    }
`;

function DotField({
    backgroundGeometry,
    accentGeometry,
}: {
    backgroundGeometry: THREE.BufferGeometry;
    accentGeometry: THREE.BufferGeometry;
}) {
    const {gl} = useThree();
    const groupRef = useRef<THREE.Group>(null);
    const ownAngle = useRef(0);

    const material = useMemo(
        () =>
            new THREE.ShaderMaterial({
                transparent: true,
                depthWrite: false,
                uniforms: {
                    uScale: {value: 600.0},
                    uPixelRatio: {value: gl.getPixelRatio()},
                    uFadeStart: {value: 18.0},
                    uFadeEnd: {value: 32.0},
                },
                vertexShader: DOT_VERTEX_SHADER,
                fragmentShader: DOT_FRAGMENT_SHADER,
            }),
        [gl]
    );

    useEffect(() => {
        material.uniforms.uPixelRatio.value = gl.getPixelRatio();
    }, [gl, material]);

    useEffect(() => {
        if (!groupRef.current) return;

        groupRef.current.scale.set(0.01, 0.01, 0.01);

        const tween = gsap.to(groupRef.current.scale, {
            x: 1,
            y: 1,
            z: 1,
            duration: 2.2,
            ease: "power2.out",
            delay: 0.1,
        });

        return () => {
            tween.kill();
        };
    }, []);

    useFrame((_, delta) => {
        if (!groupRef.current) return;
        // Clockwise rotation in the XY plane, independent of the camera.
        ownAngle.current -= 0.03 * delta;
        groupRef.current.rotation.z = ownAngle.current;
    });

    return (
        <group ref={groupRef}>
            <points geometry={backgroundGeometry} material={material}/>
            <points geometry={accentGeometry} material={material}/>
        </group>
    );
}

function MobiusScene() {
    const {gl} = useThree();
    const groupRef = useRef<THREE.Group>(null);

    const lineGeometry = useMemo(() => createMobiusLineGeometry(), []);
    const surfaceGeometry = useMemo(() => createMobiusSurfaceGeometry(), []);
    const backgroundDotGeometry = useMemo(() => createBackgroundDotGeometry(), []);
    const accentDotGeometry = useMemo(() => createAccentDotGeometry(), []);

    useEffect(() => {
        gl.setClearColor(BACKGROUND_COLOR);
    }, [gl]);

    useEffect(() => {
        if (!groupRef.current) return;

        groupRef.current.scale.set(0.01, 0.01, 0.01);

        gsap.to(groupRef.current.scale, {
            x: 1,
            y: 1,
            z: 1,
            duration: 2.2,
            ease: "power2.out",
        });
    }, []);

    useFrame((_, delta) => {
        if (!groupRef.current) return;
        // The Möbius strip rotates on its own, independent of the camera.
        groupRef.current.rotation.y += 0.1 * delta;
    });

    return (
        <>
            <group ref={groupRef}>
                <mesh geometry={surfaceGeometry}>
                    <meshBasicMaterial
                        color={SURFACE_COLOR}
                        side={THREE.DoubleSide}
                        polygonOffset
                        polygonOffsetFactor={1}
                        polygonOffsetUnits={1}
                    />
                </mesh>
                <lineSegments geometry={lineGeometry}>
                    <lineBasicMaterial
                        color={WIREFRAME_COLOR}
                        polygonOffset
                        polygonOffsetFactor={-1}
                        polygonOffsetUnits={-1}
                    />
                </lineSegments>
            </group>
            <DotField
                backgroundGeometry={backgroundDotGeometry}
                accentGeometry={accentDotGeometry}
            />
            <OrbitControls
                enableZoom
                enablePan={false}
                enableRotate
                minDistance={1}
                maxDistance={6}
            />
        </>
    );
}

function NoiseOverlay() {
    const canvasRef = useRef<HTMLCanvasElement>(null);

    useEffect(() => {
        const canvas = canvasRef.current;
        if (!canvas) return;
        const ctx = canvas.getContext("2d");
        if (!ctx) return;

        const draw = () => {
            const dpr = Math.min(window.devicePixelRatio, 2);
            const width = window.innerWidth;
            const height = window.innerHeight;

            canvas.width = width * dpr;
            canvas.height = height * dpr;
            canvas.style.width = `${width}px`;
            canvas.style.height = `${height}px`;
            ctx.setTransform(dpr, 0, 0, dpr, 0, 0);

            ctx.clearRect(0, 0, width, height);
            const count = Math.floor(width * height * 0.04);
            for (let i = 0; i < count; i++) {
                ctx.fillStyle = `rgba(0,0,0,${Math.random() * 0.07})`;
                ctx.fillRect(Math.random() * width, Math.random() * height, 1, 1);
            }
        };

        draw();
        window.addEventListener("resize", draw);
        return () => window.removeEventListener("resize", draw);
    }, []);

    return (
        <canvas
            ref={canvasRef}
            className="pointer-events-none fixed inset-0 z-[1]"
            style={{mixBlendMode: "multiply", opacity: 0.55}}
        />
    );
}

export function MobiusBackground() {
    return (
        <>
            <div className="fixed inset-0 z-0">
                <Canvas flat
                    camera={{position: [0, 0, 4.5], fov: 65}}
                    gl={{antialias: true, alpha: true}}
                    dpr={typeof window !== "undefined" ? Math.min(window.devicePixelRatio, 2) : 1}
                >
                    <MobiusScene/>
                </Canvas>
            </div>
            <NoiseOverlay/>
        </>
    );
}
