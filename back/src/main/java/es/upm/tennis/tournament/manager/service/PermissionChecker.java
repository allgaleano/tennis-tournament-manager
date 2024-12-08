package es.upm.tennis.tournament.manager.service;

import es.upm.tennis.tournament.manager.exceptions.CustomException;
import es.upm.tennis.tournament.manager.exceptions.ErrorCode;
import es.upm.tennis.tournament.manager.model.User;
import es.upm.tennis.tournament.manager.model.UserSession;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class PermissionChecker {

    private final UserSessionService userSessionService;

    public PermissionChecker(UserSessionService userSessionService) {
        this.userSessionService = userSessionService;
    }

    public UserSession validateSession (String sessionId) {
        UserSession userSession = userSessionService.getUserSession(sessionId);

        if (userSession == null || userSession.getExpirationDate().isBefore(Instant.now())) {
            throw new CustomException(
                    ErrorCode.INVALID_TOKEN,
                    "Sesión no inválida o expirada"
            );
        }

        return userSession;
    }

    public UserSession validateUserPermission (User user, String sessionId) {
        UserSession userSession = validateSession(sessionId);

        boolean isAdmin = userSession.getUser().getRole().getType().name().equals("ADMIN");

        if (isAdmin) return userSession;

        if (!user.isConfirmed()) {
            throw new CustomException(
                    ErrorCode.ACCOUNT_NOT_CONFIRMED,
                    "Cuenta no confirmada",
                    "Por favor, confirme su cuenta para continuar"
            );
        }

        if (!user.getId().equals(userSession.getUser().getId())) {
            throw new CustomException(
                    ErrorCode.UNAUTHORIZED_ACTION,
                    "Acción no autorizada",
                    "No tiene permisos para realizar esta acción"
            );
        }

        return userSession;
    }

    public void validateAdminPermission (String sessionId) {
        UserSession userSession = validateSession(sessionId);

        boolean isAdmin = userSession.getUser().getRole().getType().name().equals("ADMIN");

        if (!isAdmin) {
            throw new CustomException(
                    ErrorCode.UNAUTHORIZED_ACTION,
                    "Acción no autorizada",
                    "No tiene permisos para realizar esta acción"
            );
        }
    }
}
