"use client";

import { MobiusBackground } from "@/components/MobiusBackground";
import { SiteHeader } from "@/components/SiteHeader";
import { SideNav } from "@/components/SideNav";
import { MailPanel } from "@/components/MailPanel";
import { LoginPanel } from "@/components/LoginPanel";
import { useHash } from "@/hooks/useHash";

export default function Home() {
  const hash = useHash();
  const pageOpen = hash.startsWith("#") && hash !== "#";

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
      <MailPanel />
      <LoginPanel />
    </>
  );
}
