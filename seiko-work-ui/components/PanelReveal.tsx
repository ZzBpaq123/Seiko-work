"use client";

import type { ReactNode } from "react";
import { cn } from "@/lib/utils";

export function PanelReveal({
  phase,
  children,
}: {
  phase: "in" | "out";
  children: ReactNode;
}) {
  return (
    <div
      className={cn(
        "pointer-events-none fixed inset-0 z-10",
        phase === "in" ? "panel-in" : "panel-out"
      )}
    >
      {children}
    </div>
  );
}
