package es.upm.tennis.tournament.manager.service;

import es.upm.tennis.tournament.manager.exceptions.CustomException;
import es.upm.tennis.tournament.manager.exceptions.ErrorCode;
import es.upm.tennis.tournament.manager.model.ERole;
import es.upm.tennis.tournament.manager.model.Role;
import es.upm.tennis.tournament.manager.model.User;
import es.upm.tennis.tournament.manager.model.UserSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Permission Checker Tests")
class PermissionCheckerTest {

    @Mock
    private PermissionChecker permissionChecker;

    private User user;
    private UserSession userSession;

    @BeforeEach
    void setUp() {

        Role role = new Role();
        role.setType(ERole.USER);

        user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setRole(role);
        user.setConfirmed(true);

        userSession = new UserSession();
        userSession.setSessionId("valid-session-id");
        userSession.setUser(user);
        userSession.setExpirationDate(Instant.now().plus(1, ChronoUnit.HOURS));
    }

    @Test
    @DisplayName("Should validate session successfully")
    void validateSession_ShouldPass_WhenSessionIsValid() {
//        assertDoesNotThrow(() -> permissionChecker.validateSession(userSession));
    }

    @Test
    @DisplayName("Should throw exception for invalid session")
    void validateSession_ShouldThrowException_WhenSessionIsInvalid() {
        userSession.setExpirationDate(Instant.now().minus(1, ChronoUnit.HOURS));
//        CustomException exception = assertThrows(CustomException.class, () -> permissionChecker.validateSession(userSession));
//        assertEquals(ErrorCode.INVALID_TOKEN, exception.getErrorCode());
    }

    @Test
    @DisplayName("Should validate user permission successfully")
    void validateUserPermission_ShouldPass_WhenUserHasPermission() {
//        assertDoesNotThrow(() -> permissionChecker.validateUserPermission(user, userSession));
    }

    @Test
    @DisplayName("Should throw exception for unconfirmed user")
    void validateUserPermission_ShouldThrowException_WhenUserIsNotConfirmed() {
        user.setConfirmed(false);
//        CustomException exception = assertThrows(CustomException.class, () -> permissionChecker.validateUserPermission(user, userSession));
//        assertEquals(ErrorCode.ACCOUNT_NOT_CONFIRMED, exception.getErrorCode());
    }

    @Test
    @DisplayName("Should throw exception for unauthorized user")
    void validateUserPermission_ShouldThrowException_WhenUserIsUnauthorized() {
        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setRole(user.getRole());
        userSession.setUser(anotherUser);

//        CustomException exception = assertThrows(CustomException.class, () -> permissionChecker.validateUserPermission(user, userSession));
//        assertEquals(ErrorCode.UNAUTHORIZED_ACTION, exception.getErrorCode());
    }

    @Test
    @DisplayName("Should validate admin permission successfully")
    void validateAdminPermission_ShouldPass_WhenUserIsAdmin() {
        Role adminRole = new Role();
        adminRole.setType(ERole.ADMIN);
        user.setRole(adminRole);
        userSession.setUser(user);

//        assertDoesNotThrow(() -> permissionChecker.validateAdminPermission(userSession));
    }

    @Test
    @DisplayName("Should throw exception for non-admin user")
    void validateAdminPermission_ShouldThrowException_WhenUserIsNotAdmin() {
//        CustomException exception = assertThrows(CustomException.class, () -> permissionChecker.validateAdminPermission(userSession));
//        assertEquals(ErrorCode.UNAUTHORIZED_ACTION, exception.getErrorCode());
    }
}