package users;

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

class EmployeeTest {

    @TempDir
    Path tempDir;

    private Employee employee;
    private ObjectMapper mapper;
    private File booksFile;
    private File borrowFile;
    private File usersFile;
    private File requestsFile;

    @BeforeEach
    void setUp() {
        employee = new Employee("testemp", "testpass");
        mapper = new ObjectMapper();

        booksFile = tempDir.resolve("books.json").toFile();
        borrowFile = tempDir.resolve("borrow.json").toFile();
        usersFile = tempDir.resolve("users.json").toFile();
        requestsFile = tempDir.resolve("requests.json").toFile();

        Employee.setBooksFilePath(booksFile.getAbsolutePath());
        Employee.setBorrowFilePath(borrowFile.getAbsolutePath());
        Employee.setUsersFilePath(usersFile.getAbsolutePath());
        Employee.setRequestsFilePath(requestsFile.getAbsolutePath());
    }

    @Test
    void addBookSuccess() throws IOException {
        createEmptyBooksFile();

        employee.addBook("Test Book", "Test Author", 2024, "ISBN123");

        ObjectNode root = (ObjectNode) mapper.readTree(booksFile);
        ArrayNode books = (ArrayNode) root.get("books");
        assertEquals(1, books.size());
        assertEquals("Test Book", books.get(0).get("title").asText());
        assertEquals("Test Author", books.get(0).get("author").asText());
    }

    @Test
    void addBookDuplicateIsbn() throws IOException {
        createBooksFileWithBook("EXISTING123", "Original Title", "Original Author");

        employee.addBook("New Title", "New Author", 2025, "EXISTING123");

        ObjectNode root = (ObjectNode) mapper.readTree(booksFile);
        ArrayNode books = (ArrayNode) root.get("books");
        assertEquals(1, books.size());
        assertEquals("Original Title", books.get(0).get("title").asText());
    }

    @Test
    void editBookSuccess() throws IOException {
        createBooksFileWithBook("EDIT123", "Original Title", "Original Author");

        employee.editBook("EDIT123", "Updated Title", "Updated Author", 2026, false);

        ObjectNode root = (ObjectNode) mapper.readTree(booksFile);
        ArrayNode books = (ArrayNode) root.get("books");
        assertEquals("Updated Title", books.get(0).get("title").asText());
        assertEquals("Updated Author", books.get(0).get("author").asText());
        assertFalse(books.get(0).get("isAvailable").asBoolean());
    }

    @Test
    void editBookNotFound() throws IOException {
        createEmptyBooksFile();

        employee.editBook("NONEXISTENT", "New Title", "New Author", 2026, true);

        ObjectNode root = (ObjectNode) mapper.readTree(booksFile);
        ArrayNode books = (ArrayNode) root.get("books");
        assertEquals(0, books.size());
    }

    @Test
    void receiveBookFromStudent() throws IOException {
        createBooksFileWithBook("BORROW123", "Borrowed Book", "Test Author");
        createBorrowFileWithActiveRecord("student1", "BORROW123");

        employee.receiveBookFromStudent("student1", "BORROW123");

        ObjectNode borrowRoot = (ObjectNode) mapper.readTree(borrowFile);
        ArrayNode records = (ArrayNode) borrowRoot.get("borrowRecords");
        assertFalse(records.get(0).get("returnDate").isNull());
        assertEquals("testemp", records.get(0).get("receivedBy").asText());

        ObjectNode booksRoot = (ObjectNode) mapper.readTree(booksFile);
        ArrayNode books = (ArrayNode) booksRoot.get("books");
        assertTrue(books.get(0).get("isAvailable").asBoolean());
    }

    @Test
    void receiveBookNotFound() throws IOException {
        createEmptyBooksFile();
        createEmptyBorrowFile();

        employee.receiveBookFromStudent("nonexistent", "NONEXISTENT");

        ObjectNode borrowRoot = (ObjectNode) mapper.readTree(borrowFile);
        ArrayNode records = (ArrayNode) borrowRoot.get("borrowRecords");
        assertEquals(0, records.size());
    }

    @Test
    void changePassword() throws IOException {
        createUsersFileWithEmployee();

        employee.changePassword("newSecurePassword");

        ObjectNode root = (ObjectNode) mapper.readTree(usersFile);
        ArrayNode users = (ArrayNode) root.get("users");
        assertEquals("newSecurePassword", users.get(0).get("password").asText());
        assertEquals("testemp", users.get(0).get("username").asText());
    }

    @Test
    void changePasswordEmployeeNotFound() throws IOException {
        createEmptyUsersFile();

        employee.changePassword("newPassword");

        ObjectNode root = (ObjectNode) mapper.readTree(usersFile);
        ArrayNode users = (ArrayNode) root.get("users");
        assertEquals(0, users.size());
    }

    @Test
    void testGetSetEmployeeId() {
        employee.setEmployeeId(9999);
        assertEquals(9999, employee.getEmployeeId());
    }

    private void createEmptyBooksFile() throws IOException {
        ObjectNode root = mapper.createObjectNode();
        root.set("books", mapper.createArrayNode());
        mapper.writeValue(booksFile, root);
    }

    private void createBooksFileWithBook(String isbn, String title, String author) throws IOException {
        ObjectNode root = mapper.createObjectNode();
        ArrayNode books = mapper.createArrayNode();

        ObjectNode book = mapper.createObjectNode();
        book.put("title", title);
        book.put("author", author);
        book.put("year", 2023);
        book.put("isbn", isbn);
        book.put("isAvailable", true);
        book.put("registeredBy", "testemp");
        books.add(book);

        root.set("books", books);
        mapper.writeValue(booksFile, root);
    }

    private void createEmptyBorrowFile() throws IOException {
        ObjectNode root = mapper.createObjectNode();
        root.set("borrowRecords", mapper.createArrayNode());
        mapper.writeValue(borrowFile, root);
    }

    private void createBorrowFileWithActiveRecord(String username, String isbn) throws IOException {
        ObjectNode root = mapper.createObjectNode();
        ArrayNode records = mapper.createArrayNode();

        ObjectNode record = mapper.createObjectNode();
        record.put("username", username);
        record.put("isbn", isbn);
        record.put("issuedBy", "otherEmployee");
        record.putNull("receivedBy");
        record.put("issueDate", LocalDate.now().toString());
        record.put("dueDate", LocalDate.now().plusDays(7).toString());
        record.putNull("returnDate");
        records.add(record);

        root.set("borrowRecords", records);
        mapper.writeValue(borrowFile, root);
    }

    private void createUsersFileWithEmployee() throws IOException {
        ObjectNode root = mapper.createObjectNode();
        ArrayNode users = mapper.createArrayNode();

        ObjectNode user = mapper.createObjectNode();
        user.put("username", "testemp");
        user.put("password", "oldpassword");
        user.put("role", "employee");
        users.add(user);

        root.set("users", users);
        mapper.writeValue(usersFile, root);
    }

    private void createEmptyUsersFile() throws IOException {
        ObjectNode root = mapper.createObjectNode();
        root.set("users", mapper.createArrayNode());
        mapper.writeValue(usersFile, root);
    }
}