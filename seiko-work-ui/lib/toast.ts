export type ToastType = "success" | "error" | "info";

export interface ToastItem {
  id: number;
  type: ToastType;
  message: string;
}

const DURATION = 3000;

let queue: ToastItem[] = [];
let timer: ReturnType<typeof setTimeout> | null = null;
let seq = 0;
const listeners = new Set<(queue: ToastItem[]) => void>();

function emit() {
  const snapshot = [...queue];
  listeners.forEach((listener) => listener(snapshot));
}

function startTimer() {
  if (timer) clearTimeout(timer);
  const front = queue[0];
  if (!front) return;
  timer = setTimeout(() => dismiss(front.id), DURATION);
}

function dismiss(id: number) {
  const index = queue.findIndex((item) => item.id === id);
  if (index === -1) return;
  const wasFront = index === 0;
  queue = queue.filter((item) => item.id !== id);
  emit();
  // 队首消失后，新的队首重新计时 3s
  if (wasFront) startTimer();
}

function push(type: ToastType, message: string) {
  queue = [...queue, { id: ++seq, type, message }];
  emit();
  if (queue.length === 1) startTimer();
}

export const toast = Object.assign(
  (message: string, type: ToastType = "info") => push(type, message),
  {
    success: (message: string) => push("success", message),
    error: (message: string) => push("error", message),
    info: (message: string) => push("info", message),
  }
);

export function dismissToast(id: number) {
  dismiss(id);
}

export function subscribeToasts(listener: (queue: ToastItem[]) => void) {
  listeners.add(listener);
  listener([...queue]);
  return () => {
    listeners.delete(listener);
  };
}
