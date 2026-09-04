"use client";

import { useEffect, useState, type ReactNode } from "react";
import { cn } from "@/lib/utils";

export function PanelReveal({
  open,
  children,
}: {
  open: boolean;
  children: ReactNode;
}) {
  const [mounted, setMounted] = useState(false);
  const [phase, setPhase] = useState<"in" | "out">("in");
  const [hidden, setHidden] = useState(false);

  useEffect(() => {
    if (open) {
      setMounted(true);
      setHidden(false);
      setPhase("in");
    } else if (mounted) {
      setPhase("out");
      const timer = setTimeout(() => setHidden(true), 200);
      return () => clearTimeout(timer);
    }
  }, [open, mounted]);

  // 首次打开前不渲染；之后保持挂载以保留面板内部状态
  if (!mounted) return null;

  return (
    <div
      className={cn(
        "pointer-events-none fixed inset-0 z-10",
        phase === "in" ? "panel-in" : "panel-out",
        hidden && "invisible"
      )}
    >
      {children}
    </div>
  );
}
