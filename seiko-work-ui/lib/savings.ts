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
  createTime: string;
  updateTime: string;
}

export interface SavingsGoalParams {
  name?: string | null;
  buckets: SavingsBucket[];
}

/** 默认分配：储蓄 70% / 日常开销 20% / 心愿 10% */
export const DEFAULT_BUCKETS: SavingsBucket[] = [
  { name: "储蓄", percent: 70, saving: true },
  { name: "日常开销", percent: 20, saving: false },
  { name: "心愿", percent: 10, saving: false },
];

export const DEFAULT_GOAL_NAME = "每月工资分配";

/** 未配置时返回 null */
export function getSavingsGoal() {
  return request<SavingsGoal | null>({ method: "GET", url: "/api/savings-goal" });
}

export function saveSavingsGoal(params: SavingsGoalParams) {
  return request<void>({ method: "PUT", url: "/api/savings-goal", data: params });
}
