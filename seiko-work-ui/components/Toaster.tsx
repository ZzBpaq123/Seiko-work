"use client";

import { useEffect, useState } from "react";
import { CheckCircle2, Info, X, XCircle } from "lucide-react";
import { cn } from "@/lib/utils";
import {
  dismissToast,
  subscribeToasts,
  type ToastItem,
  type ToastType,
} from "@/lib/toast";

const TYPE_STYLES: Record<
  ToastType,
  { icon: typeof Info; className: string }
> = {
  success: {
    icon: CheckCircle2,
    className: "border-emerald-900/15 bg-emerald-50/95 text-emerald-700",
  },
  error: {
    icon: XCircle,
    className: "border-red-900/15 bg-red-50/95 text-red-700",
  },
  info: {
    icon: Info,
    className: "border-neutral-900/15 bg-white/95 text-neutral-700",
  },
};

type RenderedToast = { item: ToastItem; phase: "in" | "out" };

export function Toaster() {
  const [rendered, setRendered] = useState<RenderedToast[]>([]);

  useEffect(
    () =>
      subscribeToasts((queue) => {
        setRendered((prev) => {
          // 已不在队列中的标记为退场，新出现的标记为入场
          const next = prev.map((r) =>
            queue.some((t) => t.id === r.item.id) ? r : { ...r, phase: "out" as const }
          );
          for (const t of queue) {
            if (!next.some((r) => r.item.id === t.id)) next.push({ item: t, phase: "in" });
          }
          return next;
        });
      }),
    []
  );

  // 退场动画结束后才真正卸载
  const handleAnimationEnd = (id: number) => {
    setRendered((prev) => prev.filter((r) => !(r.item.id === id && r.phase === "out")));
  };

  if (rendered.length === 0) return null;

  return (
    <>
      {rendered.map(({ item, phase }) => {
        const { icon: Icon, className } = TYPE_STYLES[item.type];
        return (
          <div
            key={item.id}
            role="status"
            onAnimationEnd={() => handleAnimationEnd(item.id)}
            className={cn(
              "fixed left-1/2 top-6 z-[100] flex max-w-md items-center gap-2 rounded-xl border px-4 py-2.5 text-sm shadow-lg backdrop-blur-sm",
              phase === "in" ? "toast-in" : "toast-out",
              className
            )}
          >
            <Icon className="h-4 w-4 shrink-0" />
            <span className="leading-snug">{item.message}</span>
            <button
              type="button"
              aria-label="关闭"
              onClick={() => dismissToast(item.id)}
              className="ml-1 rounded-full p-0.5 opacity-60 transition-opacity hover:opacity-100"
            >
              <X className="h-3.5 w-3.5" />
            </button>
          </div>
        );
      })}
    </>
  );
}
