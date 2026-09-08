import { request } from "@/lib/axios";

export interface CalendarEvent {
  id: number;
  userId: number;
  title: string;
  startTime: string;
  endTime: string;
  isAllDay: number;
  location: string | null;
  remark: string | null;
}

export interface CalendarEventParams {
  title: string;
  startDate: string; // yyyy-MM-dd
  endDate: string; // yyyy-MM-dd
}

function toDateTime(date: string, time: string) {
  return `${date} ${time}`;
}

export function listEventsByRange(start: string, end: string) {
  return request<CalendarEvent[]>({
    method: "GET",
    url: "/api/calendar-events/range",
    params: { start: toDateTime(start, "00:00:00"), end: toDateTime(end, "23:59:59") },
  });
}

export function createEvent(params: CalendarEventParams) {
  return request<void>({
    method: "POST",
    url: "/api/calendar-events",
    data: {
      title: params.title,
      startTime: toDateTime(params.startDate, "00:00:00"),
      endTime: toDateTime(params.endDate, "23:59:59"),
      isAllDay: 1,
    },
  });
}

export function updateEvent(id: number, params: CalendarEventParams) {
  return request<void>({
    method: "PUT",
    url: `/api/calendar-events/${id}`,
    data: {
      title: params.title,
      startTime: toDateTime(params.startDate, "00:00:00"),
      endTime: toDateTime(params.endDate, "23:59:59"),
      isAllDay: 1,
    },
  });
}

export function deleteEvent(id: number) {
  return request<void>({ method: "DELETE", url: `/api/calendar-events/${id}` });
}
