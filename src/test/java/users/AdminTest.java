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

class AdminTest {

    @TempDir
    Path tempDir;

    private Admin admin;
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        admin = new Admin("admin", "admin123");
        mapper = new ObjectMapper();
    }

    @Test
    void createEmployeeSuccess() throws IOException {
        File usersFile = createEmptyUsersFile();
        Admin.setUsersFilePath(usersFile.getAbsolutePath());

        boolean result = admin.createEmployee("newemp", "emppass");

        assertTrue(result);
        ObjectNode root = (ObjectNode) mapper.readTree(usersFile);
        ArrayNode users = (ArrayNode) root.get("users");
        assertEquals(1, users.size());
        assertEquals("newemp", users.get(0).get("username").asText());
        assertEquals("employee", users.get(0).get("role").asText());
    }

    @Test
    void createEmployeeDuplicateUsername() throws IOException {
        File usersFile = createUsersFileWithEmployee();
        Admin.setUsersFilePath(usersFile.getAbsolutePath());

        boolean result = admin.createEmployee("existing", "newpass");

        assertFalse(result);

        ObjectNode root = (ObjectNode) mapper.readTree(usersFile);
        ArrayNode users = (ArrayNode) root.get("users");
        assertEquals(1, users.size());
        assertEquals("existing", users.get(0).get("username").asText());
    }

    @Test
    void viewEmployeePerformance() throws IOException {
        File booksFile = tempDir.resolve("books.json").toFile();
        File borrowFile = tempDir.resolve("borrow.json").toFile();

        createBooksFile(booksFile);
        createBorrowFile(borrowFile);

        Admin.setBooksFilePath(booksFile.getAbsolutePath());
        Admin.setBorrowFilePath(borrowFile.getAbsolutePath());

        admin.viewEmployeePerformance("emp1");
    }

    @Test
    void viewBorrowStatistics() throws IOException {
        File requestsFile = tempDir.resolve("requests.json").toFile();
        File borrowFile = tempDir.resolve("borrow.json").toFile();

        createRequestsFile(requestsFile);
        createBorrowFile(borrowFile);

        Admin.setRequestsFilePath(requestsFile.getAbsolutePath());
        Admin.setBorrowFilePath(borrowFile.getAbsolutePath());

        admin.viewBorrowStatistics();
    }

    @Test
    void viewTop10LateStudents() throws IOException {
        File borrowFile = tempDir.resolve("borrow.json").toFile();
        createBorrowFileWithLateReturns(borrowFile);

        Admin.setBorrowFilePath(borrowFile.getAbsolutePath());

        admin.viewTop10LateStudents();
    }

    private File createEmptyUsersFile() throws IOException {
        File usersFile = tempDir.resolve("users.json").toFile();
        ObjectNode root = mapper.createObjectNode();
        root.set("users", mapper.createArrayNode());
        mapper.writeValue(usersFile, root);
        return usersFile;
    }

    private File createUsersFileWithEmployee() throws IOException {
        File usersFile = tempDir.resolve("users.json").toFile();
        ObjectNode root = mapper.createObjectNode();
        ArrayNode users = mapper.createArrayNode();

        ObjectNode employee = mapper.createObjectNode();
        employee.put("username", "existing");
        employee.put("password", "pass");
        employee.put("role", "employee");
        users.add(employee);

        root.set("users", users);
        mapper.writeValue(usersFile, root);
        return usersFile;
    }

    private void createBooksFile(File booksFile) throws IOException {
        ObjectNode root = mapper.createObjectNode();
        ArrayNode books = mapper.createArrayNode();

        ObjectNode book = mapper.createObjectNode();
        book.put("registeredBy", "emp1");
        books.add(book);

        root.set("books", books);
        mapper.writeValue(booksFile, root);
    }

    private void createBorrowFile(File borrowFile) throws IOException {
        ObjectNode root = mapper.createObjectNode();
        ArrayNode records = mapper.createArrayNode();

        ObjectNode record = mapper.createObjectNode();
        record.put("username", "student1");
        record.put("isbn", "12345");
        record.put("issuedBy", "emp1");
        record.put("receivedBy", "emp1");
        record.put("issueDate", "2024-01-01");
        record.put("dueDate", "2024-01-08");
        record.put("returnDate", "2024-01-10");
        records.add(record);

        root.set("borrowRecords", records);
        mapper.writeValue(borrowFile, root);
    }

    private void createBorrowFileWithLateReturns(File borrowFile) throws IOException {
        ObjectNode root = mapper.createObjectNode();
        ArrayNode records = mapper.createArrayNode();

        ObjectNode record1 = mapper.createObjectNode();
        record1.put("username", "student1");
        record1.put("dueDate", "2024-01-01");
        record1.put("returnDate", "2024-01-10");
        records.add(record1);

        ObjectNode record2 = mapper.createObjectNode();
        record2.put("username", "student2");
        record2.put("dueDate", "2024-01-01");
        record2.put("returnDate", "2024-01-05");
        records.add(record2);

        root.set("borrowRecords", records);
        mapper.writeValue(borrowFile, root);
    }

    private void createRequestsFile(File requestsFile) throws IOException {
        ObjectNode root = mapper.createObjectNode();
        ArrayNode requests = mapper.createArrayNode();

        ObjectNode request = mapper.createObjectNode();
        request.put("username", "student1");
        request.put("isbn", "12345");
        request.put("requestDate", "2024-01-01");
        request.put("suggestedDeliveryDate", "2024-01-08");
        requests.add(request);

        root.set("bookRequests", requests);
        mapper.writeValue(requestsFile, root);
    }
}