import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;

import java.io.File;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.IOException;
import java.time.LocalDate;

class Student extends User {
    private boolean isActive;

    public Student(String username, String password, boolean isActive) {
        super(username, password);
        this.isActive = isActive;
    }

    @Override
    public void userMenu() {
        Scanner scanner = new Scanner(System.in);
        int choice = -1;

        while (choice != 0) {
            System.out.println("\n====== 📚 User Menu ======");
            System.out.println("1. Search Book by Title");
            System.out.println("2. Search Book by Author");
            System.out.println("3. Search Book by Year");
            System.out.println("4. Request Book");
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
                    System.out.print("Enter book title: ");
                    String title = scanner.nextLine();
                    Book.searchBookByTitle(title);
                    break;

                case 2:
                    System.out.print("Enter author name: ");
                    String author = scanner.nextLine();
                    Book.searchBookByAuthor(author);
                    break;

                case 3:
                    System.out.print("Enter publication year: ");
                    int year;
                    try {
                        year = Integer.parseInt(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("❌ Invalid year format!");
                        break;
                    }
                    Book.searchBookByYear(year);
                    break;

                case 4:
                    System.out.print("Enter ISBN of the book: ");
                    String isbn = scanner.nextLine();
                    System.out.print("Enter proposed return date (YYYY-MM-DD): ");
                    String proposedReturnDate = scanner.nextLine();
                    if (this instanceof Student student) {
                        student.requestBook(isbn, proposedReturnDate);
                    } else {
                        System.out.println("❌ Only students can request books!");
                    }
                    break;

                case 0:
                    System.out.println("👋 Exiting user menu...");
                    break;

                default:
                    System.out.println("❌ Invalid choice. Please try again.");
            }
        }
    }

    private static final String USERS_FILE_PATH = "src/main/resources/users.json";
    private static final String BOOKS_FILE_PATH = "src/main/resources/books.json";
    private static final String REQUESTS_FILE_PATH = "src/main/resources/book_requests.json";
    private static final ObjectMapper mapper = new ObjectMapper();

    public static int countStudents() {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(USERS_FILE_PATH);

        if (!file.exists()) {
            System.out.println("No users found.");
            return 0;
        }

        try {
            ObjectNode root = (ObjectNode) mapper.readTree(file);
            ArrayNode usersArray = (ArrayNode) root.get("users");

            int count = 0;
            for (int i = 0; i < usersArray.size(); i++) {
                if (usersArray.get(i).get("role").asText().equals("student")) {
                    count++;
                }
            }

            System.out.println("Number of students: " + count);
            return count;

        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static void toggleStudentStatus() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter student's username: ");
        String studentUsername = scanner.nextLine();

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
                if (userNode.get("username").asText().equalsIgnoreCase(studentUsername)
                        && userNode.get("role").asText().equalsIgnoreCase("student")) {

                    boolean isActive = userNode.has("isActive") && userNode.get("isActive").asBoolean();
                    ((ObjectNode) userNode).put("isActive", !isActive);
                    updated = true;

                    System.out.println("✅ Student " + studentUsername + " is now " + (!isActive ? "active" : "inactive") + ".");
                    break;
                }
            }

            if (updated) {
                mapper.writerWithDefaultPrettyPrinter().writeValue(usersFile, root);
            } else {
                System.out.println("❌ Student not found.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void requestBook(String isbn, String suggestedDeliveryDate) {
        if (!isActive) {
            System.out.println("❌ Your account is not active. Cannot request books.");
            return;
        }

        try {
            File requestFile = new File(REQUESTS_FILE_PATH);
            ObjectNode root;
            ArrayNode requestsArray;

            if (requestFile.exists()) {
                root = (ObjectNode) mapper.readTree(requestFile);
                requestsArray = (ArrayNode) root.get("bookRequests");
                if (requestsArray == null) {
                    requestsArray = mapper.createArrayNode();
                    root.set("bookRequests", requestsArray);
                }
            } else {
                root = mapper.createObjectNode();
                requestsArray = mapper.createArrayNode();
                root.set("bookRequests", requestsArray);
            }

            ObjectNode newRequest = mapper.createObjectNode();
            newRequest.put("username", this.getUsername());
            newRequest.put("isbn", isbn);
            newRequest.put("requestDate", LocalDate.now().toString());
            newRequest.put("suggestedDeliveryDate", suggestedDeliveryDate);

            requestsArray.add(newRequest);

            mapper.writerWithDefaultPrettyPrinter().writeValue(requestFile, root);

            System.out.println("✅ Book request registered successfully for ISBN: " + isbn);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}