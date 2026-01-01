package com.university.library.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;

public class JsonFileUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    // خواندن فایل JSON و تبدیل به Object
    public static <T> T readFromFile(String filePath, Class<T> clazz) {
        try {
            return mapper.readValue(new File(filePath), clazz);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read JSON file: " + filePath, e);
        }
    }

    // نوشتن Object در فایل JSON
    public static void writeToFile(String filePath, Object data) {
        try {
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(filePath), data);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write JSON file: " + filePath, e);
        }
    }
}