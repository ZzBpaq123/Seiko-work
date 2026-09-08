"use client";

import { useEffect, useRef, useState } from "react";
import { CalendarDays, ChevronLeft, ChevronRight } from "lucide-react";
import { cn } from "@/lib/utils";

export interface DateTimeRange {
  startDate: string; // yyyy-MM-dd
  endDate: string; // yyyy-MM-dd
  isAllDay: boolean;
  startTime: string; // HH:mm
  endTime: string; // HH:mm
}

const WEEKDAYS = ["一", "二", "三", "四", "五", "六", "日"];

function fmtDate(d: Date) {
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-${String(d.getDate()).padStart(2, "0")}`;
}

function parseDate(s: string) {
  const [y, m, d] = s.split("-").map(Number);
  return new Date(y, m - 1, d);
}

function addDays(d: Date, n: number) {
  const next = new Date(d);
  next.setDate(next.getDate() + n);
  return next;
}

// 快捷选项：今天 / 明天 / 本周 / 下周 / 本月
function shortcuts() {
  const now = new Date();
  const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
  const monday = addDays(today, -((today.getDay() + 6) % 7));
  const monthStart = new Date(today.getFullYear(), today.getMonth(), 1);
  const monthEnd = new Date(today.getFullYear(), today.getMonth() + 1, 0);
  return [
    { label: "今天", range: [today, today] as const },
    { label: "明天", range: [addDays(today, 1), addDays(today, 1)] as const },
    { label: "本周", range: [monday, addDays(monday, 6)] as const },
    { label: "下周", range: [addDays(monday, 7), addDays(monday, 13)] as const },
    { label: "本月", range: [monthStart, monthEnd] as const },
  ];
}

function MonthGrid({
  view,
  start,
  end,
  pendingStart,
  today,
  onPick,
}: {
  view: Date; // 仅用到年月
  start: string;
  end: string;
  pendingStart: string | null;
  today: string;
  onPick: (d: Date) => void;
}) {
  const first = new Date(view.getFullYear(), view.getMonth(), 1);
  const offset = (first.getDay() + 6) % 7; // 周一起始
  const cells = Array.from({ length: 42 }, (_, i) => addDays(first, i - offset));
  return (
    <div className="min-w-0 flex-1">
      <div className="mb-1 text-center text-xs font-medium text-neutral-700">
        {view.getFullYear()}年{view.getMonth() + 1}月
      </div>
      <div className="mb-0.5 grid grid-cols-7">
        {WEEKDAYS.map((w) => (
          <span key={w} className="text-center text-[10px] text-neutral-400">
            {w}
          </span>
        ))}
      </div>
      <div className="grid grid-cols-7 gap-y-0.5">
        {cells.map((d) => {
          const dateStr = fmtDate(d);
          const inMonth = d.getMonth() === view.getMonth();
          const edge = dateStr === start || dateStr === end || dateStr === pendingStart;
          const inRange = start && end && start < dateStr && dateStr < end;
          return (
            <button
              key={dateStr}
              type="button"
              onClick={() => onPick(d)}
              className={cn(
                "flex h-6 cursor-pointer items-center justify-center rounded text-[11px] transition-colors select-none",
                inMonth ? "text-neutral-700" : "text-neutral-300",
                !edge && "hover:bg-neutral-900/10",
                inRange && "bg-neutral-900/5",
                edge && "bg-neutral-900 text-white hover:bg-neutral-700",
                dateStr === today && !edge && "font-semibold ring-1 ring-neutral-900/30"
              )}
            >
              {d.getDate()}
            </button>
          );
        })}
      </div>
    </div>
  );
}

/**
 * 日期时间范围选择（仿 Element Plus daterange）：
 * 触发框展示当前范围，弹层内含快捷选项与双月历，点两次确定起止日期；
 * 非全天时可在弹层底部选择起止时间
 */
export function DateTimeRangePicker({
  value,
  onChange,
}: {
  value: DateTimeRange;
  onChange: (next: DateTimeRange) => void;
}) {
  const [open, setOpen] = useState(false);
  const rootRef = useRef<HTMLDivElement>(null);
  const today = fmtDate(new Date());
  const [view, setView] = useState(() => {
    const base = value.startDate ? parseDate(value.startDate) : new Date();
    return new Date(base.getFullYear(), base.getMonth(), 1);
  });
  // 已点选开始、等待点选结束的状态
  const [pendingStart, setPendingStart] = useState<string | null>(null);

  useEffect(() => {
    if (!open) return;
    const onDown = (e: MouseEvent) => {
      if (rootRef.current && !rootRef.current.contains(e.target as Node)) setOpen(false);
    };
    document.addEventListener("mousedown", onDown);
    return () => document.removeEventListener("mousedown", onDown);
  }, [open]);

  const patch = (p: Partial<DateTimeRange>) => onChange({ ...value, ...p });

  const pick = (d: Date) => {
    const dateStr = fmtDate(d);
    if (!pendingStart) {
      setPendingStart(dateStr);
      patch({ startDate: dateStr, endDate: dateStr });
    } else {
      const [start, end] = pendingStart <= dateStr ? [pendingStart, dateStr] : [dateStr, pendingStart];
      patch({ startDate: start, endDate: end });
      setPendingStart(null);
      setOpen(false);
    }
  };

  const applyShortcut = (range: readonly [Date, Date]) => {
    patch({ startDate: fmtDate(range[0]), endDate: fmtDate(range[1]) });
    setView(new Date(range[0].getFullYear(), range[0].getMonth(), 1));
    setPendingStart(null);
    setOpen(false);
  };

  const shiftMonth = (n: number) =>
    setView((v) => new Date(v.getFullYear(), v.getMonth() + n, 1));

  const nextView = new Date(view.getFullYear(), view.getMonth() + 1, 1);

  const text =
    value.startDate && value.endDate
      ? value.isAllDay
        ? `${value.startDate} 至 ${value.endDate}`
        : `${value.startDate} ${value.startTime} 至 ${value.endDate} ${value.endTime}`
      : "选择日期范围";

  return (
    <div ref={rootRef} className="relative">
      <button
        type="button"
        onClick={() => setOpen((v) => !v)}
        className={cn(
          "flex w-full items-center gap-2 rounded-lg border border-neutral-900/15 bg-white/80 px-3 py-2 text-sm outline-none transition-colors",
          "hover:border-neutral-900/40 focus:border-neutral-900",
          !value.startDate && "text-neutral-400"
        )}
      >
        <CalendarDays className="h-4 w-4 shrink-0 text-neutral-400" />
        <span className="min-w-0 flex-1 truncate text-left">{text}</span>
      </button>

      {open && (
        <div className="absolute left-1/2 top-full z-30 mt-1 w-[21rem] -translate-x-1/2 rounded-xl border border-neutral-900/15 bg-white/95 p-3 shadow-lg">
          {/* 快捷选项 */}
          <div className="flex flex-wrap gap-1.5">
            {shortcuts().map((s) => (
              <button
                key={s.label}
                type="button"
                onClick={() => applyShortcut(s.range)}
                className="rounded-md border border-neutral-900/10 px-2 py-1 text-xs text-neutral-600 transition-colors hover:border-neutral-900 hover:text-neutral-900"
              >
                {s.label}
              </button>
            ))}
          </div>

          {/* 双月历 */}
          <div className="relative mt-2">
            <button
              type="button"
              aria-label="上个月"
              onClick={() => shiftMonth(-1)}
              className="absolute top-0 left-0 rounded-full p-1 text-neutral-400 transition-colors hover:bg-neutral-900/5 hover:text-neutral-900"
            >
              <ChevronLeft className="h-4 w-4" />
            </button>
            <button
              type="button"
              aria-label="下个月"
              onClick={() => shiftMonth(1)}
              className="absolute top-0 right-0 rounded-full p-1 text-neutral-400 transition-colors hover:bg-neutral-900/5 hover:text-neutral-900"
            >
              <ChevronRight className="h-4 w-4" />
            </button>
            <div className="flex gap-3">
              <MonthGrid
                view={view}
                start={value.startDate}
                end={value.endDate}
                pendingStart={pendingStart}
                today={today}
                onPick={pick}
              />
              <MonthGrid
                view={nextView}
                start={value.startDate}
                end={value.endDate}
                pendingStart={pendingStart}
                today={today}
                onPick={pick}
              />
            </div>
          </div>

          {/* 全天 / 时间 */}
          <div className="mt-2 flex items-center justify-between gap-2 border-t border-neutral-900/10 pt-2">
            <label className="flex cursor-pointer items-center gap-1.5 text-xs text-neutral-700">
              <input
                type="checkbox"
                checked={value.isAllDay}
                onChange={(e) => patch({ isAllDay: e.target.checked })}
                className="h-3.5 w-3.5 accent-neutral-900"
              />
              全天
            </label>
            {!value.isAllDay && (
              <div className="flex items-center gap-1.5">
                <input
                  type="time"
                  value={value.startTime}
                  onChange={(e) => patch({ startTime: e.target.value })}
                  className="w-[6.5rem] rounded-md border border-neutral-900/15 bg-white/80 px-2 py-1 text-xs text-neutral-900 outline-none transition-colors focus:border-neutral-900"
                />
                <span className="text-[10px] text-neutral-400">至</span>
                <input
                  type="time"
                  value={value.endTime}
                  onChange={(e) => patch({ endTime: e.target.value })}
                  className="w-[6.5rem] rounded-md border border-neutral-900/15 bg-white/80 px-2 py-1 text-xs text-neutral-900 outline-none transition-colors focus:border-neutral-900"
                />
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
