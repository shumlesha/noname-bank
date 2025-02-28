package ru.patterns.credit.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String writeValueAsAString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка при сериализации объекта в строку JSON", e);
        }
    }
}