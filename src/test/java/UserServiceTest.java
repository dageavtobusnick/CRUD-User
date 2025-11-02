import org.example.dao.UserDao;
import org.example.model.User;
import org.example.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDao userDao;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService();
        try {
            var field = UserService.class.getDeclaredField("userDao");
            field.setAccessible(true);
            field.set(userService, userDao);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock UserDao", e);
        }
    }

    @Test
    void createUser_ShouldCreateUser_WhenEmailIsUnique() {
        // Arrange
        String name = "John Doe";
        String email = "john@example.com";
        Integer age = 30;
        User expectedUser = new User(name, email, age);
        expectedUser.setId(1L);

        when(userDao.findByEmail(email)).thenReturn(Optional.empty());
        when(userDao.save(any(User.class))).thenReturn(expectedUser);

        // Act
        User createdUser = userService.createUser(name, email, age);

        // Assert
        assertNotNull(createdUser);
        assertEquals(name, createdUser.getName());
        assertEquals(email, createdUser.getEmail());
        assertEquals(age, createdUser.getAge());

        verify(userDao).findByEmail(email);
        verify(userDao).save(any(User.class));
    }

    @Test
    void createUser_ShouldThrowException_WhenEmailAlreadyExists() {
        // Arrange
        String name = "John Doe";
        String email = "existing@example.com";
        Integer age = 30;
        User existingUser = new User("Existing User", email, 25);

        when(userDao.findByEmail(email)).thenReturn(Optional.of(existingUser));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(name, email, age)
        );

        assertEquals("User with email " + email + " already exists", exception.getMessage());
        verify(userDao).findByEmail(email);
        verify(userDao, never()).save(any(User.class));
    }

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        // Arrange
        Long userId = 1L;
        User expectedUser = new User("John Doe", "john@example.com", 30);
        expectedUser.setId(userId);

        when(userDao.findById(userId)).thenReturn(Optional.of(expectedUser));

        // Act
        Optional<User> result = userService.getUserById(userId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getId());
        verify(userDao).findById(userId);
    }

    @Test
    void getUserById_ShouldReturnEmpty_WhenUserNotExists() {
        // Arrange
        Long userId = 999L;
        when(userDao.findById(userId)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.getUserById(userId);

        // Assert
        assertTrue(result.isEmpty());
        verify(userDao).findById(userId);
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        // Arrange
        List<User> expectedUsers = Arrays.asList(
                new User("User1", "user1@example.com", 25),
                new User("User2", "user2@example.com", 30)
        );

        when(userDao.findAll()).thenReturn(expectedUsers);

        // Act
        List<User> result = userService.getAllUsers();

        // Assert
        assertEquals(2, result.size());
        verify(userDao).findAll();
    }

    @Test
    void updateUser_ShouldUpdateUser_WhenValidData() {
        // Arrange
        Long userId = 1L;
        String newName = "Updated Name";
        String newEmail = "updated@example.com";
        Integer newAge = 35;

        User existingUser = new User("Old Name", "old@example.com", 30);
        existingUser.setId(userId);
        User updatedUser = new User(newName, newEmail, newAge);
        updatedUser.setId(userId);

        when(userDao.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userDao.findByEmail(newEmail)).thenReturn(Optional.empty());
        when(userDao.update(any(User.class))).thenReturn(updatedUser);

        // Act
        User result = userService.updateUser(userId, newName, newEmail, newAge);

        // Assert
        assertEquals(newName, result.getName());
        assertEquals(newEmail, result.getEmail());
        assertEquals(newAge, result.getAge());

        verify(userDao).findById(userId);
        verify(userDao).findByEmail(newEmail);
        verify(userDao).update(any(User.class));
    }

    @Test
    void updateUser_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        Long userId = 999L;
        when(userDao.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.updateUser(userId, "Name", "email@example.com", 25)
        );

        assertEquals("User not found with id: " + userId, exception.getMessage());
        verify(userDao).findById(userId);
        verify(userDao, never()).findByEmail(anyString());
        verify(userDao, never()).update(any(User.class));
    }

    @Test
    void updateUser_ShouldThrowException_WhenEmailAlreadyExists() {
        // Arrange
        Long userId = 1L;
        String newEmail = "existing@example.com";
        User existingUser = new User("Current User", "current@example.com", 30);
        existingUser.setId(userId);
        User otherUser = new User("Other User", newEmail, 25);

        when(userDao.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userDao.findByEmail(newEmail)).thenReturn(Optional.of(otherUser));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.updateUser(userId, "New Name", newEmail, 35)
        );

        assertEquals("User with email " + newEmail + " already exists", exception.getMessage());
        verify(userDao).findById(userId);
        verify(userDao).findByEmail(newEmail);
        verify(userDao, never()).update(any(User.class));
    }

    @Test
    void updateUser_ShouldAllowSameEmail_WhenUpdatingSameUser() {
        // Arrange
        Long userId = 1L;
        String sameEmail = "same@example.com";
        User existingUser = new User("Old Name", sameEmail, 30);
        existingUser.setId(userId);
        User updatedUser = new User("New Name", sameEmail, 35);
        updatedUser.setId(userId);

        when(userDao.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userDao.update(any(User.class))).thenReturn(updatedUser);

        // Act
        User result = userService.updateUser(userId, "New Name", sameEmail, 35);

        // Assert
        assertEquals("New Name", result.getName());
        assertEquals(sameEmail, result.getEmail());
        assertEquals(35, result.getAge());

        verify(userDao).findById(userId);
        verify(userDao, never()).findByEmail(sameEmail); // Не проверяем email, т.к. он не изменился
        verify(userDao).update(any(User.class));
    }

    @Test
    void deleteUser_ShouldDeleteUser_WhenUserExists() {
        // Arrange
        Long userId = 1L;
        doNothing().when(userDao).delete(userId);

        // Act & Assert
        assertDoesNotThrow(() -> userService.deleteUser(userId));
        verify(userDao).delete(userId);
    }

    @Test
    void deleteUser_ShouldPropagateException_WhenDaoFails() {
        // Arrange
        Long userId = 1L;
        doThrow(new RuntimeException("Database error")).when(userDao).delete(userId);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userService.deleteUser(userId));
        verify(userDao).delete(userId);
    }
}
