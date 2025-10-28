import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BorrowRecord {

    private static final String BOOKS_FILE_PATH = "src/main/resources/books.json";
    private static final String BORROW_FILE_PATH = "src/main/resources/borrow_records.json";
    private static final String USERS_FILE_PATH = "src/main/resources/users.json";

    private static final ObjectMapper mapper = new ObjectMapper();

    public static int getTotalBorrowedBooks() {
        try {
            File file = new File(BORROW_FILE_PATH);
            if (!file.exists()) {
                System.out.println("No borrow record file found.");
                return 0;
            }

            JsonNode root = mapper.readTree(file);
            JsonNode records = root.get("borrowRecords");

            if (records == null || !records.isArray()) {
                System.out.println("No borrow records available.");
                return 0;
            }

            return records.size();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static void showLastThreeBorrowedBooks() {
        try {
            File borrowFile = new File(BORROW_FILE_PATH);
            if (!borrowFile.exists()) {
                System.out.println("No borrowed books found.");
                return;
            }

            JsonNode root = mapper.readTree(borrowFile);
            ArrayNode borrowArray = (ArrayNode) root.get("borrowRecords");
            if (borrowArray == null || borrowArray.size() == 0) {
                System.out.println("No borrowed books found.");
                return;
            }

            int start = Math.max(0, borrowArray.size() - 3);
            System.out.println("📚 Last 3 borrowed books:");
            for (int i = borrowArray.size() - 1; i >= start; i--) {
                JsonNode record = borrowArray.get(i);
                System.out.println("Username: " + record.get("username").asText());
                System.out.println("ISBN    : " + record.get("isbn").asText());
                System.out.println("Issue   : " + record.get("issueDate").asText());
                System.out.println("Return  : " + (record.get("returnDate").isNull() ? "Not returned" : record.get("returnDate").asText()));
                System.out.println("----------------------------------");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showStudentBorrowHistory(String studentUsername) {
        try {
            File file = new File(BORROW_FILE_PATH);
            if (!file.exists()) {
                System.out.println("No borrow records found.");
                return;
            }

            JsonNode root = mapper.readTree(file);
            ArrayNode borrowArray = (ArrayNode) root.get("borrowRecords");
            if (borrowArray == null || borrowArray.isEmpty()) {
                System.out.println("No borrow records available.");
                return;
            }

            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
            int totalBorrows = 0;
            int unreturnedCount = 0;
            int delayedCount = 0;

            System.out.println("📖 Borrow History for Student: " + studentUsername);
            System.out.println("--------------------------------------------------");

            for (JsonNode record : borrowArray) {
                if (record.get("username").asText().equalsIgnoreCase(studentUsername)) {
                    totalBorrows++;

                    String isbn = record.get("isbn").asText();
                    String issueDate = record.get("issueDate").asText();
                    String dueDate = record.get("dueDate").asText();
                    String returnDate = record.get("returnDate").isNull() ? "Not Returned" : record.get("returnDate").asText();
                    String issuedBy = record.has("issuedBy") ? record.get("issuedBy").asText() : "Unknown";
                    String receivedBy = record.has("receivedBy") && !record.get("receivedBy").isNull() ? record.get("receivedBy").asText() : "-";

                    if (record.get("returnDate").isNull()) unreturnedCount++;

                    if (!record.get("returnDate").isNull()) {
                        LocalDate due = LocalDate.parse(dueDate, formatter);
                        LocalDate returned = LocalDate.parse(record.get("returnDate").asText(), formatter);
                        if (returned.isAfter(due)) delayedCount++;
                    }

                    System.out.println("📌 ISBN       : " + isbn);
                    System.out.println("    Issue Date: " + issueDate);
                    System.out.println("    Due Date  : " + dueDate);
                    System.out.println("    Return Date: " + returnDate);
                    System.out.println("    Issued By : " + issuedBy);
                    System.out.println("    Received By: " + receivedBy);
                    System.out.println("--------------------------------------------------");
                }
            }

            System.out.println("📊 Statistics:");
            System.out.println("Total Borrows      : " + totalBorrows);
            System.out.println("Unreturned Books   : " + unreturnedCount);
            System.out.println("Delayed Returns    : " + delayedCount);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
