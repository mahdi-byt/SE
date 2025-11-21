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

class BorrowRecordTest {

    @TempDir
    Path tempDir;

    private ObjectMapper mapper;
    private File borrowFile;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private PrintStream originalOut;

    @BeforeEach
    void setUp() throws IOException {
        mapper = new ObjectMapper();
        borrowFile = tempDir.resolve("borrow.json").toFile();

        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        BorrowRecord.setBorrowFilePath(borrowFile.getAbsolutePath());
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void getTotalBorrowedBooksEmpty() throws IOException {
        createEmptyBorrowFile();

        int count = BorrowRecord.getTotalBorrowedBooks();

        assertEquals(0, count);
    }

    @Test
    void getTotalBorrowedBooksWithRecords() throws IOException {
        createBorrowFileWithRecords();

        int count = BorrowRecord.getTotalBorrowedBooks();

        assertEquals(3, count);
    }

    @Test
    void showLastThreeBorrowedBooks() throws IOException {
        createBorrowFileWithRecords();

        BorrowRecord.showLastThreeBorrowedBooks();

        String output = outputStream.toString();
        assertTrue(output.contains("user3"));
        assertTrue(output.contains("user2"));
        assertTrue(output.contains("user1"));
    }

    @Test
    void showStudentBorrowHistory() throws IOException {
        createBorrowFileWithRecords();

        BorrowRecord.showStudentBorrowHistory("user1");

        String output = outputStream.toString();
        assertTrue(output.contains("user1"));
        assertTrue(output.contains("ISBN001"));
    }

    private void createEmptyBorrowFile() throws IOException {
        ObjectNode root = mapper.createObjectNode();
        root.set("borrowRecords", mapper.createArrayNode());
        mapper.writeValue(borrowFile, root);
    }

    private void createBorrowFileWithRecords() throws IOException {
        ObjectNode root = mapper.createObjectNode();
        ArrayNode records = mapper.createArrayNode();

        for (int i = 1; i <= 3; i++) {
            ObjectNode record = mapper.createObjectNode();
            record.put("username", "user" + i);
            record.put("isbn", "ISBN00" + i);
            record.put("issueDate", "2024-01-0" + i);
            record.put("dueDate", "2024-01-1" + i);
            record.putNull("returnDate");
            records.add(record);
        }

        root.set("borrowRecords", records);
        mapper.writeValue(borrowFile, root);
    }
}