package com.university.library.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.university.library.model.User;
import com.university.library.repository.wrapper.UsersWrapper;
import com.university.library.util.JsonFileUtil;
import org.springframework.stereotype.Repository;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    private static final String USERS_FILE = "data/users.json";
    private static final ObjectMapper mapper = new ObjectMapper();

    public List<User> findAll() {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("data/users.json");

            if (is == null) {
                throw new RuntimeException("Failed to read JSON file: data/users.json");
            }

            UsersWrapper wrapper = mapper.readValue(is, UsersWrapper.class);

            // اگر هیچ کاربری وجود ندارد، لیست خالی برگردان
            return wrapper.getUsers() != null ? wrapper.getUsers() : new ArrayList<>();

        } catch (Exception e) {
            throw new RuntimeException("Failed to read JSON file: data/users.json", e);
        }
    }

    public Optional<User> findByUsername(String username) {
        return findAll().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst();
    }

    public void save(User user) {
        UsersWrapper wrapper =
                JsonFileUtil.readFromFile(USERS_FILE, UsersWrapper.class);

        wrapper.getUsers().add(user);

        JsonFileUtil.writeToFile(USERS_FILE, wrapper);
    }

    public void saveAll(List<User> users) {
        UsersWrapper wrapper = new UsersWrapper();
        wrapper.setUsers(users);
        JsonFileUtil.writeToFile(USERS_FILE, wrapper);
    }
}