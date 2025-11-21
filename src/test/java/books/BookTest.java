package books;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    @TempDir
    Path tempDir;

    private ObjectMapper mapper;
    private File booksFile;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private PrintStream originalOut;

    @BeforeEach
    void setUp() throws IOException {
        mapper = new ObjectMapper();
        booksFile = tempDir.resolve("books.json").toFile();
        createTestBooksFile();

        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void countBooks() {
        int count = Book.countBooks();
        assertEquals(3, count);
    }

    @Test
    void searchBookByTitle() {
        Book.searchBookByTitle("Java");

        String output = outputStream.toString();
        assertTrue(output.contains("Java Programming"));
        assertFalse(output.contains("Python Cookbook"));
    }

    @Test
    void searchBookByAuthor() {
        Book.searchBookByAuthor("Alice");

        String output = outputStream.toString();
        assertTrue(output.contains("Alice"));
        assertFalse(output.contains("Bob"));
    }

    @Test
    void searchBookByYear() {
        Book.searchBookByYear(2023);

        String output = outputStream.toString();
        assertTrue(output.contains("2023"));
        assertFalse(output.contains("2022"));
    }

    private void createTestBooksFile() throws IOException {
        ObjectNode root = mapper.createObjectNode();
        ArrayNode books = mapper.createArrayNode();

        ObjectNode book1 = mapper.createObjectNode();
        book1.put("title", "Java Programming");
        book1.put("author", "Alice");
        book1.put("year", 2023);
        book1.put("isbn", "ISBN001");
        book1.put("isAvailable", true);
        books.add(book1);

        ObjectNode book2 = mapper.createObjectNode();
        book2.put("title", "Python Cookbook");
        book2.put("author", "Bob");
        book2.put("year", 2022);
        book2.put("isbn", "ISBN002");
        book2.put("isAvailable", false);
        books.add(book2);

        ObjectNode book3 = mapper.createObjectNode();
        book3.put("title", "Advanced Java");
        book3.put("author", "Alice");
        book3.put("year", 2024);
        book3.put("isbn", "ISBN003");
        book3.put("isAvailable", true);
        books.add(book3);

        root.set("books", books);
        mapper.writeValue(booksFile, root);

        Book.setBooksFilePath(booksFile.getAbsolutePath());
    }
}