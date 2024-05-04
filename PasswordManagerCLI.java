// import java.util.Scanner;
// import java.security.MessageDigest;
// import java.security.NoSuchAlgorithmException;
// import java.security.SecureRandom;
// import java.util.Base64;
// import java.util.HashMap;
// import java.util.Map;


// public class PasswordManagerCLI {
//     private static Map<String, String> passwords = new HashMap<>();
//     public static void addPassword(String username, String password) {
//         byte[] salt = generateSalt();
//         String hashedPassword = hashPassword(password, salt);
//         passwords.put(username, Base64.getEncoder().encodeToString(salt) + ":" + hashedPassword);
//         System.out.println(passwords);
//     }

//     public static void deletePassword(String username) {
//         passwords.remove(username);
//     }
//     public static void main(String[] args) {
       
//         Scanner scanner = new Scanner(System.in);
//         while (true) {
//             System.out.println("1. Add Password");
//             System.out.println("2. Delete Password");
//             System.out.println("3. Exit");
//             System.out.print("Choose an option: ");
//             int choice = scanner.nextInt();
//             scanner.nextLine(); // Consume newline
            
//             switch (choice) {
//                 case 1:
//                     System.out.print("Enter username: ");
//                     String username = scanner.nextLine();
//                     System.out.print("Enter password: ");
//                     String password = scanner.nextLine();
//                     addPassword(username, password);
//                     System.out.println("Password added successfully.");
//                     break;
//                 case 2:
//                     System.out.print("Enter username to delete: ");
//                     String userToDelete = scanner.nextLine();
//                    deletePassword(userToDelete);
                    
//                     System.out.println("Password deleted successfully.");
//                     break;
//                 case 3:
//                     System.out.println("Exiting...");
//                     System.exit(0);
//                 default:
//                     System.out.println("Invalid option. Please try again.");
//             }
        
            
//         }
        
//     }
//     public static boolean checkPassword(String username, String password) {
//         if (!passwords.containsKey(username))
//             return false;
//         String storedPassword = passwords.get(username);
//         String[] parts = storedPassword.split(":");
//         byte[] salt = Base64.getDecoder().decode(parts[0]);
//         String hashedPassword = hashPassword(password, salt);
//         return parts[1].equals(hashedPassword);
//     }

//     private static byte[] generateSalt() {
//         SecureRandom random = new SecureRandom();
//         byte[] salt = new byte[16];
//         random.nextBytes(salt);
//         return salt;
//     }

//     private static String hashPassword(String password, byte[] salt) {
//         try {
//             MessageDigest md = MessageDigest.getInstance("SHA-256");
//             md.update(salt);
//             byte[] hashedPassword = md.digest(password.getBytes());
//             return Base64.getEncoder().encodeToString(hashedPassword);
//         } catch (NoSuchAlgorithmException e) {
//             e.printStackTrace();
//             return null;
//         }
//     }
// }








import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class PasswordManagerCLI {
    private static final String PASSWORD_FILE = "passwords.txt";
    private static final String SALT_SEPARATOR = "::";

    private static Map<String, String> passwords = new HashMap<>();

    public static void addPassword(String username, String password) {
        byte[] salt = generateSalt();
        String hashedPassword = hashPassword(password, salt);
        passwords.put(username, Base64.getEncoder().encodeToString(salt) + SALT_SEPARATOR + hashedPassword);
        savePasswordsToFile();
        System.out.println("Password added successfully.");
    }

    public static void deletePassword(String username) {
        passwords.remove(username);
        savePasswordsToFile();
        System.out.println("Password deleted successfully.");
    }

    private static void loadPasswordsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(PASSWORD_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(SALT_SEPARATOR);
                passwords.put(parts[0], parts[1]);
            }
        } catch (IOException e) {
            System.err.println("Error loading passwords from file: " + e.getMessage());
        }
    }

    private static void savePasswordsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PASSWORD_FILE))) {
            for (Map.Entry<String, String> entry : passwords.entrySet()) {
                writer.write(entry.getKey() + SALT_SEPARATOR + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving passwords to file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        loadPasswordsFromFile();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("1. Add Password");
            System.out.println("2. Delete Password");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter username: ");
                    String username = scanner.nextLine();
                    System.out.print("Enter password: ");
                    String password = scanner.nextLine();
                    addPassword(username, password);
                    break;
                case 2:
                    System.out.print("Enter username to delete: ");
                    String userToDelete = scanner.nextLine();
                    deletePassword(userToDelete);
                    break;
                case 3:
                    System.out.println("Exiting...");
                    System.exit(0);
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    public static boolean checkPassword(String username, String password) {
        if (!passwords.containsKey(username))
            return false;
        String storedPassword = passwords.get(username);
        String[] parts = storedPassword.split(SALT_SEPARATOR);
        byte[] salt = Base64.getDecoder().decode(parts[0]);
        String hashedPassword = hashPassword(password, salt);
        return parts[1].equals(hashedPassword);
    }

    private static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    private static String hashPassword(String password, byte[] salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
