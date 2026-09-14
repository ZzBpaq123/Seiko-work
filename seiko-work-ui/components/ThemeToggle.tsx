"use client";

import { useTheme } from "next-themes";
import { Moon, Sun } from "lucide-react";
import { useEffect, useState } from "react";
import { flushSync } from "react-dom";
import { cn } from "@/lib/utils";

type ViewTransitionDocument = Document & {
  startViewTransition?: (callback: () => void) => {
    ready: Promise<void>;
  };
};

export function ThemeToggle({ className }: { className?: string }) {
  const { setTheme, resolvedTheme } = useTheme();
  const [mounted, setMounted] = useState(false);

  useEffect(() => {
    setMounted(true);
  }, []);

  if (!mounted) {
    return (
      <button
        className={cn(
          "relative flex h-12 w-12 items-center justify-center overflow-hidden rounded-full border backdrop-blur-sm",
          className
        )}
        aria-label="切换主题"
      >
        <span className="h-5 w-5" />
      </button>
    );
  }

  const isDark = resolvedTheme === "dark";

  // 深色→浅色：浅色圆形从按钮处展开到屏幕最远端（动画作用在新页面上）
  // 浅色→深色：浅色圆形从最远端收缩到按钮处（动画作用在旧页面上）
  const toggleTheme = (event: React.MouseEvent<HTMLButtonElement>) => {
    const nextTheme = isDark ? "light" : "dark";
    const doc = document as ViewTransitionDocument;

    if (!doc.startViewTransition) {
      setTheme(nextTheme);
      return;
    }

    const x = event.clientX;
    const y = event.clientY;
    const radius = Math.hypot(
      Math.max(x, window.innerWidth - x),
      Math.max(y, window.innerHeight - y)
    );

    const transition = doc.startViewTransition(() => {
      flushSync(() => setTheme(nextTheme));
    });

    transition.ready
      .then(() => {
        const target = isDark ? "::view-transition-new(root)" : "::view-transition-old(root)";
        const other = isDark ? "::view-transition-old(root)" : "::view-transition-new(root)";

        // 主动画：深色→浅色时新页面（浅色）圆形从按钮展开到最远端；
        // 浅色→深色时旧页面（浅色）圆形从最远端收缩到按钮处
        document.documentElement.animate(
          isDark
            ? {
                clipPath: [
                  `circle(0px at ${x}px ${y}px)`,
                  `circle(${radius}px at ${x}px ${y}px)`,
                ],
              }
            : {
                clipPath: [
                  `circle(${radius}px at ${x}px ${y}px)`,
                  `circle(0px at ${x}px ${y}px)`,
                ],
              },
          { duration: 450, easing: "ease-in-out", fill: "forwards", pseudoElement: target }
        );

        // 另一张快照保持透明，避免遮挡主动画（其下方就是切换后的实时页面）
        document.documentElement.animate(
          { opacity: [0, 0] },
          { duration: 450, fill: "forwards", pseudoElement: other }
        );
      })
      .catch(() => {});
  };

  return (
    <button
      onClick={toggleTheme}
      className={cn(
        "relative flex h-12 w-12 items-center justify-center overflow-hidden rounded-full border backdrop-blur-sm transition-colors duration-200",
        isDark
          ? "border-neutral-50/20 bg-neutral-900/70 text-neutral-200 hover:bg-neutral-50 hover:text-neutral-900"
          : "border-neutral-900/15 bg-white/70 text-neutral-700 hover:border-neutral-900 hover:bg-neutral-900 hover:text-neutral-50",
        className
      )}
      aria-label="切换主题"
    >
      {isDark ? <Moon className="h-5 w-5" /> : <Sun className="h-5 w-5" />}
    </button>
  );
}
