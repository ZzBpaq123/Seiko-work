import { request } from "@/lib/axios";

export interface HolidayInfo {
  date: string;
  type: number; // 0-工作日 1-周末 2-节日 3-调休放假 4-补班
  isHoliday: number; // 1=放假（周末/法定节假日/调休），0=工作日（含补班）
  name: string;
  wage: number | null;
  after: number | null; // 1-假后调休/补班，0-假前，否则 null
  week: number; // 1-周一 … 7-周日
  target: string | null; // 调休的节假日名称
  lunar: string;
  extraInfo: string;
  nameEn: string | null;
  extraEn: string | null;
}

const cache = new Map<number, Promise<Record<string, HolidayInfo>>>();

// 节假日由后端 /api/holidays/{year} 代理（第三方 API 不支持浏览器跨域），按年缓存，失败静默降级
export function fetchHolidays(year: number): Promise<Record<string, HolidayInfo>> {
  let cached = cache.get(year);
  if (!cached) {
    cached = request<HolidayInfo[]>({ method: "GET", url: `/api/holidays/${year}` })
      .then((list) => Object.fromEntries(list.map((h) => [h.date, h])))
      .catch(() => {
        cache.delete(year);
        return {};
      });
    cache.set(year, cached);
  }
  return cached;
}
