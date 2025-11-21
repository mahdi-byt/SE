package users;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class GuestTest {

    @TempDir
    Path tempDir;

    private Guest guest;
    private ObjectMapper mapper;
    private File usersFile;
    private File borrowFile;

    @BeforeEach
    void setUp() {
        guest = new Guest("guest", "guest123");
        mapper = new ObjectMapper();

        usersFile = tempDir.resolve("users.json").toFile();
        borrowFile = tempDir.resolve("borrow.json").toFile();

        Guest.setUsersFilePath(usersFile.getAbsolutePath());
        Guest.setBorrowFilePath(borrowFile.getAbsolutePath());
    }

    @Test
    void registerSuccess() throws IOException {
        createEmptyUsersFile();

        boolean result = guest.register("newuser", "pass123");

        assertTrue(result);

        ObjectNode root = (ObjectNode) mapper.readTree(usersFile);
        ArrayNode users = (ArrayNode) root.get("users");
        assertEquals(1, users.size());
        assertEquals("newuser", users.get(0).get("username").asText());
        assertEquals("student", users.get(0).get("role").asText());
    }

    @Test
    void registerDuplicateUsername() throws IOException {
        createUsersFileWithUser();

        boolean result = guest.register("existinguser", "newpass");

        assertFalse(result);

        ObjectNode root = (ObjectNode) mapper.readTree(usersFile);
        ArrayNode users = (ArrayNode) root.get("users");
        assertEquals(1, users.size());
        assertEquals("existinguser", users.get(0).get("username").asText());
        assertEquals("oldpass", users.get(0).get("password").asText());
    }

    @Test
    void getBorrowedBooksCount() throws IOException {
        createBorrowFileWithRecords();

        int count = guest.getBorrowedBooksCount();

        assertEquals(2, count);
    }

    @Test
    void getBorrowedBooksCountEmpty() throws IOException {
        createEmptyBorrowFile();

        int count = guest.getBorrowedBooksCount();

        assertEquals(0, count);
    }

    private void createEmptyUsersFile() throws IOException {
        ObjectNode root = mapper.createObjectNode();
        root.set("users", mapper.createArrayNode());
        mapper.writeValue(usersFile, root);
    }

    private void createUsersFileWithUser() throws IOException {
        ObjectNode root = mapper.createObjectNode();
        ArrayNode users = mapper.createArrayNode();

        ObjectNode user = mapper.createObjectNode();
        user.put("username", "existinguser");
        user.put("password", "oldpass");
        user.put("role", "student");
        users.add(user);

        root.set("users", users);
        mapper.writeValue(usersFile, root);
    }

    private void createEmptyBorrowFile() throws IOException {
        ObjectNode root = mapper.createObjectNode();
        root.set("borrowRecords", mapper.createArrayNode());
        mapper.writeValue(borrowFile, root);
    }

    private void createBorrowFileWithRecords() throws IOException {
        ObjectNode root = mapper.createObjectNode();
        ArrayNode records = mapper.createArrayNode();

        // 2 record با returnDate null
        ObjectNode record1 = mapper.createObjectNode();
        record1.putNull("returnDate");
        records.add(record1);

        ObjectNode record2 = mapper.createObjectNode();
        record2.putNull("returnDate");
        records.add(record2);

        // 1 record با returnDate پر
        ObjectNode record3 = mapper.createObjectNode();
        record3.put("returnDate", "2024-01-01");
        records.add(record3);

        root.set("borrowRecords", records);
        mapper.writeValue(borrowFile, root);
    }
}