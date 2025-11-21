package users;
import books.*;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

class Employee extends User {
    private int employeeId;

    public Employee(String username, String password) {
        super(username, password);
    }

    private static String USERS_FILE_PATH = "src/main/resources/users.json";
    private static String BOOKS_FILE_PATH = "src/main/resources/books.json";
    private static String BORROW_FILE_PATH = "src/main/resources/borrow_records.json";
    private static String REQUESTS_FILE_PATH = "src/main/resources/book_requests.json";
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Scanner scanner = new Scanner(System.in);

    public static void setUsersFilePath(String path) {
        USERS_FILE_PATH = path;
    }

    public static void setBooksFilePath(String path) {
        BOOKS_FILE_PATH = path;
    }

    public static void setBorrowFilePath(String path) {
        BORROW_FILE_PATH = path;
    }

    public static void setRequestsFilePath(String path) {
        REQUESTS_FILE_PATH = path;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    @Override
    public void userMenu() {
        int choice = -1;

        while (choice != 0) {
            System.out.println("\n====== 👨‍💼 Users.Employee Menu ======");
            System.out.println("1. Change Password");
            System.out.println("2. Add Books.Book");
            System.out.println("3. Search Books.Book by Title");
            System.out.println("4. Search Books.Book by Author");
            System.out.println("5. Search Books.Book by Year");
            System.out.println("6. Edit Books.Book Information");
            System.out.println("7. Check & Approve Borrow Requests");
            System.out.println("8. View Borrow History Report");
            System.out.println("9. Activate/Deactivate Users.Student");
            System.out.println("10. Receive Borrowed Books.Book");
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
                    this.changePasswordWithInput();
                    break;

                case 2:
                    System.out.print("Enter Books.Book Title: ");
                    String title = scanner.nextLine();
                    System.out.print("Enter Author Name: ");
                    String author = scanner.nextLine();
                    System.out.print("Enter Year of Publication: ");
                    int year = Integer.parseInt(scanner.nextLine());
                    System.out.print("Enter ISBN: ");
                    String isbn = scanner.nextLine();
                    this.addBook(title, author, year, isbn);
                    break;

                case 3:
                    System.out.print("Enter book title to search: ");
                    Book.searchBookByTitle(scanner.nextLine());
                    break;

                case 4:
                    System.out.print("Enter author name to search: ");
                    Book.searchBookByAuthor(scanner.nextLine());
                    break;

                case 5:
                    System.out.print("Enter publication year to search: ");
                    int searchYear = Integer.parseInt(scanner.nextLine());
                    Book.searchBookByYear(searchYear);
                    break;

                case 6:
                    System.out.print("Enter ISBN of the book to edit: ");
                    String editIsbn = scanner.nextLine();

                    System.out.print("Enter new Title: ");
                    String newTitle = scanner.nextLine();

                    System.out.print("Enter new Author: ");
                    String newAuthor = scanner.nextLine();

                    System.out.print("Enter new Year of Publication: ");
                    int newYear;
                    try {
                        newYear = Integer.parseInt(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("❌ Invalid year format!");
                        break;
                    }

                    System.out.print("Is the book available? (true/false): ");
                    boolean newAvailability;
                    String availInput = scanner.nextLine();
                    if (availInput.equalsIgnoreCase("true")) {
                        newAvailability = true;
                    } else if (availInput.equalsIgnoreCase("false")) {
                        newAvailability = false;
                    } else {
                        System.out.println("❌ Invalid input for availability!");
                        break;
                    }

                    this.editBook(editIsbn, newTitle, newAuthor, newYear, newAvailability);
                    break;


                case 7:
                    this.reviewAndApproveRequests();
                    break;

                case 8:
                    System.out.print("Enter student's username to view history: ");
                    String studentUsername = scanner.nextLine();
                    BorrowRecord.showStudentBorrowHistory(studentUsername);
                    break;

                case 9:
                    Student.toggleStudentStatus();
                    break;

                case 10:
                    System.out.print("Enter student's username: ");
                    String stuUser = scanner.nextLine();
                    System.out.print("Enter ISBN of the book: ");
                    String recvIsbn = scanner.nextLine();
                    this.receiveBookFromStudent(stuUser, recvIsbn);
                    break;

                case 0:
                    System.out.println("👋 Exiting Users.Employee Menu...");
                    break;

                default:
                    System.out.println("❌ Invalid choice. Try again.");
            }
        }
    }
    public void changePassword() {
        changePassword("newPassword123");
    }

    public void changePassword(String newPassword) {
        try {
            File usersFile = new File(USERS_FILE_PATH);
            if (!usersFile.exists()) {
                System.out.println("❌ User file not found.");
                return;
            }

            JsonNode root = mapper.readTree(usersFile);
            ArrayNode usersArray = (ArrayNode) root.get("users");

            boolean updated = false;
            for (JsonNode userNode : usersArray) {
                if (userNode.get("username").asText().equalsIgnoreCase(this.getUsername())
                        && userNode.get("role").asText().equalsIgnoreCase("employee")) {

                    ((ObjectNode) userNode).put("password", newPassword);
                    this.setPassword(newPassword);
                    updated = true;
                    break;
                }
            }

            if (updated) {
                mapper.writerWithDefaultPrettyPrinter().writeValue(usersFile, root);
                System.out.println("✅ Password changed successfully.");
            } else {
                System.out.println("❌ Employee record not found.");
            }

        } catch (IOException e) {
            System.out.println("❌ Error changing password: " + e.getMessage());
        }
    }

    public void changePasswordWithInput() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter new password: ");
        String newPassword = scanner.nextLine();
        changePassword(newPassword);
    }

    public void addBook(String title, String author, int year, String isbn) {
        try {
            File booksFile = new File(BOOKS_FILE_PATH);

            ObjectNode root;
            ArrayNode booksArray;


            if (booksFile.exists()) {
                root = (ObjectNode) mapper.readTree(booksFile);
                booksArray = (ArrayNode) root.get("books");
                if (booksArray == null) {
                    booksArray = mapper.createArrayNode();
                    root.set("books", booksArray);
                }
            } else {
                root = mapper.createObjectNode();
                booksArray = mapper.createArrayNode();
                root.set("books", booksArray);
            }

            for (JsonNode bookNode : booksArray) {
                if (bookNode.get("isbn").asText().equals(isbn)) {
                    System.out.println("❌ Books.Book with ISBN " + isbn + " already exists.");
                    return;
                }
            }

            ObjectNode newBook = mapper.createObjectNode();
            newBook.put("title", title);
            newBook.put("author", author);
            newBook.put("year", year);
            newBook.put("isbn", isbn);
            newBook.put("registeredBy", this.getUsername());
            newBook.put("isAvailable", true);

            booksArray.add(newBook);

            mapper.writerWithDefaultPrettyPrinter().writeValue(booksFile, root);
            System.out.println("✅ Books.Book \"" + title + "\" added successfully by " + this.getUsername());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reviewAndApproveRequests() {
        try (Scanner scanner = new Scanner(System.in)) {
            File requestFile = new File(REQUESTS_FILE_PATH);
            File booksFile = new File(BOOKS_FILE_PATH);
            File borrowFile = new File(BORROW_FILE_PATH);

            if (!requestFile.exists()) {
                System.out.println("No book requests found.");
                return;
            }

            ObjectNode requestRoot = (ObjectNode) mapper.readTree(requestFile);
            ArrayNode requestArray = (ArrayNode) requestRoot.get("bookRequests");

            if (requestArray == null || requestArray.isEmpty()) {
                System.out.println("No book requests available.");
                return;
            }

            LocalDate today = LocalDate.now();
            LocalDate yesterday = today.minusDays(1);
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;

            ObjectNode booksRoot = (ObjectNode) mapper.readTree(booksFile);
            ArrayNode booksArray = (ArrayNode) booksRoot.get("books");

            ObjectNode borrowRoot;
            ArrayNode borrowArray;
            if (borrowFile.exists()) {
                borrowRoot = (ObjectNode) mapper.readTree(borrowFile);
                borrowArray = (ArrayNode) borrowRoot.get("borrowRecords");
            } else {
                borrowRoot = mapper.createObjectNode();
                borrowArray = mapper.createArrayNode();
                borrowRoot.set("borrowRecords", borrowArray);
            }

            ArrayNode remainingRequests = mapper.createArrayNode();

            for (JsonNode req : requestArray) {
                LocalDate reqDate = LocalDate.parse(req.get("requestDate").asText(), formatter);
                if (!reqDate.equals(today) && !reqDate.equals(yesterday)) {
                    remainingRequests.add(req);
                    continue;
                }

                String username = req.get("username").asText();
                String isbn = req.get("isbn").asText();
                String dueDateStr = req.get("suggestedDeliveryDate").asText();

                System.out.println("\nUsers.User: " + username + ", ISBN: " + isbn + ", Suggested Delivery: " + dueDateStr);
                System.out.print("Approve this request? (y/n): ");
                String choice = scanner.nextLine().trim().toLowerCase();

                if (choice.equals("y")) {
                    boolean bookAvailable = false;
                    for (JsonNode bookNode : booksArray) {
                        if (bookNode.get("isbn").asText().equals(isbn) &&
                                bookNode.get("isAvailable").asBoolean()) {
                            bookAvailable = true;
                            ((ObjectNode) bookNode).put("isAvailable", false);
                            break;
                        }
                    }

                    if (!bookAvailable) {
                        System.out.println("❌ Books.Book is not available. Skipping request.");
                        remainingRequests.add(req);
                        continue;
                    }

                    ObjectNode newRecord = mapper.createObjectNode();
                    newRecord.put("username", username);
                    newRecord.put("isbn", isbn);
                    newRecord.put("issuedBy", this.getUsername());
                    newRecord.putNull("receivedBy");
                    newRecord.put("issueDate", LocalDate.now().toString());
                    newRecord.put("dueDate", dueDateStr); // افزودن تاریخ پیشنهادی دانشجو
                    newRecord.putNull("returnDate");

                    borrowArray.add(newRecord);
                    System.out.println("✅ Request approved and book issued to " + username);

                } else {
                    System.out.println("❌ Request denied for " + username);
                }
            }

            mapper.writerWithDefaultPrettyPrinter().writeValue(booksFile, booksRoot);
            mapper.writerWithDefaultPrettyPrinter().writeValue(borrowFile, borrowRoot);

            requestRoot.set("bookRequests", remainingRequests);
            mapper.writerWithDefaultPrettyPrinter().writeValue(requestFile, requestRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void editBook(String isbn, String newTitle, String newAuthor, int newYear, boolean newAvailability) {
        try {
            File booksFile = new File(BOOKS_FILE_PATH);
            if (!booksFile.exists()) {
                System.out.println("❌ Book file not found.");
                return;
            }

            ObjectNode root = (ObjectNode) mapper.readTree(booksFile);
            ArrayNode booksArray = (ArrayNode) root.get("books");

            if (booksArray == null) {
                System.out.println("❌ No books found in the file.");
                return;
            }

            boolean found = false;
            for (JsonNode bookNode : booksArray) {
                if (bookNode.get("isbn").asText().equals(isbn)) {
                    ((ObjectNode) bookNode).put("title", newTitle);
                    ((ObjectNode) bookNode).put("author", newAuthor);
                    ((ObjectNode) bookNode).put("year", newYear);
                    ((ObjectNode) bookNode).put("isAvailable", newAvailability);
                    found = true;
                    break;
                }
            }

            if (!found) {
                System.out.println("❌ Book with ISBN " + isbn + " not found.");
                return;
            }

            mapper.writerWithDefaultPrettyPrinter().writeValue(booksFile, root);
            System.out.println("✅ Book with ISBN " + isbn + " has been updated successfully.");

        } catch (IOException e) {
            System.out.println("❌ Error updating book: " + e.getMessage());
        }
    }

    public void receiveBookFromStudent(String studentUsername, String isbn) {
        try {
            File borrowFile = new File(BORROW_FILE_PATH);
            File booksFile = new File(BOOKS_FILE_PATH);

            if (!borrowFile.exists() || !booksFile.exists()) {
                System.out.println("Required data files not found.");
                return;
            }

            JsonNode root = mapper.readTree(borrowFile);
            ArrayNode borrowArray = (ArrayNode) root.get("borrowRecords");

            boolean recordFound = false;

            for (JsonNode record : borrowArray) {
                if (record.get("username").asText().equalsIgnoreCase(studentUsername)
                        && record.get("isbn").asText().equals(isbn)
                        && record.get("returnDate").isNull()) {

                    ((ObjectNode) record).put("returnDate", LocalDate.now().toString());
                    ((ObjectNode) record).put("receivedBy", this.getUsername());
                    recordFound = true;
                    break;
                }
            }

            if (!recordFound) {
                System.out.println("No active borrow record found for this student and ISBN.");
                return;
            }

            mapper.writerWithDefaultPrettyPrinter().writeValue(borrowFile, root);

            JsonNode booksRoot = mapper.readTree(booksFile);
            ArrayNode booksArray = (ArrayNode) booksRoot.get("books");

            for (JsonNode bookNode : booksArray) {
                if (bookNode.get("isbn").asText().equals(isbn)) {
                    ((ObjectNode) bookNode).put("isAvailable", true);
                    break;
                }
            }

            mapper.writerWithDefaultPrettyPrinter().writeValue(booksFile, booksRoot);
            System.out.println("✅ Book received and record updated successfully.");

        } catch (IOException e) {
            System.out.println("❌ Error receiving book: " + e.getMessage());
        }
    }

}