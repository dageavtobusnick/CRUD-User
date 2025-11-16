import org.example.model.User;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class UserRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail_ShouldReturnUser() {
        // Given
        User user = new User("John Doe", "john@example.com", 30);
        userRepository.save(user);

        // When
        Optional<User> found = userRepository.findByEmail("john@example.com");

        // Then
        assertTrue(found.isPresent());
        assertEquals("John Doe", found.get().getName());
    }

    @Test
    void existsByEmail_ShouldReturnTrueForExistingEmail() {
        // Given
        User user = new User("John Doe", "john@example.com", 30);
        userRepository.save(user);

        // When
        boolean exists = userRepository.existsByEmail("john@example.com");

        // Then
        assertTrue(exists);
    }

    @Test
    void existsByEmail_ShouldReturnFalseForNonExistingEmail() {
        // When
        boolean exists = userRepository.existsByEmail("nonexisting@example.com");

        // Then
        assertFalse(exists);
    }

    @Test
    void saveUser_ShouldPersistUser() {
        // Given
        User user = new User("Jane Smith", "jane@example.com", 25);

        // When
        User saved = userRepository.save(user);

        // Then
        assertNotNull(saved.getId());
        assertEquals("Jane Smith", saved.getName());
        assertEquals("jane@example.com", saved.getEmail());
        assertEquals(25, saved.getAge());
        assertNotNull(saved.getCreatedAt());
    }
}
