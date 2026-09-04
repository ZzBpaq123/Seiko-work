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

export function Toaster() {
  const [queue, setQueue] = useState<ToastItem[]>([]);

  useEffect(() => subscribeToasts(setQueue), []);

  const current = queue[0];
  if (!current) return null;

  const { icon: Icon, className } = TYPE_STYLES[current.type];

  return (
    <div
      key={current.id}
      role="status"
      className={cn(
        "toast-pop fixed left-1/2 top-6 z-[100] flex max-w-md items-center gap-2 rounded-xl border px-4 py-2.5 text-sm shadow-lg backdrop-blur-sm",
        className
      )}
    >
      <Icon className="h-4 w-4 shrink-0" />
      <span className="leading-snug">{current.message}</span>
      <button
        type="button"
        aria-label="关闭"
        onClick={() => dismissToast(current.id)}
        className="ml-1 rounded-full p-0.5 opacity-60 transition-opacity hover:opacity-100"
      >
        <X className="h-3.5 w-3.5" />
      </button>
    </div>
  );
}
