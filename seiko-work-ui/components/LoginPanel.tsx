"use client";

import { useEffect, useState, type InputHTMLAttributes } from "react";
import { KeyRound, Loader2, LogOut, X } from "lucide-react";
import { cn } from "@/lib/utils";
import { clearHash } from "@/hooks/useHash";
import {
  TOKEN_KEY,
  type UserVO,
  clearLoginState,
  emailLogin,
  emailRegister,
  getCurrentUser,
  getStoredUser,
  logout,
  phoneLogin,
  saveLoginState,
  sendEmailCode,
  sendPhoneCode,
} from "@/lib/auth";

type Mode = "email-login" | "email-register" | "phone-login";

const MODE_TABS: { key: Mode; label: string }[] = [
  { key: "email-login", label: "邮箱登录" },
  { key: "phone-login", label: "手机登录" },
];

const EMAIL_RE = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const PHONE_RE = /^1[3-9]\d{9}$/;
const CODE_RE = /^\d{6}$/;

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

function SendCodeButton({
  onSend,
  disabled,
}: {
  onSend: () => Promise<boolean>;
  disabled?: boolean;
}) {
  const [countdown, setCountdown] = useState(0);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (countdown <= 0) return;
    const timer = setTimeout(() => setCountdown((c) => c - 1), 1000);
    return () => clearTimeout(timer);
  }, [countdown]);

  const handleClick = async () => {
    setLoading(true);
    try {
      if (await onSend()) setCountdown(60);
    } finally {
      setLoading(false);
    }
  };

  const busy = loading || countdown > 0;
  return (
    <button
      type="button"
      onClick={handleClick}
      disabled={disabled || busy}
      className="shrink-0 rounded-lg border border-neutral-900/15 bg-white/60 px-3 py-2 text-xs text-neutral-700 transition-colors hover:border-neutral-900 disabled:cursor-not-allowed disabled:opacity-50"
    >
      {countdown > 0 ? `${countdown}s 后重发` : loading ? "发送中…" : "获取验证码"}
    </button>
  );
}

function SubmitButton({ loading, children }: { loading: boolean; children: string }) {
  return (
    <button
      type="submit"
      disabled={loading}
      className="flex w-full items-center justify-center gap-2 rounded-lg bg-neutral-900 py-2.5 text-sm font-medium text-neutral-50 transition-opacity disabled:opacity-60"
    >
      {loading && <Loader2 className="h-4 w-4 animate-spin" />}
      {children}
    </button>
  );
}

export function LoginPanel() {
  const [user, setUser] = useState<UserVO | null>(null);
  const [checked, setChecked] = useState(false);
  const [mode, setMode] = useState<Mode>("email-login");

  const [error, setError] = useState("");
  const [notice, setNotice] = useState("");
  const [loading, setLoading] = useState(false);

  // 邮箱登录
  const [loginEmail, setLoginEmail] = useState("");
  const [loginPassword, setLoginPassword] = useState("");

  // 邮箱注册
  const [regUsername, setRegUsername] = useState("");
  const [regEmail, setRegEmail] = useState("");
  const [regCode, setRegCode] = useState("");
  const [regPassword, setRegPassword] = useState("");
  const [regConfirm, setRegConfirm] = useState("");

  // 手机登录
  const [phone, setPhone] = useState("");
  const [phoneCode, setPhoneCode] = useState("");

  useEffect(() => {
    if (!localStorage.getItem(TOKEN_KEY)) {
      setChecked(true);
      return;
    }
    getCurrentUser()
      .then(setUser)
      .catch(() => clearLoginState())
      .finally(() => setChecked(true));
  }, []);

  useEffect(() => {
    const onKey = (e: KeyboardEvent) => {
      if (e.key === "Escape") clearHash();
    };
    window.addEventListener("keydown", onKey);
    return () => window.removeEventListener("keydown", onKey);
  }, []);

  const fail = (e: unknown) => setError(e instanceof Error ? e.message : "操作失败，请稍后重试");

  const switchMode = (next: Mode) => {
    setMode(next);
    setError("");
    setNotice("");
  };

  const handleEmailLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setNotice("");
    if (!EMAIL_RE.test(loginEmail)) return setError("邮箱格式不正确");
    if (!loginPassword) return setError("请输入密码");
    setLoading(true);
    try {
      const login = await emailLogin(loginEmail, loginPassword);
      saveLoginState(login);
      setUser(login.user);
      setNotice("登录成功");
      clearHash();
    } catch (err) {
      fail(err);
    } finally {
      setLoading(false);
    }
  };

  const handleSendEmailCode = async (): Promise<boolean> => {
    setError("");
    setNotice("");
    if (!EMAIL_RE.test(regEmail)) {
      setError("请输入正确的邮箱后再获取验证码");
      return false;
    }
    try {
      await sendEmailCode(regEmail);
      setNotice("验证码已发送，5 分钟内有效");
      return true;
    } catch (err) {
      fail(err);
      return false;
    }
  };

  const handleEmailRegister = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setNotice("");
    if (regUsername.trim().length < 2 || regUsername.trim().length > 50)
      return setError("用户名长度需为 2-50 个字符");
    if (!EMAIL_RE.test(regEmail)) return setError("邮箱格式不正确");
    if (!CODE_RE.test(regCode)) return setError("验证码为 6 位数字");
    if (regPassword.length < 6 || regPassword.length > 20)
      return setError("密码长度需为 6-20 个字符");
    if (regPassword !== regConfirm) return setError("两次输入的密码不一致");
    setLoading(true);
    try {
      await emailRegister({
        username: regUsername.trim(),
        email: regEmail,
        code: regCode,
        password: regPassword,
        confirmPassword: regConfirm,
      });
      setLoginEmail(regEmail);
      setLoginPassword("");
      setRegCode("");
      setRegPassword("");
      setRegConfirm("");
      switchMode("email-login");
      setNotice("注册成功，请登录");
    } catch (err) {
      fail(err);
    } finally {
      setLoading(false);
    }
  };

  const handleSendPhoneCode = async (): Promise<boolean> => {
    setError("");
    setNotice("");
    if (!PHONE_RE.test(phone)) {
      setError("请输入正确的手机号后再获取验证码");
      return false;
    }
    try {
      await sendPhoneCode(phone);
      setNotice("验证码已发送，5 分钟内有效");
      return true;
    } catch (err) {
      fail(err);
      return false;
    }
  };

  const handlePhoneLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setNotice("");
    if (!PHONE_RE.test(phone)) return setError("手机号格式不正确");
    if (!CODE_RE.test(phoneCode)) return setError("验证码为 6 位数字");
    setLoading(true);
    try {
      const login = await phoneLogin(phone, phoneCode);
      saveLoginState(login);
      setUser(login.user);
      setNotice("登录成功");
      clearHash();
    } catch (err) {
      fail(err);
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = async () => {
    setError("");
    setNotice("");
    try {
      await logout();
    } catch {
      // 本地登出不受服务端失败影响
    }
    clearLoginState();
    setUser(null);
    setLoginPassword("");
    setPhoneCode("");
  };

  const inputClass = "h-4 w-4 text-neutral-600";

  return (
    <div className="fixed bottom-6 left-6 right-24 top-24 z-10 flex items-center justify-center">
      <section className="flex w-full max-w-md flex-col overflow-hidden rounded-2xl border border-neutral-900/15 bg-white/20 shadow-sm">
        <div className="flex items-center justify-between border-b border-neutral-900/10 px-6 py-4">
          <div className="flex items-center gap-2 text-neutral-900">
            <span className="text-sm font-semibold">
              {user ? "当前账号" : mode === "email-register" ? "注册账号" : "登录账号"}
            </span>
          </div>
          <button
            onClick={clearHash}
            aria-label="关闭"
            className="rounded-full p-1.5 text-neutral-500 transition-colors hover:bg-neutral-900/5 hover:text-neutral-900"
          >
            <X className="h-5 w-5" />
          </button>
        </div>

        <div className="px-6 py-5">
          {!checked ? (
            <div className="flex items-center justify-center gap-2 py-10 text-sm text-neutral-500">
              <Loader2 className="h-4 w-4 animate-spin" />
              正在检查登录状态…
            </div>
          ) : user ? (
            <div className="flex flex-col items-center gap-4 py-4">
              <span className="flex h-16 w-16 items-center justify-center rounded-full bg-neutral-900/10 text-xl font-medium text-neutral-700">
                {(user.nickname || user.username).slice(0, 1)}
              </span>
              <div className="text-center">
                <p className="text-base font-semibold text-neutral-900">
                  {user.nickname || user.username}
                </p>
                <p className="mt-0.5 text-xs text-neutral-500">
                  {user.email || user.phone || ""}
                </p>
              </div>
              <button
                onClick={handleLogout}
                className="flex items-center gap-2 rounded-lg border border-neutral-900/15 bg-white/60 px-4 py-2 text-sm text-neutral-700 transition-colors hover:border-neutral-900"
              >
                <LogOut className="h-4 w-4" />
                退出登录
              </button>
            </div>
          ) : (
            <>
              {mode !== "email-register" && (
                <div className="mb-5 flex rounded-lg border border-neutral-900/10 bg-neutral-900/5 p-1">
                  {MODE_TABS.map((tab) => (
                    <button
                      key={tab.key}
                      type="button"
                      onClick={() => switchMode(tab.key)}
                      className={cn(
                        "flex-1 rounded-md py-1.5 text-xs transition-colors",
                        mode === tab.key
                          ? "bg-white text-neutral-900 shadow-sm"
                          : "text-neutral-500 hover:text-neutral-900"
                      )}
                    >
                      {tab.label}
                    </button>
                  ))}
                </div>
              )}

              {error && (
                <p className="mb-3 rounded-lg border border-red-900/15 bg-red-50/80 px-3 py-2 text-xs text-red-700">
                  {error}
                </p>
              )}
              {notice && (
                <p className="mb-3 rounded-lg border border-emerald-900/15 bg-emerald-50/80 px-3 py-2 text-xs text-emerald-700">
                  {notice}
                </p>
              )}

              {mode === "email-login" && (
                <form onSubmit={handleEmailLogin} className="flex flex-col gap-3">
                  <Field
                    label="邮箱"
                    type="email"
                    placeholder="you@example.com"
                    value={loginEmail}
                    onChange={(e) => setLoginEmail(e.target.value)}
                  />
                  <Field
                    label="密码"
                    type="password"
                    placeholder="请输入密码"
                    value={loginPassword}
                    onChange={(e) => setLoginPassword(e.target.value)}
                  />
                  <div className="pt-1">
                    <SubmitButton loading={loading}>登录</SubmitButton>
                  </div>
                </form>
              )}

              {mode === "email-register" && (
                <form onSubmit={handleEmailRegister} className="flex flex-col gap-3">
                  <Field
                    label="用户名"
                    placeholder="2-50 个字符"
                    value={regUsername}
                    onChange={(e) => setRegUsername(e.target.value)}
                  />
                  <Field
                    label="邮箱"
                    type="email"
                    placeholder="you@example.com"
                    value={regEmail}
                    onChange={(e) => setRegEmail(e.target.value)}
                  />
                  <div>
                    <span className="mb-1 block text-xs text-neutral-500">邮箱验证码</span>
                    <div className="flex gap-2">
                      <input
                        value={regCode}
                        onChange={(e) => setRegCode(e.target.value)}
                        placeholder="6 位数字"
                        maxLength={6}
                        className="w-full rounded-lg border border-neutral-900/15 bg-white/60 px-3 py-2 text-sm text-neutral-900 outline-none transition-colors placeholder:text-neutral-400 focus:border-neutral-900"
                      />
                      <SendCodeButton onSend={handleSendEmailCode} disabled={loading} />
                    </div>
                  </div>
                  <Field
                    label="密码"
                    type="password"
                    placeholder="6-20 个字符"
                    value={regPassword}
                    onChange={(e) => setRegPassword(e.target.value)}
                  />
                  <Field
                    label="确认密码"
                    type="password"
                    placeholder="再次输入密码"
                    value={regConfirm}
                    onChange={(e) => setRegConfirm(e.target.value)}
                  />
                  <div className="pt-1">
                    <SubmitButton loading={loading}>注册</SubmitButton>
                  </div>
                </form>
              )}

              {mode === "phone-login" && (
                <form onSubmit={handlePhoneLogin} className="flex flex-col gap-3">
                  <div>
                    <span className="mb-1 block text-xs text-neutral-500">
                      手机号
                    </span>
                    <input
                      value={phone}
                      onChange={(e) => setPhone(e.target.value)}
                      placeholder="11 位手机号"
                      maxLength={11}
                      className="w-full rounded-lg border border-neutral-900/15 bg-white/60 px-3 py-2 text-sm text-neutral-900 outline-none transition-colors placeholder:text-neutral-400 focus:border-neutral-900"
                    />
                  </div>
                  <div>
                    <span className="mb-1 block text-xs text-neutral-500">短信验证码</span>
                    <div className="flex gap-2">
                      <input
                        value={phoneCode}
                        onChange={(e) => setPhoneCode(e.target.value)}
                        placeholder="6 位数字"
                        maxLength={6}
                        className="w-full rounded-lg border border-neutral-900/15 bg-white/60 px-3 py-2 text-sm text-neutral-900 outline-none transition-colors placeholder:text-neutral-400 focus:border-neutral-900"
                      />
                      <SendCodeButton onSend={handleSendPhoneCode} disabled={loading} />
                    </div>
                  </div>
                  <div className="pt-1">
                    <SubmitButton loading={loading}>登录</SubmitButton>
                  </div>
                </form>
              )}

              {mode !== "email-register" ? (
                <p className="mt-4 text-center text-xs text-neutral-500">
                  还没账号？
                  <button
                    type="button"
                    onClick={() => switchMode("email-register")}
                    className="text-neutral-900 underline underline-offset-2 transition-opacity hover:opacity-60"
                  >
                    赶快注册...
                  </button>
                </p>
              ) : (
                <p className="mt-4 text-center text-xs text-neutral-500">
                  已有账号
                  <button
                    type="button"
                    onClick={() => switchMode("email-login")}
                    className="text-neutral-900 underline underline-offset-2 transition-opacity hover:opacity-60"
                  >
                    返回登录
                  </button>
                </p>
              )}

              <p className="mt-4 flex items-center justify-center gap-1 text-xs text-neutral-400">
                <KeyRound className="h-3 w-3" />
                登录即代表同意服务条款与隐私政策
              </p>
            </>
          )}
        </div>
      </section>
    </div>
  );
}
