/** 日期工具：账单相关组件共用 */

export function fmtDate(d: Date) {
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-${String(d.getDate()).padStart(2, "0")}`;
}

/**
 * 手动解析 yyyy-MM-dd。
 * 不要用 new Date("2026-10-12")——那会按 UTC 解析，在东八区会差一天。
 */
export function parseDate(s: string) {
  const [y, m, d] = s.split("-").map(Number);
  return new Date(y, m - 1, d);
}

export function addDays(d: Date, n: number) {
  const next = new Date(d);
  next.setDate(next.getDate() + n);
  return next;
}

/** 金额展示：千分位 + 两位小数 */
export function fmtAmount(n: number) {
  return n.toLocaleString("zh-CN", { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}
