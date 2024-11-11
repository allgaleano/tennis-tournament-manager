package es.upm.tennis.tournament.manager.service;

import es.upm.tennis.tournament.manager.exceptions.AccountNotConfirmedException;
import es.upm.tennis.tournament.manager.exceptions.InvalidCodeException;
import es.upm.tennis.tournament.manager.exceptions.UnauthorizedUserAction;
import es.upm.tennis.tournament.manager.model.User;
import es.upm.tennis.tournament.manager.model.UserSession;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class PermissionChecker {

    public void validateUserPermission (User user, UserSession userSession) {
        if (userSession == null || userSession.getExpirationDate().isBefore(Instant.now())) {
            throw new InvalidCodeException("Invalid or expired session");
        }
        boolean isAdmin = userSession.getUser().getRole().getType().name().equals("ADMIN");

        if (!isAdmin && !user.isConfirmed()) {
            throw new AccountNotConfirmedException("Account not confirmed");
        }

        if (!isAdmin && !user.getId().equals(userSession.getUser().getId())) {
            throw new UnauthorizedUserAction("Unauthorized to perform this action");
        }
    }
}
