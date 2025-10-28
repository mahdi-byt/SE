import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;


class Admin extends User {
    private String adminLevel;

    public Admin(String username, String password){
        super(username, password);
    }

    private static final String USERS_FILE_PATH = "src/main/resources/users.json";
    private static final String BOOKS_FILE_PATH = "src/main/resources/books.json";
    private static final String BORROW_FILE_PATH = "src/main/resources/borrow_records.json";
    private static final String REQUESTS_FILE_PATH = "src/main/resources/book_requests.json";
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Scanner scanner = new Scanner(System.in);

    public void userMenu() {
        int choice = -1;

        while (choice != 0) {
            System.out.println("\n====== 🛡 Admin Menu ======");
            System.out.println("1. Add New Employee");
            System.out.println("2. View Employee Performance");
            System.out.println("3. View Borrow Statistics");
            System.out.println("4. Show Student Borrow History");
            System.out.println("5. View Top 10 Late Students");
            System.out.println("0. Exit");
            System.out.print("👉 Enter your choice: ");

            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input! Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1:
                    System.out.print("Enter new employee username: ");
                    String empUsername = scanner.nextLine();
                    System.out.print("Enter new employee password: ");
                    String empPassword = scanner.nextLine();
                    this.createEmployee(empUsername, empPassword);
                    break;

                case 2:
                    System.out.print("Enter employee username to view performance: ");
                    String empToCheck = scanner.nextLine();
                    this.viewEmployeePerformance(empToCheck);
                    break;

                case 3:
                    this.viewBorrowStatistics();
                    break;

                case 4:
                    System.out.print("Enter student's username to view borrow history: ");
                    String studentUsername = scanner.nextLine();
                    BorrowRecord.showStudentBorrowHistory(studentUsername);
                    break;

                case 5:
                    this.viewTop10LateStudents();
                    break;

                case 0:
                    System.out.println("👋 Exiting Admin Menu...");
                    break;

                default:
                    System.out.println("❌ Invalid choice. Try again.");
            }
        }
    }
    public boolean createEmployee(String username, String password) {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(USERS_FILE_PATH);

        try {
            ObjectNode root;
            ArrayNode usersArray;

            if (file.exists()) {
                root = (ObjectNode) mapper.readTree(file);
                usersArray = (ArrayNode) root.get("users");
            } else {
                root = mapper.createObjectNode();
                usersArray = mapper.createArrayNode();
                root.set("users", usersArray);
            }

            for (int i = 0; i < usersArray.size(); i++) {
                if (usersArray.get(i).get("username").asText().equals(username)) {
                    System.out.println("Username already exists!");
                    return false;
                }
            }

            ObjectNode newUser = mapper.createObjectNode();
            newUser.put("username", username);
            newUser.put("password", password);
            newUser.put("role", "employee");

            usersArray.add(newUser);

            mapper.writerWithDefaultPrettyPrinter().writeValue(file, root);

            System.out.println("Employee created successfully!");
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void viewEmployeePerformance(String employeeUsername) {
        try {
            int booksRegistered = 0;
            int booksLent = 0;
            int booksReceived = 0;

            File booksFile = new File(BOOKS_FILE_PATH);
            if (booksFile.exists()) {
                JsonNode booksRoot = mapper.readTree(booksFile);
                ArrayNode booksArray = (ArrayNode) booksRoot.get("books");
                if (booksArray != null) {
                    for (JsonNode book : booksArray) {
                        if (book.has("registeredBy") &&
                                book.get("registeredBy").asText().equalsIgnoreCase(employeeUsername)) {
                            booksRegistered++;
                        }
                    }
                }
            }

            File borrowFile = new File(BORROW_FILE_PATH);
            if (borrowFile.exists()) {
                JsonNode borrowRoot = mapper.readTree(borrowFile);
                ArrayNode borrowArray = (ArrayNode) borrowRoot.get("borrowRecords");
                if (borrowArray != null) {
                    for (JsonNode record : borrowArray) {
                        if (record.has("issuedBy") &&
                                record.get("issuedBy").asText().equalsIgnoreCase(employeeUsername)) {
                            booksLent++;
                        }
                        if (record.has("receivedBy") &&
                                !record.get("receivedBy").isNull() &&
                                record.get("receivedBy").asText().equalsIgnoreCase(employeeUsername)) {
                            booksReceived++;
                        }
                    }
                }
            }

            System.out.println("📊 Performance Report for Employee: " + employeeUsername);
            System.out.println("------------------------------------");
            System.out.println("📘 Books Registered : " + booksRegistered);
            System.out.println("📗 Books Lent       : " + booksLent);
            System.out.println("📙 Books Received   : " + booksReceived);
            System.out.println("------------------------------------");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void viewBorrowStatistics() {
        try {
            int totalRequests = 0;
            int totalBorrows = 0;
            int totalCompleted = 0;
            long totalDays = 0;

            File requestsFile = new File(REQUESTS_FILE_PATH);
            if (requestsFile.exists()) {
                JsonNode reqRoot = mapper.readTree(requestsFile);
                ArrayNode reqArray = (ArrayNode) reqRoot.get("bookRequests");
                if (reqArray != null) totalRequests = reqArray.size();
            }

            File borrowFile = new File(BORROW_FILE_PATH);
            if (borrowFile.exists()) {
                JsonNode borrowRoot = mapper.readTree(borrowFile);
                ArrayNode borrowArray = (ArrayNode) borrowRoot.get("borrowRecords");
                if (borrowArray != null) {
                    totalBorrows = borrowArray.size();
                    for (JsonNode record : borrowArray) {
                        if (!record.get("returnDate").isNull()) {
                            LocalDate issue = LocalDate.parse(record.get("issueDate").asText());
                            LocalDate returned = LocalDate.parse(record.get("returnDate").asText());
                            totalDays += ChronoUnit.DAYS.between(issue, returned);
                            totalCompleted++;
                        }
                    }
                }
            }

            double avgDays = totalCompleted > 0 ? (double) totalDays / totalCompleted : 0;

            System.out.println("📊 Borrow Statistics Report");
            System.out.println("-----------------------------");
            System.out.println("📘 Total Requests Registered : " + totalRequests);
            System.out.println("📗 Total Borrows Issued      : " + totalBorrows);
            System.out.printf("📙 Average Borrow Duration    : %.2f days%n", avgDays);
            System.out.println("-----------------------------");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void viewTop10LateStudents() {
        try {
            File borrowFile = new File(BORROW_FILE_PATH);
            if (!borrowFile.exists()) {
                System.out.println("No borrow records found.");
                return;
            }

            JsonNode root = mapper.readTree(borrowFile);
            ArrayNode borrowArray = (ArrayNode) root.get("borrowRecords");
            if (borrowArray == null || borrowArray.isEmpty()) {
                System.out.println("No borrow records available.");
                return;
            }

            Map<String, Long> delayMap = new HashMap<>();
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;

            for (JsonNode record : borrowArray) {
                if (!record.get("returnDate").isNull() && !record.get("dueDate").isNull()) {
                    LocalDate dueDate = LocalDate.parse(record.get("dueDate").asText(), formatter);
                    LocalDate returnDate = LocalDate.parse(record.get("returnDate").asText(), formatter);

                    if (returnDate.isAfter(dueDate)) {
                        long delayDays = ChronoUnit.DAYS.between(dueDate, returnDate);
                        String username = record.get("username").asText();

                        delayMap.put(username, delayMap.getOrDefault(username, 0L) + delayDays);
                    }
                }
            }

            List<Map.Entry<String, Long>> sortedList = new ArrayList<>(delayMap.entrySet());
            sortedList.sort((a, b) -> Long.compare(b.getValue(), a.getValue()));

            System.out.println("🏆 Top 10 Students with Longest Delays");
            System.out.println("--------------------------------------");

            int limit = Math.min(10, sortedList.size());
            for (int i = 0; i < limit; i++) {
                Map.Entry<String, Long> entry = sortedList.get(i);
                System.out.printf("%2d. %-15s → %d days delay%n", i + 1, entry.getKey(), entry.getValue());
            }

            if (sortedList.isEmpty()) {
                System.out.println("No late returns found.");
            }

            System.out.println("--------------------------------------");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}