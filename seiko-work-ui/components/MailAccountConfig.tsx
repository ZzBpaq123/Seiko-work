"use client";

import { useEffect, useState, type InputHTMLAttributes } from "react";
import { ChevronDown, Loader2, Mail } from "lucide-react";
import { cn } from "@/lib/utils";
import { getMailAccount, saveMailAccount, type MailAccount } from "@/lib/mail";
import { toast } from "@/lib/toast";

const EMAIL_RE = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

function Field({ label, ...props }: { label: string } & InputHTMLAttributes<HTMLInputElement>) {
  return (
    <label className="block">
      <span className="mb-1 block text-xs text-neutral-500">{label}</span>
      <input
        {...props}
        className="w-full rounded-lg border border-neutral-900/15 bg-white/60 px-3 py-2 text-sm text-neutral-900 outline-none transition-colors placeholder:text-neutral-400 focus:border-neutral-900"
      />
    </label>
  );
}

export function MailAccountConfig() {
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [account, setAccount] = useState<MailAccount | null>(null);
  const [showAdvanced, setShowAdvanced] = useState(false);

  const [email, setEmail] = useState("");
  const [authCode, setAuthCode] = useState("");
  const [imapHost, setImapHost] = useState("");
  const [imapPort, setImapPort] = useState("");
  const [sslEnable, setSslEnable] = useState(true);

  useEffect(() => {
    getMailAccount()
      .then((acc) => {
        if (acc) {
          setAccount(acc);
          setEmail(acc.email ?? "");
          setImapHost(acc.imapHost ?? "");
          setImapPort(acc.imapPort != null ? String(acc.imapPort) : "");
          setSslEnable(acc.sslEnable ?? true);
        }
      })
      .catch(() => toast.error("加载邮箱授权配置失败"))
      .finally(() => setLoading(false));
  }, []);

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!EMAIL_RE.test(email.trim())) return toast.error("邮箱格式不正确");
    if (!authCode.trim()) return toast.error("请输入邮箱授权码");

    let port: number | undefined;
    if (imapPort.trim()) {
      const n = Number(imapPort.trim());
      if (!Number.isInteger(n) || n <= 0 || n > 65535) return toast.error("IMAP 端口不合法");
      port = n;
    }

    setSaving(true);
    try {
      await saveMailAccount({
        email: email.trim(),
        authCode: authCode.trim(),
        imapHost: imapHost.trim() || undefined,
        imapPort: port,
        sslEnable,
      });
      toast.success("邮箱授权配置已保存");
      const acc = await getMailAccount().catch(() => null);
      if (acc) {
        setAccount(acc);
        setEmail(acc.email ?? "");
        setImapHost(acc.imapHost ?? "");
        setImapPort(acc.imapPort != null ? String(acc.imapPort) : "");
        setSslEnable(acc.sslEnable ?? true);
      }
      setAuthCode("");
    } catch (err) {
      toast.error(err instanceof Error ? err.message : "保存失败，请稍后重试");
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="flex flex-col gap-3">
      <div className="flex items-center gap-2">
        <Mail className="h-4 w-4 text-neutral-700" />
        <span className="text-sm font-semibold text-neutral-900">邮箱授权配置</span>
        {account && (
          <span className="rounded-full bg-neutral-900/5 px-2 py-0.5 text-xs text-neutral-600">
            已配置
          </span>
        )}
      </div>

      {loading ? (
        <div className="flex items-center justify-center gap-2 py-10 text-sm text-neutral-500">
          <Loader2 className="h-4 w-4 animate-spin" />
          正在加载配置…
        </div>
      ) : (
        <form onSubmit={handleSave} className="flex flex-col gap-3">
          <Field
            label="邮箱地址"
            type="email"
            placeholder="you@example.com"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
          <Field
            label="授权码"
            type="password"
            placeholder={account ? "已配置，重新输入可更新授权码" : "请输入邮箱授权码"}
            value={authCode}
            onChange={(e) => setAuthCode(e.target.value)}
          />

          <button
            type="button"
            onClick={() => setShowAdvanced((v) => !v)}
            className="flex items-center gap-1 self-start text-xs text-neutral-500 underline underline-offset-2 transition-colors hover:text-neutral-900"
          >
            高级设置
            <ChevronDown
              className={cn("h-3 w-3 transition-transform", showAdvanced && "rotate-180")}
            />
          </button>

          {showAdvanced && (
            <>
              <Field
                label="IMAP 服务器地址（缺省自动识别）"
                placeholder="imap.example.com"
                value={imapHost}
                onChange={(e) => setImapHost(e.target.value)}
              />
              <Field
                label="IMAP 端口（缺省自动识别）"
                inputMode="numeric"
                placeholder="993"
                value={imapPort}
                onChange={(e) => setImapPort(e.target.value)}
              />
              <label className="flex items-center gap-2 text-sm text-neutral-700">
                <input
                  type="checkbox"
                  checked={sslEnable}
                  onChange={(e) => setSslEnable(e.target.checked)}
                  className="h-4 w-4 accent-neutral-900"
                />
                启用 SSL
              </label>
            </>
          )}

          <div className="pt-1">
            <button
              type="submit"
              disabled={saving}
              className="flex w-full items-center justify-center gap-2 rounded-lg bg-neutral-900 py-2.5 text-sm font-medium text-neutral-50 transition-opacity disabled:opacity-60"
            >
              {saving && <Loader2 className="h-4 w-4 animate-spin" />}
              保存配置
            </button>
          </div>

          {account && (
            <p className="text-xs text-neutral-400">
              当前服务器：{account.imapHost || "自动识别"}
              {account.imapPort ? ` : ${account.imapPort}` : ""}
              {account.sslEnable ? "（SSL）" : ""}
            </p>
          )}
        </form>
      )}
    </div>
  );
}
