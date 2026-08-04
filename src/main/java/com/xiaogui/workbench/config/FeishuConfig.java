package com.xiaogui.workbench.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "feishu")
public class FeishuConfig {

    private String appId;
    private String appSecret;
    private String baseUrl;
}
