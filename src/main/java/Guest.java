import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;

class Guest extends User {
    public Guest(String username, String password) {
        super(username, password);
    }
    private static final String USERS_FILE_PATH = "src/main/resources/users.json";
    public boolean register(String username, String password) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            File file = new File(USERS_FILE_PATH);
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
            newUser.put("role", "student");

            usersArray.add(newUser);

            mapper.writerWithDefaultPrettyPrinter().writeValue(file, root);

            System.out.println("User registered successfully!");
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int countStudents() {
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

}