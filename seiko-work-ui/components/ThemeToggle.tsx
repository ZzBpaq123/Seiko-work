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

type ThemeName = "light" | "dark";

// 圆形展开动画时长（毫秒）
const DURATION = 450;

// 挂在 <html> 上的标记类：只有浏览器真正支持「给伪元素挂动画」时才加。
// globals.css 里 .vt-clip::view-transition-*(root) 才会关掉默认的交叉淡入淡出，
// 改由下面 JS 挂的 clip-path 圆形展开动画驱动。
// 不支持的浏览器（会忽略 animate 的 pseudoElement 选项）就不加这个类，
// 让它们退回到默认的交叉淡入淡出——总好过把 clip-path 误作用到 <html> 上。
const VT_CLIP_CLASS = "vt-clip";

/**
 * 探测 Element.animate 是否真的支持 pseudoElement 选项。
 * 支持的浏览器里动画的 effect.pseudoElement 会等于传入的伪元素字符串；
 * 不支持的浏览器会忽略该选项（pseudoElement 为空），此时动画会落到 <html> 本身，
 * 必须放弃这条路径。
 */
function supportsPseudoElementAnimation(): boolean {
  if (typeof document === "undefined") return false;
  try {
    const probe = document.documentElement.animate(
      { opacity: [1, 1] },
      { duration: 0, pseudoElement: "::view-transition-new(root)" }
    );
    // pseudoElement 声明在 KeyframeEffect 上，而不是 AnimationEffect 基类
    const ok = !!(probe.effect as KeyframeEffect | null)?.pseudoElement;
    probe.cancel();
    return ok;
  } catch {
    return false;
  }
}

export function ThemeToggle({ className }: { className?: string }) {
  const { setTheme, resolvedTheme } = useTheme();
  const [mounted, setMounted] = useState(false);
  // 当前正在进行的视图过渡：用来处理「上一次还没播完就又点了一次」
  const transitionRef = useRef<ViewTransitionLike | null>(null);
  // 上一次「命令」的目标主题。
  // 不能直接用 resolvedTheme：它来自 next-themes 的 state，过渡播放期间可能还没刷新，
  // 会导致连点两次时第二次算出的目标主题和第一次相同、被静默吞掉（表现为「点了没反应」）。
  const commandedThemeRef = useRef<ThemeName | null>(null);

  useEffect(() => {
    setMounted(true);
    // 只在支持伪元素动画时才关掉默认的交叉淡入淡出
    if (supportsPseudoElementAnimation()) {
      document.documentElement.classList.add(VT_CLIP_CLASS);
    }
    return () => {
      // 卸载时把可能还在跑的过渡结束掉，避免残留动画影响下次挂载
      transitionRef.current?.skipTransition?.();
      transitionRef.current = null;
      document.documentElement.classList.remove(VT_CLIP_CLASS);
    };
  }, []);

  // 外部来源（系统主题变化、其它组件调用 setTheme）改动时同步「命令目标」；
  // 但过渡播放期间 resolvedTheme 可能滞后，此时不要覆盖我们刚下达的命令。
  useEffect(() => {
    if (transitionRef.current) return;
    if (resolvedTheme === "light" || resolvedTheme === "dark") {
      commandedThemeRef.current = resolvedTheme;
    }
  }, [resolvedTheme]);

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

  // 两个方向都是「新主题从按钮处呈圆形展开」：
  // 深色 -> 浅色是浅色展开，浅色 -> 深色是深色展开，圆心始终是按钮中心。
  // 实现上只动画 ::view-transition-new(root)（它盖在旧快照之上），
  // 所以不需要再去压透明任何一张快照——少一个会残留、会失效的环节。
  const toggleTheme = (event: React.MouseEvent<HTMLButtonElement>) => {
    const root = document.documentElement;
    const doc = document as ViewTransitionDocument;

    // 以「上一次命令」为准推算下一个主题，保证连点一定在明暗之间来回切
    const current: ThemeName =
      commandedThemeRef.current ?? (isDark ? "dark" : "light");
    const nextTheme: ThemeName = current === "dark" ? "light" : "dark";
    commandedThemeRef.current = nextTheme;

    // 浏览器不支持 View Transition，或用户开了「减少动态效果」→ 直接切换，不做动画
    const prefersReducedMotion =
      typeof window.matchMedia === "function" &&
      window.matchMedia("(prefers-reduced-motion: reduce)").matches;
    if (!doc.startViewTransition || prefersReducedMotion) {
      setTheme(nextTheme);
      return;
    }

    // 上一次过渡还在跑就再次点击：先主动结束它。
    // 否则浏览器会「跳过」旧过渡，旧过渡的 ready 被 reject、动画中途消失，
    // 表现就是「有时有动画、有时没有」。
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

    const transition = doc.startViewTransition(() => {
      flushSync(() => setTheme(nextTheme));
    });
    transitionRef.current = transition;

    // 本次过渡挂上去的动画。过渡结束后必须显式 cancel()：
    // fill:"forwards" 的动画不会随伪元素销毁而释放，会残留下来继续作用在
    // 下一次过渡的伪元素上（历史上正是残留动画把后续过渡整个压没了）。
    let animation: Animation | null = null;

    const cleanup = () => {
      // 动画无条件 cancel：即使本次过渡被后续过渡接管，它对应的伪元素已经销毁，
      // 残留的 forwards 动画只会污染下一次过渡，必须清掉。
      animation?.cancel();
      animation = null;
      // 但「当前过渡」的引用只有在没被接管时才能清空
      if (transitionRef.current === transition) transitionRef.current = null;
    };
    // finished 在过渡被跳过时会 reject，成功/失败都要清理
    transition.finished.then(cleanup, cleanup);

    transition.ready
      .then(() => {
        animation = root.animate(
          {
            clipPath: [
              `circle(0px at ${x}px ${y}px)`,
              `circle(${radius}px at ${x}px ${y}px)`,
            ],
          },
          {
            duration: DURATION,
            easing: "ease-in-out",
            fill: "forwards",
            pseudoElement: "::view-transition-new(root)",
          }
        );
      })
      .catch(() => {
        // 过渡被跳过（例如连续快速点击、页面不可见）时 ready 会 reject：
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
