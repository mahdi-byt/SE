import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class User {

    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static User login(String username, String password) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            File file = new File("src/main/resources/users.json");
            JsonNode root = mapper.readTree(file);
            JsonNode users = root.get("users");

            for (JsonNode userNode : users) {
                String u = userNode.get("username").asText();
                String p = userNode.get("password").asText();
                String role = userNode.get("role").asText();

                if (username.equals(u) && password.equals(p)) {
                    switch (role.toLowerCase()) {
                        case "student":
                            return new Student(u, p);
                        case "admin":
                            return new Admin(u, p);
                        case "employee":
                            return new Employee(u, p);
                        default:
                            return null;
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
