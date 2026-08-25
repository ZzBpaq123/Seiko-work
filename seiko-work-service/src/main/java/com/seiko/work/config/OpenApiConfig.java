package com.seiko.work.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Springdoc OpenAPI 配置类
 * <p>
 * 配置 Swagger UI 文档信息、认证方式
 *
 * @author seiko
 */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "Authorization";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Seiko Work Service API")
                        .version("1.0.0")
                        .description("Seiko Work Service 接口文档 - 基于 Spring Boot 3.4.0 + Sa-Token + MyBatis-Plus")
                        .contact(new Contact()
                                .name("seiko")
                                .email("seiko@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server().url("http://localhost:8080/api").description("本地开发环境"),
                        new Server().url("https://api.example.com/api").description("生产环境")
                ))
                // 全局认证配置：在 Swagger UI 中可输入 Token
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .description("请输入登录后获取的 Token（UUID 格式）")));
    }
}
