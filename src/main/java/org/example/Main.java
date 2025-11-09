package org.example;

import org.example.model.User;
import org.example.service.UserService;
import org.example.util.HibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    private static final Scanner scanner = new Scanner(System.in);

    private static UserService userService;

    public static void main(String[] args) {
        logger.info("Starting User Service application");

        try {
            if (!HibernateUtil.isInitialized()) {
                System.err.println("Hibernate initialization failed. Application cannot start.");
                System.exit(1);
            }

            userService = new UserService();
            displayMenu();
            boolean running = true;

            while (running) {
                System.out.print("\nEnter your choice: ");
                String choice = scanner.nextLine();

                switch (choice) {
                    case "1":
                        createUser();
                        break;
                    case "2":
                        getUserById();
                        break;
                    case "3":
                        getAllUsers();
                        break;
                    case "4":
                        updateUser();
                        break;
                    case "5":
                        deleteUser();
                        break;
                    case "6":
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }

                if (running) {
                    displayMenu();
                }
            }

            System.out.println("Goodbye!");

        } catch (Exception e) {
            logger.error("Application error", e);
            System.err.println("An error occurred: " + e.getMessage());
        } finally {
            HibernateUtil.shutdown();
            scanner.close();
            logger.info("User Service application stopped");
        }
    }

    private static void displayMenu() {
        System.out.println("\n=== User Service ===");
        System.out.println("1. Create User");
        System.out.println("2. Get User by ID");
        System.out.println("3. Get All Users");
        System.out.println("4. Update User");
        System.out.println("5. Delete User");
        System.out.println("6. Exit");
    }

    private static void createUser() {
        try {
            System.out.print("Enter name: ");
            String name = scanner.nextLine();

            System.out.print("Enter email: ");
            String email = scanner.nextLine();

            System.out.print("Enter age: ");
            int age = Integer.parseInt(scanner.nextLine());

            User user = userService.createUser(name, email, age);
            System.out.println("User created successfully: " + user);

        } catch (NumberFormatException e) {
            System.out.println("Invalid age format. Please enter a number.");
        } catch (Exception e) {
            System.out.println("Error creating user: " + e.getMessage());
            logger.error("Error creating user", e);
        }
    }

    private static void getUserById() {
        try {
            System.out.print("Enter user ID: ");
            Long id = Long.parseLong(scanner.nextLine());

            Optional<User> user = userService.getUserById(id);
            if (user.isPresent()) {
                System.out.println("User found: " + user.get());
            } else {
                System.out.println("User not found with ID: " + id);
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Please enter a number.");
        } catch (Exception e) {
            System.out.println("Error retrieving user: " + e.getMessage());
            logger.error("Error retrieving user", e);
        }
    }

    private static void getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            if (users.isEmpty()) {
                System.out.println("No users found.");
            } else {
                System.out.println("Users:");
                users.forEach(System.out::println);
            }
        } catch (Exception e) {
            System.out.println("Error retrieving users: " + e.getMessage());
            logger.error("Error retrieving users", e);
        }
    }

    private static void updateUser() {
        try {
            System.out.print("Enter user ID to update: ");
            Long id = Long.parseLong(scanner.nextLine());

            System.out.print("Enter new name: ");
            String name = scanner.nextLine();

            System.out.print("Enter new email: ");
            String email = scanner.nextLine();

            System.out.print("Enter new age: ");
            int age = Integer.parseInt(scanner.nextLine());

            User user = userService.updateUser(id, name, email, age);
            System.out.println("User updated successfully: " + user);

        } catch (NumberFormatException e) {
            System.out.println("Invalid number format. Please check your input.");
        } catch (Exception e) {
            System.out.println("Error updating user: " + e.getMessage());
            logger.error("Error updating user", e);
        }
    }

    private static void deleteUser() {
        try {
            System.out.print("Enter user ID to delete: ");
            Long id = Long.parseLong(scanner.nextLine());

            userService.deleteUser(id);
            System.out.println("User deleted successfully");

        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Please enter a number.");
        } catch (Exception e) {
            System.out.println("Error deleting user: " + e.getMessage());
            logger.error("Error deleting user", e);
        }
    }
}