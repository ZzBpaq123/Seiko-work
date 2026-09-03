"use client";

import { CalendarDays, Home, LogIn, Mail } from "lucide-react";
import Link from "next/link";
import { usePathname } from "next/navigation";
import type { ComponentType } from "react";
import { cn } from "@/lib/utils";

type NavItem = {
  key: string;
  label: string;
  href: string;
  icon: ComponentType<{ className?: string }>;
};

const NAV_ITEMS: NavItem[] = [
  { key: "login", label: "登录", href: "#login", icon: LogIn },
  { key: "home", label: "首页", href: "/", icon: Home },
  { key: "mail", label: "邮箱", href: "#mail", icon: Mail },
  { key: "schedule", label: "日程", href: "#schedule", icon: CalendarDays },
];

function NavButton({ item, active }: { item: NavItem; active: boolean }) {
  const Icon = item.icon;
  return (
    <Link
      href={item.href}
      aria-label={item.label}
      className={cn(
        "group relative flex h-12 w-12 items-center justify-center rounded-full border backdrop-blur-sm transition-colors duration-200",
        active
          ? "border-neutral-900 bg-neutral-900 text-neutral-50"
          : "border-neutral-900/15 bg-white/70 text-neutral-700 hover:border-neutral-900 hover:bg-neutral-900 hover:text-neutral-50"
      )}
    >
      <Icon className="h-5 w-5" />
      <span
        className={cn(
          "pointer-events-none absolute right-full mr-3 whitespace-nowrap rounded-md border border-neutral-900/10 bg-white/90 px-2 py-1 text-xs text-neutral-800 opacity-0 shadow-sm backdrop-blur-sm transition-opacity duration-200 group-hover:opacity-100",
          active && "opacity-0 group-hover:opacity-100"
        )}
      >
        {item.label}
      </span>
    </Link>
  );
}

export function SideNav() {
  const pathname = usePathname();

  return (
    <nav
      aria-label="功能导航"
      className="fixed right-6 top-1/2 z-10 flex -translate-y-1/2 flex-col items-center gap-3"
    >
      {NAV_ITEMS.map((item) => (
        <NavButton
          key={item.key}
          item={item}
          active={item.href !== "#" && pathname === item.href}
        />
      ))}
    </nav>
  );
}
