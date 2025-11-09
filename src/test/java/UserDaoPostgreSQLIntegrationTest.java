import org.example.dao.UserDaoPostgreSQL;
import org.example.model.User;
import org.example.util.HibernateUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserDaoPostgreSQLIntegrationTest {
    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
    private static UserDaoPostgreSQL userDao;

    @BeforeAll
    static void beforeAll() {
        System.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
        System.setProperty("hibernate.connection.username", postgres.getUsername());
        System.setProperty("hibernate.connection.password", postgres.getPassword());

        HibernateUtil.shutdown();

        userDao = new UserDaoPostgreSQL();
    }

    @AfterAll
    static void afterAll() {
        HibernateUtil.shutdown();
    }

    @BeforeEach
    void setUp() {
        try (var session = HibernateUtil.getSessionFactory().openSession()) {
            var transaction = session.beginTransaction();
            session.createMutationQuery("DELETE FROM User").executeUpdate();
            transaction.commit();
        }
    }

    @Test
    @Order(1)
    void save_ShouldSaveUser_WhenValidUser() {
        // Arrange
        User user = new User("John Doe", "john@example.com", 30);

        // Act
        User savedUser = userDao.save(user);

        // Assert
        assertNotNull(savedUser);
        assertNotNull(savedUser.getId());
        assertEquals("John Doe", savedUser.getName());
        assertEquals("john@example.com", savedUser.getEmail());
        assertEquals(30, savedUser.getAge());
    }

    @Test
    @Order(2)
    void findById_ShouldReturnUser_WhenUserExists() {
        // Arrange
        User user = new User("Jane Doe", "jane@example.com", 25);
        User savedUser = userDao.save(user);

        // Act
        Optional<User> foundUser = userDao.findById(savedUser.getId());

        // Assert
        assertTrue(foundUser.isPresent());
        assertEquals(savedUser.getId(), foundUser.get().getId());
        assertEquals("Jane Doe", foundUser.get().getName());
    }

    @Test
    @Order(3)
    void findById_ShouldReturnEmpty_WhenUserNotExists() {
        // Act
        Optional<User> foundUser = userDao.findById(999L);

        // Assert
        assertTrue(foundUser.isEmpty());
    }

    @Test
    @Order(4)
    void findAll_ShouldReturnAllUsers() {
        // Arrange
        userDao.save(new User("User1", "user1@example.com", 20));
        userDao.save(new User("User2", "user2@example.com", 25));

        // Act
        List<User> users = userDao.findAll();

        // Assert
        assertEquals(2, users.size());
    }

    @Test
    @Order(5)
    void update_ShouldUpdateUser_WhenValidUser() {
        // Arrange
        User user = new User("Old Name", "old@example.com", 30);
        User savedUser = userDao.save(user);

        savedUser.setName("New Name");
        savedUser.setEmail("new@example.com");
        savedUser.setAge(35);

        // Act
        User updatedUser = userDao.update(savedUser);

        // Assert
        assertEquals("New Name", updatedUser.getName());
        assertEquals("new@example.com", updatedUser.getEmail());
        assertEquals(35, updatedUser.getAge());
    }

    @Test
    @Order(6)
    void delete_ShouldDeleteUser_WhenUserExists() {
        // Arrange
        User user = new User("To Delete", "delete@example.com", 40);
        User savedUser = userDao.save(user);

        // Act & Assert
        assertDoesNotThrow(() -> userDao.delete(savedUser.getId()));

        Optional<User> deletedUser = userDao.findById(savedUser.getId());
        assertTrue(deletedUser.isEmpty());
    }

    @Test
    @Order(7)
    void findByEmail_ShouldReturnUser_WhenEmailExists() {
        // Arrange
        String email = "find@example.com";
        userDao.save(new User("Find User", email, 28));

        // Act
        Optional<User> foundUser = userDao.findByEmail(email);

        // Assert
        assertTrue(foundUser.isPresent());
        assertEquals(email, foundUser.get().getEmail());
    }

    @Test
    @Order(8)
    void findByEmail_ShouldReturnEmpty_WhenEmailNotExists() {
        // Act
        Optional<User> foundUser = userDao.findByEmail("nonexistent@example.com");

        // Assert
        assertTrue(foundUser.isEmpty());
    }

    @Test
    @Order(9)
    void save_ShouldThrowException_WhenDatabaseError() {
        // Arrange
        User user = new User(null, "invalid@example.com", 30);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userDao.save(user));
    }
}
