package es.upm.tennis.tournament.manager.repo;

import es.upm.tennis.tournament.manager.model.ERole;
import es.upm.tennis.tournament.manager.model.Role;
import es.upm.tennis.tournament.manager.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("User Repository Tests")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        Role userRole = new Role();
        userRole.setType(ERole.USER);
        roleRepository.save(userRole);

        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setName("Test");
        testUser.setSurname("User");
        testUser.setPhonePrefix("+34");
        testUser.setPhoneNumber("123456789");
        testUser.setConfirmed(true);
        testUser.setEnabled(true);
        testUser.setCreatedAt(Instant.now());
        testUser.setRole(userRole);
    }

    @Nested
    @DisplayName("Find User Tests")
    class FindUserTests {

        @Test
        @DisplayName("Should find user by username when user exists")
        void findByUsername_ShouldReturnUser_WhenUserExists() {
            entityManager.persistAndFlush(testUser);

            User found = userRepository.findByUsername(testUser.getUsername());

            assertNotNull(found, "User should be found");
            assertEquals(testUser.getUsername(), found.getUsername(), "Username should match");
            assertEquals(testUser.getEmail(), found.getEmail(), "Email should match");
        }

        @Test
        @DisplayName("Should return null when searching by non-existent username")
        void findByUsername_ShouldReturnNull_WhenUserDoesNotExist() {
            User found = userRepository.findByUsername("nonexistent");
            assertNull(found, "User should not be found");
        }

        @Test
        @DisplayName("Should find user by email when user exists")
        void findByEmail_ShouldReturnUser_WhenUserExists() {
            entityManager.persistAndFlush(testUser);

            User found = userRepository.findByEmail(testUser.getEmail());

            assertNotNull(found, "User should be found");
            assertEquals(testUser.getEmail(), found.getEmail(), "Email should match");
            assertEquals(testUser.getUsername(), found.getUsername(), "Username should match");
        }
    }

    @Nested
    @DisplayName("Save User Tests")
    class SaveUserTests {

        @Test
        @DisplayName("Should save user successfully")
        void save_ShouldPersistUser() {
            User savedUser = userRepository.save(testUser);

            assertNotNull(savedUser.getId(), "Saved user should have an ID");

            User found = entityManager.find(User.class, savedUser.getId());
            assertNotNull(found, "User should be found in database");
            assertEquals(testUser.getUsername(), found.getUsername(), "Username should match");
            assertEquals(testUser.getEmail(), found.getEmail(), "Email should match");
            assertEquals(testUser.getRole().getType(), found.getRole().getType(), "Role should match");
        }

        @Test
        @DisplayName("Should update existing user")
        void save_ShouldUpdateExistingUser() {
            User savedUser = entityManager.persistAndFlush(testUser);

            String newEmail = "updated@example.com";
            savedUser.setEmail(newEmail);
            userRepository.save(savedUser);

            User updatedUser = entityManager.find(User.class, savedUser.getId());
            assertEquals(newEmail, updatedUser.getEmail(), "Email should be updated");
        }
    }

    @Nested
    @DisplayName("Delete User Tests")
    class DeleteUserTests {

        @Test
        @DisplayName("Should delete user successfully")
        void delete_ShouldRemoveUser() {
            User savedUser = entityManager.persistAndFlush(testUser);

            userRepository.deleteById(savedUser.getId());

            User deletedUser = entityManager.find(User.class, savedUser.getId());
            assertNull(deletedUser, "User should be deleted");
        }
    }
}
