package users;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class StudentTest {

    @TempDir
    Path tempDir;

    private ObjectMapper mapper;
    private File requestsFile;
    private File usersFile;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();

        requestsFile = tempDir.resolve("requests.json").toFile();
        usersFile = tempDir.resolve("users.json").toFile();

        Student.setRequestFilePath(requestsFile.getAbsolutePath());
        Student.setUsersFilePath(usersFile.getAbsolutePath());
    }

    @Test
    void requestBookActiveStudent() throws IOException {
        createEmptyRequestsFile();
        Student student = new Student("activestudent", "pass123", true);

        student.requestBook("ISBN123", LocalDate.now().plusDays(7).toString());

        ObjectNode root = (ObjectNode) mapper.readTree(requestsFile);
        ArrayNode requests = (ArrayNode) root.get("bookRequests");
        assertEquals(1, requests.size());
        assertEquals("activestudent", requests.get(0).get("username").asText());
        assertEquals("ISBN123", requests.get(0).get("isbn").asText());
    }

    @Test
    void requestBookInactiveStudent() throws IOException {
        createEmptyRequestsFile();
        Student student = new Student("inactivestudent", "pass123", false);

        student.requestBook("ISBN123", LocalDate.now().plusDays(7).toString());

        ObjectNode root = (ObjectNode) mapper.readTree(requestsFile);
        ArrayNode requests = (ArrayNode) root.get("bookRequests");
        assertEquals(0, requests.size());
    }

    @Test
    void countStudents() throws IOException {
        createUsersFileWithStudents();

        int count = Student.countStudents();

        assertEquals(2, count);
    }

    @Test
    void toggleStudentStatus() throws IOException {
        createUsersFileWithStudents();

        Student.toggleStudentStatus("student1");

        ObjectNode root = (ObjectNode) mapper.readTree(usersFile);
        ArrayNode users = (ArrayNode) root.get("users");

        boolean found = false;
        for (JsonNode user : users) {
            if (user.get("username").asText().equals("student1")) {
                assertFalse(user.get("isActive").asBoolean());
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    void toggleStudentStatusNotFound() throws IOException {
        createUsersFileWithStudents();

        Student.toggleStudentStatus("nonexistent");

        ObjectNode root = (ObjectNode) mapper.readTree(usersFile);
        ArrayNode users = (ArrayNode) root.get("users");

        boolean student1Active = true;
        boolean student2Active = false;
        for (JsonNode user : users) {
            if (user.get("username").asText().equals("student1")) {
                student1Active = user.get("isActive").asBoolean();
            }
            if (user.get("username").asText().equals("student2")) {
                student2Active = user.get("isActive").asBoolean();
            }
        }
        assertTrue(student1Active);
        assertFalse(student2Active);
    }

    private void createEmptyRequestsFile() throws IOException {
        ObjectNode root = mapper.createObjectNode();
        root.set("bookRequests", mapper.createArrayNode());
        mapper.writeValue(requestsFile, root);
    }

    private void createUsersFileWithStudents() throws IOException {
        ObjectNode root = mapper.createObjectNode();
        ArrayNode users = mapper.createArrayNode();

        ObjectNode student1 = mapper.createObjectNode();
        student1.put("username", "student1");
        student1.put("password", "pass1");
        student1.put("role", "student");
        student1.put("isActive", true);
        users.add(student1);

        ObjectNode student2 = mapper.createObjectNode();
        student2.put("username", "student2");
        student2.put("password", "pass2");
        student2.put("role", "student");
        student2.put("isActive", false);
        users.add(student2);

        ObjectNode employee = mapper.createObjectNode();
        employee.put("username", "employee1");
        employee.put("password", "pass3");
        employee.put("role", "employee");
        users.add(employee);

        root.set("users", users);
        mapper.writeValue(usersFile, root);
    }
}