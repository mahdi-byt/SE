package com.university.library.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.university.library.model.Book;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookRepository {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String FILE_NAME = "data/books.json";

    /**
     * خواندن تمام کتاب‌ها از resources/data/books.json
     */
    public List<Book> findAll() {
        try (InputStream is = getResourceAsStream(FILE_NAME)) {
            Map<String, List<Book>> map = mapper.readValue(is, new TypeReference<Map<String, List<Book>>>() {});
            return map.getOrDefault("books", new ArrayList<>());
        } catch (Exception e) {
            throw new RuntimeException("Failed to read JSON file: " + FILE_NAME, e);
        }
    }

    /**
     * ذخیره تمام کتاب‌ها در JSON
     */
    public void saveAll(List<Book> books) {
        try {
            File file = getWritableFile(FILE_NAME);
            Map<String, List<Book>> map = new HashMap<>();
            map.put("books", books);
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, map);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save JSON file: " + FILE_NAME, e);
        }
    }

    /**
     * خواندن فایل JSON از resources
     */
    private InputStream getResourceAsStream(String path) {
        InputStream is = getClass().getClassLoader().getResourceAsStream(path);
        if (is == null) throw new RuntimeException("Failed to read JSON file: " + path);
        return is;
    }

    /**
     * گرفتن مسیر فایل قابل نوشتن (برای IDE و jar)
     */
    private File getWritableFile(String path) throws URISyntaxException {
        URL resourceUrl = getClass().getClassLoader().getResource(path);
        if (resourceUrl == null) throw new RuntimeException("File not found: " + path);

        // اگر فایل داخل jar باشد، نمی‌توان مستقیم نوشت → بهتر است مسیر temp یا پروژه
        if (resourceUrl.getProtocol().equals("jar")) {
            return new File("data/books.json"); // مسیر writable خارج از jar
        }
        return new File(resourceUrl.toURI());
    }
}