package es.upm.tennis.tournament.manager.service;

import es.upm.tennis.tournament.manager.DTO.UserDTO;
import es.upm.tennis.tournament.manager.config.TestSecurityConfig;
import es.upm.tennis.tournament.manager.exceptions.*;
import es.upm.tennis.tournament.manager.model.*;
import es.upm.tennis.tournament.manager.repo.ConfirmationCodeRepository;
import es.upm.tennis.tournament.manager.repo.RoleRepository;
import es.upm.tennis.tournament.manager.repo.UserRepository;
import es.upm.tennis.tournament.manager.repo.UserSessionRepository;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {TestSecurityConfig.class})
@ActiveProfiles("test")
@DisplayName("User Service Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ConfirmationCodeRepository confirmationCodeRepository;

    @Mock
    private UserSessionRepository userSessionRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PermissionChecker permissionChecker;

    @Mock
    private UserSessionService sessionService;

    @InjectMocks
    private UserService userService;

    private UserDTO userDTO;
    private User user;
    private Role role;
    private ConfirmationCode confirmationCode;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        // Setup test data
        userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setEmail("test@example.com");
        userDTO.setPassword("password123");
        userDTO.setName("Test");
        userDTO.setSurname("User");
        userDTO.setPhonePrefix("+34");
        userDTO.setPhoneNumber("123456789");

        role = new Role();
        role.setType(ERole.USER);

        user = new User();
        user.setId(1L);
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword("encodedPassword");
        user.setRole(role);
        user.setCreatedAt(Instant.now());
        user.setConfirmed(false);
        user.setEnabled(true);

        confirmationCode = new ConfirmationCode(user, 30);

        userDetails = new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Nested
    @DisplayName("User Registration Tests")
    class RegisterUserTests {

        @Test
        @DisplayName("Should successfully register new user")
        void registerUser_ShouldSucceed_WhenUserDoesNotExist() throws Exception {
            when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(null);
            when(userRepository.findByUsername(userDTO.getUsername())).thenReturn(null);
            when(roleRepository.findByType(ERole.USER)).thenReturn(role);
            when(passwordEncoder.encode(any())).thenReturn("encodedPassword");

            assertDoesNotThrow(() -> userService.registerUser(userDTO));

            verify(userRepository).save(any(User.class));
            verify(confirmationCodeRepository).save(any(ConfirmationCode.class));
            verify(emailService).sendEmail(any(), any(), any());
        }

        @Test
        @DisplayName("Should throw exception when email exists")
        void registerUser_ShouldThrowException_WhenEmailExists() {
            when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(user);

            EmailAlreadyExistsException exception = assertThrows(
                    EmailAlreadyExistsException.class,
                    () -> userService.registerUser(userDTO)
            );

            assertEquals("An account associated to that email already exists", exception.getMessage());
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("User Confirmation Tests")
    class ConfirmUserTests {

        @Test
        @DisplayName("Should successfully confirm user")
        void confirmUser_ShouldSucceed_WhenCodeIsValid() {
            String code = "validCode";
            confirmationCode.setExpirationDate(Instant.now().plusSeconds(300));
            when(confirmationCodeRepository.findByCode(code)).thenReturn(confirmationCode);

            userService.confirmUser(code);

            assertTrue(user.isConfirmed());
            verify(userRepository).save(user);
            verify(confirmationCodeRepository).delete(confirmationCode);
        }

        @Test
        @DisplayName("Should throw exception when code is expired")
        void confirmUser_ShouldThrowException_WhenCodeIsExpired() {
            String code = "expiredCode";
            confirmationCode.setExpirationDate(Instant.now().minusSeconds(300));
            when(confirmationCodeRepository.findByCode(code)).thenReturn(confirmationCode);

            InvalidCodeException exception = assertThrows(
                    InvalidCodeException.class,
                    () -> userService.confirmUser(code)
            );

            assertEquals("Expired code", exception.getMessage());
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Password Management Tests")
    class PasswordManagementTests {

        @Test
        @DisplayName("Should initiate password change process")
        void changePassword_ShouldSucceed_WhenUserExists() throws MessagingException {
            when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(user);

            assertDoesNotThrow(() -> userService.changePassword(userDTO.getEmail()));

            verify(confirmationCodeRepository).save(any(ConfirmationCode.class));
            verify(emailService).sendEmail(any(), any(), any());
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void changePassword_ShouldThrowException_WhenUserNotFound() {
            when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(null);

            UserNotFoundException exception = assertThrows(
                    UserNotFoundException.class,
                    () -> userService.changePassword(userDTO.getEmail())
            );

            assertEquals("User not found", exception.getMessage());
        }
    }
    @Nested
    @DisplayName("Authentication Tests")
    class AuthenticationTests {
        @BeforeEach
        void setUpAuth() {
            user.setConfirmed(true);
        }

        @Test
        @DisplayName("Should successfully authenticate user")
        void authenticateUser_ShouldSucceed_WhenValidCredentials() {
            // Arrange
            Authentication authentication = mock(Authentication.class);
            when(authentication.getPrincipal()).thenReturn(userDetails);
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(userRepository.findByUsername(userDetails.getUsername())).thenReturn(user);

            // Mock session service
            UserSession newSession = new UserSession();
            newSession.setSessionId(UUID.randomUUID().toString());
            newSession.setExpirationDate(Instant.now().plusSeconds(1440 * 60));
            when(sessionService.createSession(user)).thenReturn(newSession);

            // Act
            Map<String, Object> result = userService.authenticateUser(user.getUsername(), "password");

            // Assert
            assertNotNull(result.get("sessionId"));
            assertNotNull(result.get("sessionExp"));
            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        }

        @Test
        @DisplayName("Should throw exception when user not confirmed")
        void authenticateUser_ShouldThrow_WhenUserNotConfirmed() {
            // Arrange
            user.setConfirmed(false);
            Authentication authentication = mock(Authentication.class);
            when(authentication.getPrincipal()).thenReturn(userDetails);
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(userRepository.findByUsername(userDetails.getUsername())).thenReturn(user);

            // Act & Assert
            assertThrows(AccountNotConfirmedException.class,
                    () -> userService.authenticateUser(user.getUsername(), "password"));
        }

        @Test
        @DisplayName("Should throw exception when account disabled")
        void authenticateUser_ShouldThrow_WhenAccountDisabled() {
            // Arrange
            user.setConfirmed(true);
            user.setEnabled(false);
            Authentication authentication = mock(Authentication.class);
            when(authentication.getPrincipal()).thenReturn(userDetails);
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(userRepository.findByUsername(userDetails.getUsername())).thenReturn(user);

            // Act & Assert
            assertThrows(AccountDisabledException.class,
                    () -> userService.authenticateUser(user.getUsername(), "password"));
        }

        @Test
        @DisplayName("Should invalidate existing session when authenticating")
        void authenticateUser_ShouldInvalidateExistingSession() {
            // Arrange
            Authentication authentication = mock(Authentication.class);
            when(authentication.getPrincipal()).thenReturn(userDetails);
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(userRepository.findByUsername(userDetails.getUsername())).thenReturn(user);

            UserSession existingSession = new UserSession();
            existingSession.setUser(user);
            existingSession.setSessionId("existing-session-id");

            UserSession newSession = new UserSession();
            newSession.setUser(user);
            newSession.setSessionId("new-session-id");
            newSession.setExpirationDate(Instant.now().plusSeconds(1440 * 60));

            // Mock session service behaviors
            when(sessionService.findByUser(user)).thenReturn(existingSession);
            when(sessionService.invalidateSession("existing-session-id")).thenReturn(true);
            when(sessionService.createSession(user)).thenReturn(newSession);

            // Act
            Map<String, Object> result = userService.authenticateUser(user.getUsername(), "password");

            // Assert
            verify(sessionService).findByUser(user);
            verify(sessionService).invalidateSession("existing-session-id");
            verify(sessionService).createSession(user);

            assertNotNull(result.get("sessionId"));
            assertNotNull(result.get("sessionExp"));
            assertEquals("new-session-id", result.get("sessionId"));
            assertEquals(newSession.getExpirationDate(), result.get("sessionExp"));
        }
    }

    @Nested
    @DisplayName("User Modification Tests")
    class UserModificationTests {
        @Test
        @DisplayName("Should successfully modify user")
        void modifyUser_ShouldSucceed_WhenValidData() {
            // Arrange
            UserSession userSession = new UserSession();
            userSession.setUser(user);
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userSessionRepository.findBySessionId("sessionId")).thenReturn(userSession);
            doNothing().when(permissionChecker).validateUserPermission(any(), any());

            UserDTO updateDTO = new UserDTO();
            updateDTO.setUsername("newUsername");
            updateDTO.setName("New Name");
            when(userRepository.findByUsername(updateDTO.getUsername())).thenReturn(null);

            // Act
            userService.modifyUser(1L, "sessionId", updateDTO);

            // Assert
            verify(userRepository).save(user);
            assertEquals("newUsername", user.getUsername());
            assertEquals("New Name", user.getName());
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void modifyUser_ShouldThrow_WhenUserNotFound() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(UserNotFoundException.class,
                    () -> userService.modifyUser(1L, "sessionId", new UserDTO()));
        }
    }

    @Nested
    @DisplayName("Delete User Tests")
    class DeleteUserTests {
        @Test
        @DisplayName("Should successfully delete user")
        void deleteUser_ShouldSucceed() {
            // Arrange
            UserSession userSession = new UserSession();
            userSession.setUser(user);
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userSessionRepository.findBySessionId("sessionId")).thenReturn(userSession);
            when(confirmationCodeRepository.findByUser(user)).thenReturn(confirmationCode);
            doNothing().when(permissionChecker).validateUserPermission(any(), any());

            // Act
            userService.deleteUser(1L, "sessionId");

            // Assert
            verify(userSessionRepository).delete(userSession);
            verify(confirmationCodeRepository).delete(confirmationCode);
            verify(userRepository).delete(user);
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void deleteUser_ShouldThrow_WhenUserNotFound() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(UserNotFoundException.class,
                    () -> userService.deleteUser(1L, "sessionId"));
        }
    }

    @Nested
    @DisplayName("Registration Edge Cases")
    class RegistrationEdgeCasesTests {
        @Test
        @DisplayName("Should delete old unconfirmed user and allow re-registration")
        void registerUser_ShouldDeleteOldUnconfirmedUser() throws Exception {
            User oldUser = new User();
            oldUser.setEmail(userDTO.getEmail());
            oldUser.setConfirmed(false);
            oldUser.setCreatedAt(Instant.now().minus(Duration.ofDays(2)));

            ConfirmationCode oldCode = new ConfirmationCode(oldUser, 30);
            oldCode.setExpirationDate(Instant.now().minus(Duration.ofHours(2)));

            when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(oldUser);
            when(confirmationCodeRepository.findByUser(oldUser)).thenReturn(oldCode);
            doNothing().when(confirmationCodeRepository).delete(any(ConfirmationCode.class));
            doNothing().when(userRepository).delete(any(User.class));
            when(userRepository.findByUsername(userDTO.getUsername())).thenReturn(null);
            when(passwordEncoder.encode(userDTO.getPassword())).thenReturn("encodedPassword");
            when(roleRepository.findByType(ERole.USER)).thenReturn(role);

            userService.registerUser(userDTO);

            InOrder inOrder = inOrder(userRepository, confirmationCodeRepository, roleRepository, emailService);

            inOrder.verify(userRepository).findByEmail(userDTO.getEmail());
            inOrder.verify(confirmationCodeRepository).findByUser(oldUser);
            inOrder.verify(confirmationCodeRepository).delete(oldCode);
            inOrder.verify(userRepository).findByUsername(userDTO.getUsername());
            inOrder.verify(roleRepository).findByType(ERole.USER);

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            inOrder.verify(userRepository).save(userCaptor.capture());

            User savedUser = userCaptor.getValue();
            assertAll(
                    () -> assertEquals(userDTO.getUsername(), savedUser.getUsername()),
                    () -> assertEquals(userDTO.getEmail(), savedUser.getEmail()),
                    () -> assertEquals(userDTO.getName(), savedUser.getName()),
                    () -> assertEquals(userDTO.getSurname(), savedUser.getSurname()),
                    () -> assertEquals(userDTO.getPhonePrefix(), savedUser.getPhonePrefix()),
                    () -> assertEquals(userDTO.getPhoneNumber(), savedUser.getPhoneNumber()),
                    () -> assertEquals("encodedPassword", savedUser.getPassword()),
                    () -> assertFalse(savedUser.isConfirmed()),
                    () -> assertNotNull(savedUser.getCreatedAt()),
                    () -> assertEquals(role, savedUser.getRole())
            );
        }

        @Test
        @DisplayName("Should throw when confirmation email fails")
        void registerUser_ShouldThrow_WhenEmailFails() throws Exception {
            // Arrange
            when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(null);
            when(userRepository.findByUsername(userDTO.getUsername())).thenReturn(null);
            when(roleRepository.findByType(ERole.USER)).thenReturn(role);
            doThrow(new MessagingException("Email error")).when(emailService).sendEmail(any(), any(), any());

            // Act & Assert
            assertThrows(EmailNotSentException.class,
                    () -> userService.registerUser(userDTO));
        }
    }
}
