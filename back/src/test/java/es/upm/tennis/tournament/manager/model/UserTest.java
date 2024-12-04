package es.upm.tennis.tournament.manager.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create User with valid data")
        void shouldCreateUserWithValidData() {
            // Arrange
            String username = "testuser";
            String email = "test@example.com";
            String name = "Test";
            String surname = "User";
            String phonePrefix = "+34";
            String phoneNumber = "123456789";
            String password = "password123";
            boolean isConfirmed = false;
            boolean isEnabled = true;
            Instant createdAt = Instant.now();
            Role role = new Role();
            role.setType(ERole.USER);

            // Act
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setName(name);
            user.setSurname(surname);
            user.setPhonePrefix(phonePrefix);
            user.setPhoneNumber(phoneNumber);
            user.setPassword(password);
            user.setConfirmed(isConfirmed);
            user.setEnabled(isEnabled);
            user.setCreatedAt(createdAt);
            user.setRole(role);

            // Assert
            assertNotNull(user.getUsername());
            assertNotNull(user.getEmail());
            assertNotNull(user.getName());
            assertNotNull(user.getSurname());
            assertNotNull(user.getPhonePrefix());
            assertNotNull(user.getPhoneNumber());
            assertNotNull(user.getPassword());
            assertFalse(user.isConfirmed());
            assertTrue(user.isEnabled());
            assertNotNull(user.getCreatedAt());
            assertNotNull(user.getRole());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should get and set id")
        void shouldGetAndSetId() {
            // Arrange
            User user = new User();
            Long id = 1L;

            // Act
            user.setId(id);

            // Assert
            assertEquals(id, user.getId());
        }

        @Test
        @DisplayName("Should get and set username")
        void shouldGetAndSetUsername() {
            // Arrange
            User user = new User();
            String username = "testuser";

            // Act
            user.setUsername(username);

            // Assert
            assertEquals(username, user.getUsername());
        }

        @Test
        @DisplayName("Should get and set email")
        void shouldGetAndSetEmail() {
            // Arrange
            User user = new User();
            user.setEmail("myemail@g.com");
            // Assert
            assertEquals("myemail@g.com", user.getEmail());
        }

        @Test
        @DisplayName("Should get and set name")
        void shouldGetAndSetName() {
            // Arrange
            User user = new User();
            String name = "Test";

            // Act
            user.setName(name);

            // Assert
            assertEquals(name, user.getName());
        }

        @Test
        @DisplayName("Should get and set surname")
        void shouldGetAndSetSurname() {
            // Arrange
            User user = new User();
            String surname = "User";

            // Act
            user.setSurname(surname);

            String email = "test@example.com";

            // Act
            user.setEmail(email);

            // Assert
            assertEquals(surname, user.getSurname());
        }

        @Test
        @DisplayName("Should get and set phone prefix")
        void shouldGetAndSetPhonePrefix() {
            // Arrange
            User user = new User();
            String phonePrefix = "+34";

            // Act
            user.setPhonePrefix(phonePrefix);

            // Assert
            assertEquals(phonePrefix, user.getPhonePrefix());
        }

        @Test
        @DisplayName("Should get and set phone number")
        void shouldGetAndSetPhoneNumber() {
            // Arrange
            User user = new User();
            String phoneNumber = "123456789";

            // Act
            user.setPhoneNumber(phoneNumber);

            // Assert
            assertEquals(phoneNumber, user.getPhoneNumber());
        }

        @Test
        @DisplayName("Should get and set password")
        void shouldGetAndSetPassword() {
            // Arrange
            User user = new User();
            String password = "password123";

            // Act
            user.setPassword(password);

            // Assert
            assertEquals(password, user.getPassword());
        }

        @Test
        @DisplayName("Should get and set confirmed status")
        void shouldGetAndSetConfirmedStatus() {
            // Arrange
            User user = new User();
            boolean isConfirmed = true;

            // Act
            user.setConfirmed(isConfirmed);

            // Assert
            assertTrue(user.isConfirmed());
        }

        @Test
        @DisplayName("Should get and set enabled status")
        void shouldGetAndSetEnabledStatus() {
            // Arrange
            User user = new User();
            boolean isEnabled = false;

            // Act
            user.setEnabled(isEnabled);

            // Assert
            assertFalse(user.isEnabled());
        }

        @Test
        @DisplayName("Should get and set created at")
        void shouldGetAndSetCreatedAt() {
            // Arrange
            User user = new User();
            Instant createdAt = Instant.now();

            // Act
            user.setCreatedAt(createdAt);

            // Assert
            assertEquals(createdAt, user.getCreatedAt());
        }

        @Test
        @DisplayName("Should get and set role")
        void shouldGetAndSetRole() {
            // Arrange
            User user = new User();
            Role role = new Role();
            role.setType(ERole.USER);

            // Act
            user.setRole(role);

            // Assert
            assertEquals(role, user.getRole());
        }
    }
}