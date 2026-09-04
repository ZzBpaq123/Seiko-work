"use client";

import { useEffect, useRef, useState } from "react";
import { MobiusBackground } from "@/components/MobiusBackground";
import { SiteHeader } from "@/components/SiteHeader";
import { SideNav } from "@/components/SideNav";
import { MailPanel } from "@/components/MailPanel";
import { LoginPanel } from "@/components/LoginPanel";
import { PanelReveal } from "@/components/PanelReveal";
import { useHash } from "@/hooks/useHash";

const PANEL_HASHES = ["#mail", "#login"];

export default function Home() {
  const hash = useHash();
  const [closingHash, setClosingHash] = useState<string | null>(null);
  const prevHashRef = useRef("");

  // 面板关闭后延迟卸载，给退场动画留出时间
  useEffect(() => {
    const prev = prevHashRef.current;
    prevHashRef.current = hash;
    if (prev !== hash && PANEL_HASHES.includes(prev)) {
      setClosingHash(prev);
      const timer = setTimeout(() => setClosingHash(null), 300);
      return () => clearTimeout(timer);
    }
  }, [hash]);

  const pageOpen =
    (hash.startsWith("#") && hash !== "#") || closingHash !== null;

  return (
    <>
      <MobiusBackground />
      {pageOpen && (
        <div
          aria-hidden
          className="fixed inset-0 z-5 bg-[#FAFAF7]/75 backdrop-blur-none"
        />
      )}
      <SiteHeader />
      <SideNav />
      {hash === "#mail" && (
        <PanelReveal phase="in">
          <MailPanel />
        </PanelReveal>
      )}
      {hash === "#login" && (
        <PanelReveal phase="in">
          <LoginPanel />
        </PanelReveal>
      )}
      {closingHash === "#mail" && hash !== "#mail" && (
        <PanelReveal phase="out">
          <MailPanel />
        </PanelReveal>
      )}
      {closingHash === "#login" && hash !== "#login" && (
        <PanelReveal phase="out">
          <LoginPanel />
        </PanelReveal>
      )}
    </>
  );
}
