package com.xiaogui.workbench.config;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.format.DateTimeFormatter;

/**
 * 全局 Jackson 日期格式配置
 * 统一前后端 LocalDateTime 序列化/反序列化格式：yyyy-MM-dd HH:mm:ss
 * 同时兼容前端缺秒的 "2026-08-07 09:00" 格式（自动补 00 秒）
 * 也兼容 ISO 格式 "2026-08-07T09:00:00"
 */
@Configuration
public class JacksonConfig {

    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> {
            // 注册自定义灵活反序列化器（兼容带 T/空格、带/不带秒）
            JavaTimeModule module = new JavaTimeModule();
            module.addDeserializer(java.time.LocalDateTime.class, new FlexibleLocalDateTimeDeserializer());
            module.addSerializer(java.time.LocalDateTime.class,
                    new com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer(
                            DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)));

            builder.modules(module)
                   .featuresToDisable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        };
    }
}
