package com.seiko.work.util;

import jakarta.servlet.http.HttpServletRequest;

import java.net.InetAddress;
import java.util.List;

/**
 * IP 工具类
 *
 * <p>仅在直连来源属于可信代理时才解析 X-Forwarded-For，防止客户端伪造头部绕过限流。</p>
 */
public final class IpUtils {

    private IpUtils() {
    }

    /**
     * 获取客户端 IP。
     *
     * <p>仅当请求的直连来源（remoteAddr）在可信代理列表中时才信任 X-Forwarded-For，
     * 且只取可信代理刚追加的最后一个 IP（前面的条目可能被客户端伪造）；
     * 直连来源不可信时一律使用 remoteAddr。</p>
     *
     * @param request        HTTP 请求
     * @param trustedProxies 可信代理 IP 或 CIDR 列表，如 127.0.0.1、10.0.0.0/8
     * @return 客户端 IP
     */
    public static String getClientIp(HttpServletRequest request, List<String> trustedProxies) {
        String remoteAddr = request.getRemoteAddr();
        if (!isTrustedProxy(remoteAddr, trustedProxies)) {
            return remoteAddr;
        }

        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            String[] entries = xff.split(",");
            String last = entries[entries.length - 1].trim();
            if (!last.isEmpty() && !"unknown".equalsIgnoreCase(last)) {
                return last;
            }
        }
        return remoteAddr;
    }

    private static boolean isTrustedProxy(String remoteAddr, List<String> trustedProxies) {
        if (remoteAddr == null || trustedProxies == null) {
            return false;
        }
        for (String proxy : trustedProxies) {
            if (proxy == null || proxy.isBlank()) {
                continue;
            }
            try {
                InetAddress remote = InetAddress.getByName(remoteAddr.trim());
                String cidr = proxy.trim();
                String base = cidr;
                int prefix = remote.getAddress().length * 8;
                int idx = cidr.indexOf('/');
                if (idx >= 0) {
                    base = cidr.substring(0, idx);
                    prefix = Integer.parseInt(cidr.substring(idx + 1).trim());
                }
                if (matchesPrefix(remote.getAddress(), InetAddress.getByName(base).getAddress(), prefix)) {
                    return true;
                }
            } catch (Exception ignored) {
                // 配置项或地址无法解析，视为不匹配
            }
        }
        return false;
    }

    private static boolean matchesPrefix(byte[] ip, byte[] base, int prefix) {
        if (ip.length != base.length || prefix < 0 || prefix > ip.length * 8) {
            return false;
        }
        int fullBytes = prefix / 8;
        for (int i = 0; i < fullBytes; i++) {
            if (ip[i] != base[i]) {
                return false;
            }
        }
        int remaining = prefix % 8;
        if (remaining > 0) {
            int mask = 0xFF << (8 - remaining);
            return (ip[fullBytes] & mask) == (base[fullBytes] & mask);
        }
        return true;
    }
}
