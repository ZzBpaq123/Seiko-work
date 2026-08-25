package com.seiko.work.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token 权限认证配置类
 * <p>
 * 配置路由拦截规则，开放接口白名单
 *
 * @author seiko
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    /**
     * 注册 Sa-Token 拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> {
                    // 全局登录校验：除白名单外，所有请求都需要登录
                    StpUtil.checkLogin();
                }))
                .addPathPatterns("/**")
                .excludePathPatterns(
                        // 认证相关接口
                        "/auth/login",
                        "/auth/register",
                        "/auth/captcha",
                        "/auth/send-code",

                        // Swagger / OpenAPI 文档
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/swagger-resources/**",
                        "/webjars/**",
                        "/favicon.ico",

                        // 健康检查
                        "/actuator/**",
                        "/error"
                );
    }
}
