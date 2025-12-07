package io.github.uchkun07.travelsystem.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Jackson 配置类
 * 用于配置 JSON 序列化和反序列化，特别是 Java 8 日期时间类型
 */
@Configuration
public class JacksonConfig {

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIME_FORMAT = "HH:mm:ss";

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> {
            // 注册 JavaTimeModule
            JavaTimeModule javaTimeModule = new JavaTimeModule();

            // LocalDateTime 序列化和反序列化
            javaTimeModule.addSerializer(LocalDateTime.class,
                    new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
            javaTimeModule.addDeserializer(LocalDateTime.class,
                    new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));

            // LocalDate 序列化和反序列化
            javaTimeModule.addSerializer(LocalDate.class,
                    new LocalDateSerializer(DateTimeFormatter.ofPattern(DATE_FORMAT)));
            javaTimeModule.addDeserializer(LocalDate.class,
                    new LocalDateDeserializer(DateTimeFormatter.ofPattern(DATE_FORMAT)));

            // LocalTime 序列化和反序列化
            javaTimeModule.addSerializer(LocalTime.class,
                    new LocalTimeSerializer(DateTimeFormatter.ofPattern(TIME_FORMAT)));
            javaTimeModule.addDeserializer(LocalTime.class,
                    new LocalTimeDeserializer(DateTimeFormatter.ofPattern(TIME_FORMAT)));

            builder.modules(javaTimeModule);

            // 禁用将日期序列化为时间戳
            builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            
            // 忽略未知属性
            builder.featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        };
    }
}
