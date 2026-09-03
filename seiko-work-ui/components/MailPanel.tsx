"use client";

import { useEffect, useMemo, useState } from "react";
import { Inbox, X } from "lucide-react";
import { cn } from "@/lib/utils";
import { clearHash } from "@/hooks/useHash";

type Mail = {
  id: number;
  fromName: string;
  fromEmail: string;
  subject: string;
  snippet: string;
  body: string[];
  time: string;
  unread: boolean;
};

const MOCK_MAILS: Mail[] = [
  {
    id: 1,
    fromName: "张明",
    fromEmail: "zhangming@seiko.com",
    subject: "【评审通知】V2.3 版本需求评审会",
    snippet: "各位同事好，V2.3 版本的需求评审会定于本周四下午两点在三号会议室举行…",
    body: [
      "各位同事好：",
      "V2.3 版本的需求评审会定于本周四（9月10日）14:00 在三号会议室举行，届时请相关产品、设计、研发同学准时参加。",
      "请参会同学提前阅读需求文档（链接见 wiki），并准备好各自模块的排期评估。会议预计持续一个半小时。",
      "如有时间冲突无法参会，请提前与我联系。",
      "—— 张明 · 产品部",
    ],
    time: "09:42",
    unread: true,
  },
  {
    id: 2,
    fromName: "王莉",
    fromEmail: "hr-wangli@seiko.com",
    subject: "九月团建活动报名开始啦",
    snippet: "秋高气爽，九月团建正式开始报名！本次目的地：莫干山，活动时间…",
    body: [
      "各位小伙伴：",
      "秋高气爽，一年一度的秋季团建正式启动报名！本次活动定于 9月19日—9月20日，地点为莫干山，行程包含徒步、烧烤与篝火晚会。",
      "请有意参加的同学在本周五前回复本邮件报名，并注明是否有饮食禁忌。家属同行请在报名时一并说明。",
      "—— 人力资源部 王莉",
    ],
    time: "08:15",
    unread: true,
  },
  {
    id: 3,
    fromName: "系统通知",
    fromEmail: "no-reply@seiko.com",
    subject: "你的账号密码即将过期",
    snippet: "为保障账号安全，你的登录密码将于 7 天后过期，请尽快前往个人中心…",
    body: [
      "尊敬的用户：",
      "为保障账号安全，你的登录密码将于 7 天（2026年9月10日）后过期。",
      "请尽快前往「个人中心 → 安全设置」修改密码。密码需包含大小写字母、数字，长度不少于 8 位。",
      "如非本人操作请忽略本邮件，如有疑问请联系 IT 支持。",
      "—— Seiko Work 安全团队",
    ],
    time: "昨天 18:30",
    unread: false,
  },
  {
    id: 4,
    fromName: "李强",
    fromEmail: "liqiang@seiko.com",
    subject: "莫比乌斯背景渲染性能优化方案",
    snippet: "针对首页 3D 背景在低配设备上的帧率问题，我整理了三套优化方案…",
    body: [
      "Hi，",
      "针对首页莫比乌斯环背景在低配设备上的帧率问题，我整理了三套优化方案：",
      "1. 粒子数量按设备像素比动态降级（2000 → 800）；",
      "2. 线框与表面合并为单次 draw call，减少状态切换；",
      "3. 页面不可见时暂停 requestAnimationFrame 循环。",
      "方案细节已写到技术文档，欢迎周三之前留言讨论。",
      "—— 李强 · 前端组",
    ],
    time: "昨天 15:20",
    unread: false,
  },
  {
    id: 5,
    fromName: "客户支持",
    fromEmail: "support@seiko.com",
    subject: "工单 #1024 有新的回复",
    snippet: "你提交的工单「移动端登录页样式异常」已有新的客服回复，请查收…",
    body: [
      "你好：",
      "你提交的工单 #1024「移动端登录页样式异常」已有新的回复。",
      "客服回复：问题已定位为刘海屏安全区域适配缺失，修复版本预计本周五发布，届时会第一时间通知你验证。",
      "你可以通过工单中心查看完整对话记录。",
      "—— Seiko Work 客户支持",
    ],
    time: "周二 11:05",
    unread: false,
  },
  {
    id: 6,
    fromName: "周报机器人",
    fromEmail: "weekly-bot@seiko.com",
    subject: "你有一份待填写的周报",
    snippet: "本周工作即将结束，请及时填写本周周报，截止时间为周五 18:00…",
    body: [
      "提醒：",
      "本周工作即将结束，你还没有提交周报。",
      "请前往「工作台 → 周报」填写本周工作内容，截止时间为本周五 18:00。逾期将计入考核。",
      "—— 周报机器人",
    ],
    time: "周一 17:45",
    unread: false,
  },
  {
    id: 7,
    fromName: "GitHub",
    fromEmail: "notifications@github.com",
    subject: "[seiko-work] main 分支构建成功",
    snippet: "Build #256 succeeded · refactor(work): 移除请假、工资和五险一金相关功能模块…",
    body: [
      "Build #256 succeeded.",
      "Repository: seiko-work / seiko-work-ui",
      "Branch: main",
      "Latest commit: refactor(work): 移除请假、工资和五险一金相关功能模块",
      "查看详情：https://github.com/seiko-work/seiko-work-ui/actions/runs/256",
    ],
    time: "9月1日",
    unread: false,
  },
];

export function MailPanel() {
  const [mails, setMails] = useState(MOCK_MAILS);
  const [selectedId, setSelectedId] = useState(MOCK_MAILS[0].id);

  const unreadCount = useMemo(() => mails.filter((m) => m.unread).length, [mails]);
  const selected = mails.find((m) => m.id === selectedId) ?? null;

  const selectMail = (id: number) => {
    setSelectedId(id);
    setMails((prev) => prev.map((m) => (m.id === id ? { ...m, unread: false } : m)));
  };

  useEffect(() => {
    const onKey = (e: KeyboardEvent) => {
      if (e.key === "Escape") clearHash();
    };
    window.addEventListener("keydown", onKey);
    return () => window.removeEventListener("keydown", onKey);
  }, []);

  return (
    <div className="fixed bottom-6 left-6 right-24 top-24 z-10 flex gap-4">
      {/* 左：邮件列表 */}
      <aside className="flex w-85 shrink-0 flex-col overflow-hidden rounded-2xl border border-neutral-900/15 bg-white/20 shadow-sm">
        <div className="flex items-center justify-between border-b border-neutral-900/10 px-5 py-4">
          <div className="flex items-center gap-2 text-neutral-900">
            <Inbox className="h-4 w-4" />
            <span className="text-sm font-semibold">收件箱</span>
          </div>
          <span className="rounded-full bg-neutral-900/5 px-2 py-0.5 text-xs text-neutral-600">
            {unreadCount} 封未读
          </span>
        </div>
        <div className="flex-1 overflow-y-auto">
          {mails.map((mail) => (
            <button
              key={mail.id}
              onClick={() => selectMail(mail.id)}
              className={cn(
                "flex w-full items-start gap-3 border-b border-neutral-900/5 px-4 py-3 text-left transition-colors",
                mail.id === selectedId ? "bg-neutral-900/5" : "hover:bg-neutral-900/3"
              )}
            >
              <span className="mt-0.5 flex h-9 w-9 shrink-0 items-center justify-center rounded-full bg-neutral-900/10 text-sm font-medium text-neutral-700">
                {mail.fromName.slice(0, 1)}
              </span>
              <span className="min-w-0 flex-1">
                <span className="flex items-baseline justify-between gap-2">
                  <span
                    className={cn(
                      "truncate text-sm",
                      mail.unread ? "font-semibold text-neutral-900" : "text-neutral-700"
                    )}
                  >
                    {mail.fromName}
                  </span>
                  <span className="shrink-0 text-xs text-neutral-400">{mail.time}</span>
                </span>
                <span
                  className={cn(
                    "mt-0.5 block truncate text-sm",
                    mail.unread ? "font-medium text-neutral-900" : "text-neutral-600"
                  )}
                >
                  {mail.subject}
                </span>
                <span className="mt-0.5 block truncate text-xs text-neutral-400">
                  {mail.snippet}
                </span>
              </span>
              {mail.unread && <span className="mt-2 h-2 w-2 shrink-0 rounded-full bg-sky-500" />}
            </button>
          ))}
        </div>
      </aside>

      {/* 右：邮件详情 */}
      <section className="flex min-w-0 flex-1 flex-col overflow-hidden rounded-2xl border border-neutral-900/15 bg-white/20 shadow-sm">
        {selected ? (
          <>
            <div className="flex items-start justify-between gap-4 border-b border-neutral-900/10 px-6 py-4">
              <h2 className="text-lg font-semibold leading-snug text-neutral-900">
                {selected.subject}
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
                {selected.fromName.slice(0, 1)}
              </span>
              <div className="min-w-0">
                <p className="text-sm font-medium text-neutral-900">{selected.fromName}</p>
                <p className="truncate text-xs text-neutral-400">&lt;{selected.fromEmail}&gt;</p>
              </div>
              <span className="ml-auto shrink-0 text-xs text-neutral-400">{selected.time}</span>
            </div>
            <div className="flex-1 overflow-y-auto px-6 py-5">
              {selected.body.map((paragraph, i) => (
                <p key={i} className="mb-3 text-sm leading-7 text-neutral-700">
                  {paragraph}
                </p>
              ))}
            </div>
          </>
        ) : (
          <div className="flex flex-1 items-center justify-center text-sm text-neutral-400">
            选择一封邮件查看内容
          </div>
        )}
      </section>
    </div>
  );
}
