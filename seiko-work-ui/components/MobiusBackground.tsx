"use client";

import {useEffect, useMemo, useRef, useState} from "react";
import {Canvas, useThree} from "@react-three/fiber";
import {OrbitControls} from "@react-three/drei";
import * as THREE from "three";
import gsap from "gsap";

const BACKGROUND_COLOR = "#F8F6F0";
const SURFACE_COLOR = "#FFFFFF";
const WIREFRAME_COLOR = "#727478";

const R = 1.3;
const w = 0.55;
const uCount = 40;
const vCount = 9;

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

function MobiusScene() {
    const {gl} = useThree();
    const groupRef = useRef<THREE.Group>(null);
    const [autoRotate, setAutoRotate] = useState(true);
    const resumeTimer = useRef<ReturnType<typeof setTimeout> | null>(null);

    const lineGeometry = useMemo(() => createMobiusLineGeometry(), []);
    const surfaceGeometry = useMemo(() => createMobiusSurfaceGeometry(), []);

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

        return () => {
            if (resumeTimer.current) clearTimeout(resumeTimer.current);
        };
    }, []);

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
            <OrbitControls
                autoRotate={autoRotate}
                autoRotateSpeed={0.8}
                enableZoom
                enablePan={false}
                enableRotate
                minDistance={2}
                maxDistance={10}
                onStart={() => {
                    setAutoRotate(false);
                    if (resumeTimer.current) clearTimeout(resumeTimer.current);
                }}
                onEnd={() => {
                    if (resumeTimer.current) clearTimeout(resumeTimer.current);
                    resumeTimer.current = setTimeout(() => setAutoRotate(true), 1500);
                }}
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
                <Canvas
                    camera={{position: [0, 0, 4.5], fov: 60}}
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
