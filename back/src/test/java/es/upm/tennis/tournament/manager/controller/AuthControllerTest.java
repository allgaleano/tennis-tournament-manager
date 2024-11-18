package es.upm.tennis.tournament.manager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.upm.tennis.tournament.manager.DTO.*;
import es.upm.tennis.tournament.manager.config.TestSecurityConfig;
import es.upm.tennis.tournament.manager.exceptions.*;
import es.upm.tennis.tournament.manager.service.UserService;
import es.upm.tennis.tournament.manager.service.UserSessionService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
@DisplayName("Auth Controller Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserSessionService userSessionService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDTO userDTO;
    private LoginRequest loginRequest;
    private static final String BASE_URL = "/auth";

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setEmail("test@example.com");
        userDTO.setPassword("password123");
        userDTO.setName("Test");
        userDTO.setSurname("User");
        userDTO.setPhonePrefix("+34");
        userDTO.setPhoneNumber("123456789");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");
    }

    @Nested
    @DisplayName("Registration Endpoint Tests")
    class RegistrationTests {

        @Test
        @DisplayName("Should successfully register new user")
        void register_ShouldReturnCreated_WhenSuccessful() throws Exception {
            // Arrange
            doNothing().when(userService).registerUser(any(UserDTO.class));

            // Act & Assert
            mockMvc.perform(post(BASE_URL + "/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userDTO)))
                            .andExpect(status().isCreated())
                            .andExpect(jsonPath("$.message").value("Confirmation Email sent!"));

            verify(userService).registerUser(any(UserDTO.class));
        }

        @Test
        @DisplayName("Should return conflict when email exists")
        void register_ShouldReturnConflict_WhenEmailExists() throws Exception {
            // Arrange
            doThrow(new EmailAlreadyExistsException("An account associated to that email already exists"))
                    .when(userService).registerUser(any(UserDTO.class));

            // Act & Assert
            mockMvc.perform(post(BASE_URL + "/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userDTO)))
                            .andExpect(status().isConflict())
                            .andExpect(jsonPath("$.error")
                                    .value("An account associated to that email already exists"));
        }
    }

    @Nested
    @DisplayName("Login Endpoint Tests")
    class LoginTests {

        @Test
        @DisplayName("Should successfully login user")
        void login_ShouldReturnOk_WhenCredentialsValid() throws Exception {
            // Arrange
            Map<String, Object> loginResponse = Map.of(
                    "sessionId", "test-session-id",
                    "sessionExp", Instant.now().plusSeconds(3600)
            );
            when(userService.authenticateUser(anyString(), anyString()))
                    .thenReturn(loginResponse);

            // Act & Assert
            mockMvc.perform(post(BASE_URL + "/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.sessionId").exists())
                            .andExpect(jsonPath("$.sessionExp").exists());
        }

        @Test
        @DisplayName("Should return unauthorized when credentials invalid")
        void login_ShouldReturnUnauthorized_WhenCredentialsInvalid() throws Exception {
            // Arrange
            when(userService.authenticateUser(anyString(), anyString()))
                    .thenThrow(new BadCredentialsException("Invalid username or password"));

            // Act & Assert
            mockMvc.perform(post(BASE_URL + "/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                            .andExpect(status().isUnauthorized())
                            .andExpect(jsonPath("$.error").value("Invalid username or password"));
        }
    }

    @Nested
    @DisplayName("Email Confirmation Tests")
    class EmailConfirmationTests {

        @Test
        @DisplayName("Should successfully confirm email")
        void confirmEmail_ShouldReturnOk_WhenTokenValid() throws Exception {
            // Arrange
            String token = "valid-token";
            doNothing().when(userService).confirmUser(token);

            // Act & Assert
            mockMvc.perform(post(BASE_URL + "/confirm-email")
                            .param("token", token))
                            .andExpect(status().isOk())
                            .andExpect(content().string("Account verified successfully"));
        }

        @Test
        @DisplayName("Should return unauthorized when token invalid")
        void confirmEmail_ShouldReturnUnauthorized_WhenTokenInvalid() throws Exception {
            // Arrange
            String token = "invalid-token";
            doThrow(new InvalidCodeException("Invalid code"))
                    .when(userService).confirmUser(token);

            // Act & Assert
            mockMvc.perform(post(BASE_URL + "/confirm-email")
                            .param("token", token))
                            .andExpect(status().isUnauthorized())
                            .andExpect(content().string("Invalid code"));
        }
    }

    @Nested
    @DisplayName("Password Management Tests")
    class PasswordTests {

        @Test
        @DisplayName("Should successfully initiate password change")
        void changePassword_ShouldReturnOk_WhenEmailExists() throws Exception {
            // Arrange
            ChangePasswordRequest request = new ChangePasswordRequest();
            request.setEmail("test@example.com");

            // Act & Assert
            mockMvc.perform(post(BASE_URL + "/change-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                            .andExpect(status().isOk())
                            .andExpect(content().string("Password modification email sent"));
        }

        @Test
        @DisplayName("Should successfully confirm password change")
        void confirmPassword_ShouldReturnOk_WhenTokenValid() throws Exception {
            // Arrange
            String token = "valid-token";
            ConfirmPasswordRequest request = new ConfirmPasswordRequest();
            request.setPassword("newPassword123");

            // Act & Assert
            mockMvc.perform(post(BASE_URL + "/confirm-password")
                            .param("token", token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                            .andExpect(status().isOk())
                            .andExpect(content().string("Password changed successfully"));
        }
    }
}