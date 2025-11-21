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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserTest {

    @TempDir
    Path tempDir;

    private ObjectMapper mapper;
    private File testUsersFile;

    @BeforeEach
    void setUp() throws IOException {
        mapper = new ObjectMapper();
        testUsersFile = tempDir.resolve("test_users.json").toFile();

        ObjectNode root = mapper.createObjectNode();
        ArrayNode users = mapper.createArrayNode();

        ObjectNode student = mapper.createObjectNode();
        student.put("username", "student1");
        student.put("password", "pass123");
        student.put("role", "student");
        student.put("isActive", true);
        users.add(student);

        ObjectNode admin = mapper.createObjectNode();
        admin.put("username", "admin1");
        admin.put("password", "admin123");
        admin.put("role", "admin");
        users.add(admin);

        ObjectNode employee = mapper.createObjectNode();
        employee.put("username", "employee1");
        employee.put("password", "emp123");
        employee.put("role", "employee");
        users.add(employee);

        root.set("users", users);
        mapper.writeValue(testUsersFile, root);

        User.USERS_FILE_PATH = testUsersFile.getAbsolutePath();
    }

    @Test
    void loginStudentSuccess() {
        User user = User.login("student1", "pass123");
        assertNotNull(user);
        assertTrue(user instanceof Student);
        assertEquals("student1", user.getUsername());
    }

    @Test
    void loginAdminSuccess() {
        User user = User.login("admin1", "admin123");
        assertNotNull(user);
        assertTrue(user instanceof Admin);
        assertEquals("admin1", user.getUsername());
    }

    @Test
    void loginEmployeeSuccess() {
        User user = User.login("employee1", "emp123");
        assertNotNull(user);
        assertTrue(user instanceof Employee);
        assertEquals("employee1", user.getUsername());
    }

    @Test
    void loginInvalidPassword() {
        User user = User.login("student1", "wrongpass");
        assertNull(user);
    }

    @Test
    void loginUserNotFound() {
        User user = User.login("nonexistent", "pass");
        assertNull(user);
    }
}