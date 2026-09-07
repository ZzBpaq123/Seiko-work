import { request } from "@/lib/axios";

export interface MailAccount {
  id: number;
  userId: number;
  email: string;
  imapHost: string | null;
  imapPort: number | null;
  sslEnable: boolean | null;
}

export interface MailAccountParams {
  email: string;
  authCode: string;
  imapHost?: string;
  imapPort?: number;
  sslEnable?: boolean;
}

export function getMailAccount() {
  return request<MailAccount | null>({ method: "GET", url: "/api/mails/account" });
}

export function saveMailAccount(params: MailAccountParams) {
  return request<void>({ method: "POST", url: "/api/mails/account", data: params });
}
