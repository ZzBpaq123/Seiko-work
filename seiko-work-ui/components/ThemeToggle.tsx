"use client";

import { useTheme } from "next-themes";
import { Moon, Sun } from "lucide-react";
import { useEffect, useRef, useState } from "react";
import { flushSync } from "react-dom";
import { cn } from "@/lib/utils";

// View Transition API 的返回对象（补上用到的成员，避免依赖 lib 版本差异）
type ViewTransitionLike = {
  ready: Promise<void>;
  finished: Promise<void>;
  skipTransition?: () => void;
};

type ViewTransitionDocument = Document & {
  startViewTransition?: (callback: () => void) => ViewTransitionLike;
};

// 动画时长（毫秒）
const DURATION = 450;

export function ThemeToggle({ className }: { className?: string }) {
  const { setTheme, resolvedTheme } = useTheme();
  const [mounted, setMounted] = useState(false);
  // 当前正在进行的视图过渡：用来处理"上一次还没播完就又点了一次"
  const transitionRef = useRef<ViewTransitionLike | null>(null);

  useEffect(() => {
    setMounted(true);
    return () => {
      // 卸载时把可能还在跑的过渡结束掉，避免残留动画影响下次挂载
      transitionRef.current?.skipTransition?.();
      transitionRef.current = null;
    };
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

  // 深色→浅色：浅色圆形从按钮处展开到屏幕最远端（动画作用在新快照上）
  // 浅色→深色：浅色圆形从最远端收缩到按钮处（动画作用在旧快照上）
  // 两个方向都是"浅色在动"，所以下面按方向决定动画挂在哪张快照上。
  const toggleTheme = (event: React.MouseEvent<HTMLButtonElement>) => {
    const nextTheme = isDark ? "light" : "dark";
    const doc = document as ViewTransitionDocument;
    const root = document.documentElement;

    // 浏览器不支持 View Transition，或用户开了"减少动态效果" → 直接切换，不做动画
    const prefersReducedMotion =
      typeof window.matchMedia === "function" &&
      window.matchMedia("(prefers-reduced-motion: reduce)").matches;
    if (!doc.startViewTransition || prefersReducedMotion) {
      setTheme(nextTheme);
      return;
    }

    // 上一次过渡还在跑就再次点击：先主动结束它。
    // 否则浏览器会"跳过"旧过渡，旧过渡的 ready 被 reject、动画中途消失，
    // 表现就是"有时有动画、有时没有"。
    transitionRef.current?.skipTransition?.();
    transitionRef.current = null;

    // 圆心取按钮中心。用 getBoundingClientRect 而不是 event.clientX/Y：
    // 键盘（Enter/Space）触发时 clientX/Y 都是 0，会从屏幕左上角开始扩散。
    const rect = event.currentTarget.getBoundingClientRect();
    const x = rect.left + rect.width / 2;
    const y = rect.top + rect.height / 2;
    const radius = Math.hypot(
      Math.max(x, window.innerWidth - x),
      Math.max(y, window.innerHeight - y)
    );

    const toDark = nextTheme === "dark";

    // 收缩方向：上层是"新"（深色）快照，必须把它整段压透明，
    // 才能露出下层"旧"的浅色快照、看到收缩效果。
    // 这里用静态样式（CSS 变量）而不是第二段 JS 动画——少一个可能失效的环节，
    // 只要过渡能跑起来，透明就是一定生效的。
    if (toDark) root.style.setProperty("--vt-new-opacity", "0");

    const transition = doc.startViewTransition(() => {
      flushSync(() => setTheme(nextTheme));
    });
    transitionRef.current = transition;

    // 本次过渡挂上去的动画。过渡结束后必须显式 cancel()：
    // fill:"forwards" 的动画不会随伪元素销毁而释放，会残留下来继续作用在
    // 下一次过渡的伪元素上。旧实现正是被上一次残留的 `opacity: [0,0]` 动画
    // 把"新"快照一直压成透明，导致反方向切换时整个动画看不见。
    let animation: Animation | null = null;

    const cleanup = () => {
      // 已经被后续过渡接管时不要动全局状态，否则会把新过渡的变量/动画清掉
      if (transitionRef.current !== transition) return;
      animation?.cancel();
      animation = null;
      root.style.removeProperty("--vt-new-opacity");
      transitionRef.current = null;
    };
    // finished 在过渡被跳过时会 reject，成功/失败都要清理
    transition.finished.then(cleanup, cleanup);

    transition.ready
      .then(() => {
        const target = toDark
          ? "::view-transition-old(root)"
          : "::view-transition-new(root)";
        const from = `circle(${toDark ? radius : 0}px at ${x}px ${y}px)`;
        const to = `circle(${toDark ? 0 : radius}px at ${x}px ${y}px)`;

        animation = root.animate(
          { clipPath: [from, to] },
          {
            duration: DURATION,
            easing: "ease-in-out",
            fill: "forwards",
            pseudoElement: target,
          }
        );
      })
      .catch(() => {
        // 过渡被跳过（例如连续快速点击）时 ready 会 reject：
        // 主题已经切好了，只是这次没播动画，无需额外处理。
      });
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
