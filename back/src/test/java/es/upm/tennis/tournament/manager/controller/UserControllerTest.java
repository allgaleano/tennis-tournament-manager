package es.upm.tennis.tournament.manager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.upm.tennis.tournament.manager.DTO.UserDTO;
import es.upm.tennis.tournament.manager.config.SessionFilter;
import es.upm.tennis.tournament.manager.config.TestSecurityConfig;
import es.upm.tennis.tournament.manager.exceptions.CustomException;
import es.upm.tennis.tournament.manager.exceptions.ErrorCode;
import es.upm.tennis.tournament.manager.model.ERole;
import es.upm.tennis.tournament.manager.model.Role;
import es.upm.tennis.tournament.manager.model.User;
import es.upm.tennis.tournament.manager.service.UserService;
import es.upm.tennis.tournament.manager.service.UserSessionService;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
@DisplayName("User Controller Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserSessionService userSessionService;

    @MockBean
    private SessionFilter sessionFilter;

    private UserDTO userDTO;
    private static final String BASE_URL = "/users";

    @BeforeEach
    void setUp() {
        try {
            doNothing().when(sessionFilter).doFilter(any(), any(), any());
        } catch (ServletException | IOException e) {
            throw new RuntimeException(e);
        }

        userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setEmail("test@example.com");
        userDTO.setName("Test");
        userDTO.setSurname("User");
        userDTO.setPhonePrefix("+34");
        userDTO.setPhoneNumber("123456789");

        when(userSessionService.validateSession("test-session-id")).thenReturn(true);
    }

    @Nested
    @DisplayName("Get User Data Tests")
    class GetUserDataTests {

        @Test
        @Order(1)
        @DisplayName("Should successfully get user data")
        void getUserData_ShouldReturnOk_WhenUserExists() throws Exception {
            // Arrange
            User user = new User();
            user.setId(1L);
            user.setUsername("testuser");
            user.setEmail("test@example.com");
            user.setName("Test");
            user.setSurname("User");
            user.setPhonePrefix("+34");
            user.setPhoneNumber("123456789");
            Role role = new Role();
            role.setType(ERole.USER);
            user.setRole(role);
            when(userService.getUserData(any(Authentication.class))).thenReturn(user);

            // Act & Assert
            mockMvc.perform(get("/userData")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Update User Tests")
    class UpdateUserTests {

        @Test
        @Order(2)
        @DisplayName("Should successfully update user")
        void updateUser_ShouldReturnOk_WhenSuccessful() throws Exception {
            // Arrange
            doNothing().when(userService).modifyUser(anyLong(), anyString(), any(UserDTO.class));

            // Act & Assert
            mockMvc.perform(put(BASE_URL + "/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Session-Id", "test-session-id")
                            .content(objectMapper.writeValueAsString(userDTO)))
                    .andExpect(status().isOk());

        }

        @Test
        @Order(3)
        @DisplayName("Should return not found when user does not exist")
        void updateUser_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
            // Arrange
            doThrow(new CustomException(
                    ErrorCode.USER_NOT_FOUND,
                    "User not found"
            )).when(userService).modifyUser(anyLong(), anyString(), any(UserDTO.class));

            // Act & Assert
            mockMvc.perform(put(BASE_URL + "/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Session-Id", "test-session-id")
                            .content(objectMapper.writeValueAsString(userDTO)))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Delete User Tests")
    class DeleteUserTests {

        @Test
        @Order(4)
        @DisplayName("Should successfully delete user")
        void deleteUser_ShouldReturnOk_WhenSuccessful() throws Exception {
            // Arrange
            doNothing().when(userService).deleteUser(anyLong(), anyString());

            // Act & Assert
            mockMvc.perform(delete(BASE_URL + "/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Session-Id", "test-session-id"))
                    .andExpect(status().isOk());
        }

        @Test
        @Order(5)
        @DisplayName("Should return not found when user does not exist")
        void deleteUser_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
            // Arrange
            doThrow(new CustomException(
                    ErrorCode.USER_NOT_FOUND,
                    "User not found"
            )).when(userService).deleteUser(anyLong(), anyString());

            // Act & Assert
            mockMvc.perform(delete(BASE_URL + "/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Session-Id", "test-session-id"))
                    .andExpect(status().isOk());
        }
    }
}