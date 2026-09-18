package com.xiaogui.workbench.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;

/**
 * 灵活的 LocalDateTime 反序列化器
 * 支持以下格式：
 *   - yyyy-MM-dd HH:mm:ss
 *   - yyyy-MM-dd HH:mm        (自动补 00 秒)
 *   - yyyy-MM-dd'T'HH:mm:ss   (ISO 格式)
 *   - yyyy-MM-dd'T'HH:mm      (ISO 缺秒)
 */
public class FlexibleLocalDateTimeDeserializer extends StdDeserializer<LocalDateTime> {

    private static final long serialVersionUID = 1L;

    private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd")
            .optionalStart()
            .appendLiteral(' ')
            .optionalEnd()
            .optionalStart()
            .appendLiteral('T')
            .optionalEnd()
            .appendPattern("HH:mm")
            .optionalStart()
            .appendLiteral(':')
            .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
            .optionalEnd()
            .toFormatter();

    public FlexibleLocalDateTimeDeserializer() {
        super(LocalDateTime.class);
    }

    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (parser.currentToken() == JsonToken.VALUE_NULL) {
            return null;
        }
        String text = parser.getText().trim();
        if (text.isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(text, FORMATTER);
        } catch (DateTimeParseException e) {
            // 兜底：尝试 ISO 格式
            try {
                return LocalDateTime.parse(text, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (DateTimeParseException e2) {
                throw context.weirdStringException(text, LocalDateTime.class, e.getMessage());
            }
        }
    }
}
