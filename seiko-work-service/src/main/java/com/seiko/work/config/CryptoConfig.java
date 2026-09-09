package com.seiko.work.config;

import com.seiko.work.config.properties.SecurityProperties;
import com.seiko.work.util.CryptoUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

/**
 * 敏感字段加密配置：启动时注入密钥，未配置则启动失败（避免运行时才暴露）
 */
@Configuration
@RequiredArgsConstructor
public class CryptoConfig {

    private final SecurityProperties securityProperties;

    @PostConstruct
    public void initCryptoKey() {
        CryptoUtil.init(securityProperties.getCryptoKey());
    }
}
