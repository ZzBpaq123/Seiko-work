"use client";

import { useEffect, useMemo, useRef } from "react";
import { Canvas, useFrame, useThree } from "@react-three/fiber";
import * as THREE from "three";
import { ParametricGeometry } from "three/examples/jsm/geometries/ParametricGeometry.js";
import gsap from "gsap";

const BACKGROUND_COLOR = 0xf8f6f0;
const WIREFRAME_COLOR = 0x000000;
const DOTS_COLOR = 0x222222;

function createMobiusGeometry() {
  function mobiusFunction(
    u: number,
    v: number,
    target: THREE.Vector3
  ) {
    const R = 1.3;
    const w = 0.55;
    const t = u * Math.PI * 2;
    const s = (v - 0.5) * 2 * w;
    const halfT = t / 2;

    const x = (R + s * Math.cos(halfT)) * Math.cos(t);
    const y = (R + s * Math.cos(halfT)) * Math.sin(t);
    const z = s * Math.sin(halfT);
    target.set(x, y, z);
  }

  return new ParametricGeometry(mobiusFunction, 100, 20);
}

function createDotsGeometry(count = 2200) {
  const positions: number[] = [];
  for (let i = 0; i < count; i++) {
    positions.push(
      (Math.random() - 0.5) * 22,
      (Math.random() - 0.5) * 16,
      (Math.random() - 0.5) * 8 - 6
    );
  }

  const geometry = new THREE.BufferGeometry();
  geometry.setAttribute(
    "position",
    new THREE.Float32BufferAttribute(positions, 3)
  );
  return geometry;
}

function MobiusScene() {
  const { scene, camera, gl } = useThree();
  const mobiusRef = useRef<THREE.Mesh>(null);

  const geometry = useMemo(() => createMobiusGeometry(), []);

  useEffect(() => {
    gl.setClearColor(BACKGROUND_COLOR);
  }, [gl]);

  useEffect(() => {
    if (!mobiusRef.current) return;

    mobiusRef.current.scale.set(0.01, 0.01, 0.01);

    gsap.to(mobiusRef.current.scale, {
      x: 1,
      y: 1,
      z: 1,
      duration: 2.2,
      ease: "power2.out",
    });
  }, []);

  useFrame(() => {
    if (!mobiusRef.current) return;
    mobiusRef.current.rotation.y += 0.0045;
    mobiusRef.current.rotation.x += 0.0012;
  });

  const dotsGeometry = useMemo(() => createDotsGeometry(), []);

  return (
    <>
      <mesh ref={mobiusRef} geometry={geometry}>
        <meshBasicMaterial
          color={WIREFRAME_COLOR}
          wireframe
          side={THREE.DoubleSide}
        />
      </mesh>
      <points geometry={dotsGeometry}>
        <pointsMaterial
          color={DOTS_COLOR}
          size={0.022}
          transparent
          opacity={0.65}
        />
      </points>
    </>
  );
}

export function MobiusBackground() {
  return (
    <div className="fixed inset-0 z-0">
      <Canvas
        camera={{ position: [0, 0, 4.5], fov: 60 }}
        gl={{ antialias: true, alpha: true }}
        dpr={typeof window !== "undefined" ? Math.min(window.devicePixelRatio, 2) : 1}
      >
        <MobiusScene />
      </Canvas>
    </div>
  );
}
