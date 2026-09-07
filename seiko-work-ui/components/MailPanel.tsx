"use client";

import { useCallback, useEffect, useMemo, useState } from "react";
import { Inbox, Loader2, MailX, Paperclip, RotateCw, X } from "lucide-react";
import { cn } from "@/lib/utils";
import { clearHash, useHash } from "@/hooks/useHash";
import { PanelReveal } from "@/components/PanelReveal";
import {
  getMailDetail,
  listMails,
  markMailRead,
  type MailMessage,
} from "@/lib/mail";
import { toast } from "@/lib/toast";

function formatTime(value: string | null) {
  if (!value) return "";
  const date = new Date(value.replace(" ", "T"));
  if (Number.isNaN(date.getTime())) return value;
  const now = new Date();
  const sameDay = (a: Date, b: Date) =>
    a.getFullYear() === b.getFullYear() && a.getMonth() === b.getMonth() && a.getDate() === b.getDate();
  const hhmm = `${String(date.getHours()).padStart(2, "0")}:${String(date.getMinutes()).padStart(2, "0")}`;
  const yesterday = new Date(now);
  yesterday.setDate(now.getDate() - 1);
  if (sameDay(date, now)) return hhmm;
  if (sameDay(date, yesterday)) return `昨天 ${hhmm}`;
  if (date.getFullYear() === now.getFullYear()) return `${date.getMonth() + 1}月${date.getDate()}日`;
  return `${date.getFullYear()}/${date.getMonth() + 1}/${date.getDate()}`;
}

function htmlToText(html: string) {
  return html
    .replace(/<style[\s\S]*?<\/style>/gi, "")
    .replace(/<script[\s\S]*?<\/script>/gi, "")
    .replace(/<br\s*\/?>/gi, "\n")
    .replace(/<\/(p|div|li|tr|h[1-6])>/gi, "\n")
    .replace(/<[^>]+>/g, "")
    .replace(/&nbsp;/g, " ")
    .replace(/&lt;/g, "<")
    .replace(/&gt;/g, ">")
    .replace(/&quot;/g, '"')
    .replace(/&#39;/g, "'")
    .replace(/&amp;/g, "&")
    .trim();
}

function failMessage(e: unknown, fallback: string) {
  return e instanceof Error ? e.message : fallback;
}

export function MailPanel() {
  const [mails, setMails] = useState<MailMessage[]>([]);
  const [selectedUid, setSelectedUid] = useState<string | null>(null);
  const [detail, setDetail] = useState<MailMessage | null>(null);
  const [loading, setLoading] = useState(false);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [detailLoading, setDetailLoading] = useState(false);

  const open = useHash() === "#mail";

  const load = useCallback(async () => {
    setLoading(true);
    setLoadError(null);
    try {
      const list = await listMails();
      setMails(list);
    } catch (err) {
      setLoadError(failMessage(err, "加载邮件失败"));
      setMails([]);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    if (!open) return;
    setSelectedUid(null);
    setDetail(null);
    load();
  }, [open, load]);

  useEffect(() => {
    const onKey = (e: KeyboardEvent) => {
      if (e.key === "Escape") clearHash();
    };
    window.addEventListener("keydown", onKey);
    return () => window.removeEventListener("keydown", onKey);
  }, []);

  const unreadCount = useMemo(() => mails.filter((m) => !m.isRead).length, [mails]);
  const selected = mails.find((m) => m.messageUid === selectedUid) ?? null;

  const selectMail = (mail: MailMessage) => {
    setSelectedUid(mail.messageUid);
    setDetail(null);
    setDetailLoading(true);
    // 本地立即置为已读，服务端同步失败不影响展示
    setMails((prev) =>
      prev.map((m) => (m.messageUid === mail.messageUid ? { ...m, isRead: true } : m))
    );
    markMailRead(mail.messageUid).catch(() => {});
    getMailDetail(mail.messageUid)
      .then(setDetail)
      .catch((err) => toast.error(failMessage(err, "加载邮件详情失败")))
      .finally(() => setDetailLoading(false));
  };

  const displayDetail = detail ?? null;
  const paragraphs = useMemo(() => {
    if (!displayDetail) return [];
    const text =
      displayDetail.contentText ??
      (displayDetail.contentHtml ? htmlToText(displayDetail.contentHtml) : "");
    return text.split(/\n+/).map((s) => s.trim()).filter(Boolean);
  }, [displayDetail]);

  return (
    <PanelReveal open={open}>
    <div className="pointer-events-auto fixed bottom-6 left-6 right-24 top-24 z-10 flex gap-4">
      {/* 左：邮件列表 */}
      <aside className="flex w-85 shrink-0 flex-col overflow-hidden rounded-2xl border border-neutral-900/15 bg-white/20 shadow-sm">
        <div className="flex items-center justify-between border-b border-neutral-900/10 px-5 py-4">
          <div className="flex items-center gap-2 text-neutral-900">
            <Inbox className="h-4 w-4" />
            <span className="text-sm font-semibold">收件箱</span>
            {!loading && !loadError && (
              <span className="rounded-full bg-neutral-900/5 px-2 py-0.5 text-xs text-neutral-600">
                {unreadCount} 封未读
              </span>
            )}
          </div>
          <button
            onClick={load}
            disabled={loading}
            aria-label="刷新"
            className="rounded-full p-1.5 text-neutral-500 transition-colors hover:bg-neutral-900/5 hover:text-neutral-900 disabled:opacity-50"
          >
            <RotateCw className={cn("h-4 w-4", loading && "animate-spin")} />
          </button>
        </div>
        <div className="flex-1 overflow-y-auto scrollbar-none [&::-webkit-scrollbar]:hidden">
          {loading ? (
            <div className="flex items-center justify-center gap-2 py-16 text-sm text-neutral-500">
              <Loader2 className="h-4 w-4 animate-spin" />
              正在收取邮件…
            </div>
          ) : loadError ? (
            <div className="flex flex-col items-center gap-3 py-16 text-sm text-neutral-500">
              <MailX className="h-6 w-6 text-neutral-400" />
              <p className="max-w-55 text-center text-xs leading-5">{loadError}</p>
              <button
                onClick={load}
                className="rounded-lg border border-neutral-900/15 bg-white/60 px-3 py-1.5 text-xs text-neutral-700 transition-colors hover:border-neutral-900"
              >
                重试
              </button>
            </div>
          ) : mails.length === 0 ? (
            <div className="flex items-center justify-center gap-2 py-16 text-sm text-neutral-400">
              收件箱为空
            </div>
          ) : (
            mails.map((mail) => (
              <button
                key={mail.messageUid}
                onClick={() => selectMail(mail)}
                className={cn(
                  "group relative flex w-full items-start gap-3 border-b border-neutral-900/5 px-4 py-3 text-left transition-colors",
                  mail.messageUid === selectedUid ? "bg-neutral-900/5" : "hover:bg-neutral-900/3"
                )}
              >
                {/* 四角断开式角标：仅悬停时显示，四边中段留空 */}
                <span className="pointer-events-none absolute left-1 top-1 h-3 w-3 border-l border-t border-neutral-900/50 opacity-0 transition-opacity duration-200 group-hover:opacity-100" />
                <span className="pointer-events-none absolute right-1 top-1 h-3 w-3 border-r border-t border-neutral-900/50 opacity-0 transition-opacity duration-200 group-hover:opacity-100" />
                <span className="pointer-events-none absolute bottom-1 left-1 h-3 w-3 border-b border-l border-neutral-900/50 opacity-0 transition-opacity duration-200 group-hover:opacity-100" />
                <span className="pointer-events-none absolute bottom-1 right-1 h-3 w-3 border-b border-r border-neutral-900/50 opacity-0 transition-opacity duration-200 group-hover:opacity-100" />
                {/* 选中项：实体线段沿边框周长循环跑动（offset-path 运动路径） */}
                {mail.messageUid === selectedUid && (
                  <span aria-hidden className="mail-runner" />
                )}
                <span className="mt-0.5 flex h-9 w-9 shrink-0 items-center justify-center rounded-full bg-neutral-900/10 text-sm font-medium text-neutral-700">
                  {(mail.fromName || mail.fromAddress || "?").slice(0, 1)}
                </span>
                <span className="min-w-0 flex-1">
                  <span className="flex items-baseline justify-between gap-2">
                    <span
                      className={cn(
                        "truncate text-sm",
                        !mail.isRead ? "font-semibold text-neutral-900" : "text-neutral-700"
                      )}
                    >
                      {mail.fromName || mail.fromAddress || "未知发件人"}
                    </span>
                    <span className="shrink-0 text-xs text-neutral-400">
                      {formatTime(mail.receiveTime)}
                    </span>
                  </span>
                  <span
                    className={cn(
                      "mt-0.5 block truncate text-sm",
                      !mail.isRead ? "font-medium text-neutral-900" : "text-neutral-600"
                    )}
                  >
                    {mail.subject || "（无主题）"}
                  </span>
                  <span className="mt-0.5 flex items-center gap-1 truncate text-xs text-neutral-400">
                    {mail.hasAttachment && <Paperclip className="h-3 w-3 shrink-0" />}
                    <span className="truncate">{mail.fromAddress || ""}</span>
                  </span>
                </span>
                {!mail.isRead && <span className="mt-2 h-2 w-2 shrink-0 rounded-full bg-sky-500" />}
              </button>
            ))
          )}
        </div>
      </aside>

      {/* 右：邮件详情 */}
      <section className="flex min-w-0 flex-1 flex-col overflow-hidden rounded-2xl border border-neutral-900/15 bg-white/20 shadow-sm">
        {detailLoading ? (
          <div className="flex flex-1 items-center justify-center gap-2 text-sm text-neutral-500">
            <Loader2 className="h-4 w-4 animate-spin" />
            正在加载邮件内容…
          </div>
        ) : displayDetail && selected ? (
          <>
            <div className="flex items-start justify-between gap-4 border-b border-neutral-900/10 px-6 py-4">
              <h2 className="text-lg font-semibold leading-snug text-neutral-900">
                {displayDetail.subject || "（无主题）"}
              </h2>
              <button
                onClick={clearHash}
                aria-label="关闭"
                className="shrink-0 rounded-full p-1.5 text-neutral-500 transition-colors hover:bg-neutral-900/5 hover:text-neutral-900"
              >
                <X className="h-5 w-5" />
              </button>
            </div>
            <div className="flex items-center gap-3 border-b border-neutral-900/10 px-6 py-3">
              <span className="flex h-10 w-10 items-center justify-center rounded-full bg-neutral-900/10 text-sm font-medium text-neutral-700">
                {(displayDetail.fromName || displayDetail.fromAddress || "?").slice(0, 1)}
              </span>
              <div className="min-w-0">
                <p className="text-sm font-medium text-neutral-900">
                  {displayDetail.fromName || displayDetail.fromAddress || "未知发件人"}
                </p>
                <p className="truncate text-xs text-neutral-400">
                  &lt;{displayDetail.fromAddress || "unknown"}&gt;
                </p>
              </div>
              <span className="ml-auto shrink-0 text-xs text-neutral-400">
                {formatTime(displayDetail.receiveTime)}
              </span>
            </div>
            <div className="flex-1 overflow-y-auto px-6 py-5">
              {paragraphs.length > 0 ? (
                paragraphs.map((paragraph, i) => (
                  <p key={i} className="mb-3 text-sm leading-7 text-neutral-700">
                    {paragraph}
                  </p>
                ))
              ) : (
                <p className="text-sm text-neutral-400">（无正文内容）</p>
              )}
            </div>
          </>
        ) : (
          <div className="flex flex-1 items-center justify-center text-sm text-neutral-400">
            {selected ? "加载邮件内容失败" : "选择一封邮件查看内容"}
          </div>
        )}
      </section>
    </div>
    </PanelReveal>
  );
}
