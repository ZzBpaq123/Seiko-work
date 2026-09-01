package com.seiko.work.config;

import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.stp.StpUtil;
import com.seiko.work.base.Result;
import com.seiko.work.base.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Sa-Token 权限认证配置
 * <p>
 * 基于 SaRouter 路由匹配实现接口权限控制：
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SaTokenConfig {

    /**
     * 注册 Sa-Token 全局过滤器
     */
    @Bean
    public SaServletFilter saServletFilter() {
        return new SaServletFilter()
                // 指定拦截路由
                .addInclude("/**")
                // 指定放行路由（静态资源 & Swagger & 上传文件）
                .addExclude("/favicon.ico", "/doc.html", "/webjars/**", "/swagger-resources/**", "/v3/api-docs/**",
                        "/swagger-ui/**", "/swagger-ui.html", "/auth/**")
                // 认证函数：基于路径和方法做权限校验
                .setAuth(obj -> {
                    // 5. 其余接口默认需要登录
                    StpUtil.checkLogin();
                })
                // 异常处理函数
                .setError(e -> {
                    log.warn("Sa-Token 全局过滤器异常: {}", e.getMessage());
                    return Result.error(ResultCode.UNAUTHORIZED.getCode(), e.getMessage());
                });
    }

}
