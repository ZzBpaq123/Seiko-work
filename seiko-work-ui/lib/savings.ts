import { request } from "@/lib/axios";

export interface SavingsBucket {
  name: string;
  /** 整数百分比，各档合计必须为 100 */
  percent: number;
  /** 是否储蓄档：非储蓄档的金额合计会被当作本月可用预算，与实际支出对比 */
  saving: boolean;
}

export interface SavingsGoal {
  id: number;
  name: string;
  buckets: SavingsBucket[];
  /** 每月发薪日 1-31；null 表示未配置，统计回落到自然月 */
  salaryDay: number | null;
  createTime: string;
  updateTime: string;
}

export interface SavingsGoalParams {
  name?: string | null;
  buckets: SavingsBucket[];
  /** 传 null 表示清空发薪日配置 */
  salaryDay?: number | null;
}

/** 默认分配：储蓄 70% / 日常开销 20% / 心愿 10% */
export const DEFAULT_BUCKETS: SavingsBucket[] = [
  { name: "储蓄", percent: 70, saving: true },
  { name: "日常开销", percent: 20, saving: false },
  { name: "心愿", percent: 10, saving: false },
];

export const DEFAULT_GOAL_NAME = "每月工资分配";

/**
 * 默认发薪日：1 号。发薪日必填（不允许为空），默认值就是它。
 * 1 号的发薪周期（本月 1 日至今）与「自然月」口径完全等价，
 * 所以把它当默认值不会改变任何测算结果。
 */
export const DEFAULT_SALARY_DAY = 1;

/** 未配置时返回 null */
export function getSavingsGoal() {
  return request<SavingsGoal | null>({ method: "GET", url: "/api/savings-goal" });
}

export function saveSavingsGoal(params: SavingsGoalParams) {
  return request<void>({ method: "PUT", url: "/api/savings-goal", data: params });
}

export interface SalaryCycle {
  /** 周期开始日 = 最近一次发薪日（含当天） */
  start: Date;
  /** 周期结束日 = 今天（含当天） */
  end: Date;
  /**
   * 发薪日必填，正常情况下恒为 true。
   * false 只在发薪日非法/为空（输入框还没填完的中间态）时出现，此时按自然月兜底统计。
   */
  isSalaryCycle: boolean;
}

/** 解析发薪日：空、非数字、超出 1-31 都返回 null（＝非法，调用方据此报错）；小数四舍五入到整数 */
export function parseSalaryDay(raw: string | number | null | undefined): number | null {
  if (raw === null || raw === undefined || raw === "") return null;
  const value = Number(raw);
  if (!Number.isFinite(value)) return null;
  const day = Math.round(value);
  return day >= 1 && day <= 31 ? day : null;
}

/**
 * 某月发薪日：31 号遇到小月落到月末（2 月就是 28/29 号）。
 * 不能直接 new Date(y, m, 31)，那会溢到下个月 3 号，周期起点就错了一个月。
 */
function paydayOfMonth(year: number, month: number, salaryDay: number) {
  const lastDay = new Date(year, month + 1, 0).getDate();
  return new Date(year, month, Math.min(salaryDay, lastDay));
}

/**
 * 发薪周期 = [最近一次发薪日, 今天]。
 * 工资到账后才是「本轮可分配的钱」，所以起点取上一次发薪日而不是月初；
 * 今天还没到发薪日时，起点回到上个月的发薪日。
 */
export function salaryCycleRange(
  salaryDay: number | null | undefined,
  today: Date = new Date()
): SalaryCycle {
  const day = parseSalaryDay(salaryDay ?? null);
  if (day === null) {
    return {
      start: new Date(today.getFullYear(), today.getMonth(), 1),
      end: today,
      isSalaryCycle: false,
    };
  }
  const thisMonthPayday = paydayOfMonth(today.getFullYear(), today.getMonth(), day);
  const start =
    today >= thisMonthPayday
      ? thisMonthPayday
      : paydayOfMonth(today.getFullYear(), today.getMonth() - 1, day);
  return { start, end: today, isSalaryCycle: true };
}
