package com.seiko.work.aspect;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 全局请求日志切面
 * <p>
 * 记录 Controller 层请求的 URL、方法、参数、响应、耗时、操作用户等信息
 *
 * @author seiko
 */
@Slf4j
@Aspect
@Component
public class WebLogAspect {

    /**
     * 敏感字段，日志中不打印
     */
    private static final List<String> SENSITIVE_FIELDS = List.of(
            "password", "pwd", "oldPassword", "newPassword", "confirmPassword",
            "token", "authorization", "secret", "captcha", "code"
    );

    /**
     * 切点：所有 Controller 层的 public 方法
     */
    @Pointcut("execution(public * com.seiko.work.module..controller..*.*(..))")
    public void webLogPointcut() {
    }

    /**
     * 环绕通知：记录请求日志
     */
    @Around("webLogPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 获取请求信息
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(attributes).getRequest();

        String requestUrl = request.getRequestURI();
        String httpMethod = request.getMethod();
        String ip = getClientIp(request);
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String userId = getCurrentUserId();

        // 记录请求参数
        String params = buildParamsString(joinPoint.getArgs());

        log.info("""
                        ====== 请求开始 ======
                        URL: {}
                        Method: {}
                        IP: {}
                        Class: {}.{}
                        UserId: {}
                        Params: {}
                        =====================""",
                requestUrl, httpMethod, ip, className, methodName, userId, params);

        Object result;
        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            long cost = System.currentTimeMillis() - startTime;
            log.error("""
                            ====== 请求异常 ======
                            URL: {}
                            Method: {}
                            Cost: {}ms
                            Error: {}
                            =====================""",
                    requestUrl, httpMethod, cost, e.getMessage());
            throw e;
        } finally {
            long cost = System.currentTimeMillis() - startTime;
            log.info("""
                            ====== 请求结束 ======
                            URL: {}
                            Method: {}
                            Cost: {}ms
                            =====================""",
                    requestUrl, httpMethod, cost);
        }
    }

    /**
     * 构建参数字符串，过滤敏感字段和不可序列化对象
     */
    private String buildParamsString(Object[] args) {
        if (args == null || args.length == 0) {
            return "{}";
        }
        try {
            Object[] filteredArgs = Arrays.stream(args)
                    .filter(arg -> !(arg instanceof MultipartFile)
                            && !(arg instanceof jakarta.servlet.ServletRequest)
                            && !(arg instanceof jakarta.servlet.ServletResponse))
                    .toArray();

            if (filteredArgs.length == 0) {
                return "{}";
            }

            String json = JSONUtil.toJsonStr(filteredArgs);
            return maskSensitiveFields(json);
        } catch (Exception e) {
            return "[参数序列化失败: " + e.getMessage() + "]";
        }
    }

    /**
     * 脱敏敏感字段
     */
    private String maskSensitiveFields(String json) {
        String result = json;
        for (String field : SENSITIVE_FIELDS) {
            result = result.replaceAll("\"" + field + "\"\\s*:\\s*\"[^\"]*\"",
                    "\"" + field + "\":\"******\"");
        }
        return result;
    }

    /**
     * 获取客户端真实 IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多级代理时取第一个
        if (StrUtil.isNotBlank(ip) && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * 获取当前登录用户 ID
     */
    private String getCurrentUserId() {
        try {
            if (StpUtil.isLogin()) {
                return StpUtil.getLoginIdAsString();
            }
        } catch (Exception ignored) {
        }
        return "anonymous";
    }
}
