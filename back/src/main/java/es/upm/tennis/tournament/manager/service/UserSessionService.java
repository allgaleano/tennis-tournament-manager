package es.upm.tennis.tournament.manager.service;

import es.upm.tennis.tournament.manager.model.User;
import es.upm.tennis.tournament.manager.model.UserSession;
import es.upm.tennis.tournament.manager.repo.UserRepository;
import es.upm.tennis.tournament.manager.repo.UserSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserSessionService {

    @Autowired
    private UserSessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    private static final int SESSION_DURATION_MINUTES = 30;

    public String createSession(User user) { // TODO: Fix existing session problem
        if (user.getSession() != null) {
            // Nullify the session field on the user entity
            user.setSession(null);
            userRepository.save(user);  // Persist this change to break the association

            // Delete the session from the repository
            sessionRepository.delete(user.getSession());
            sessionRepository.flush();  // Ensure deletion happens immediately
        }

        UserSession session = new UserSession();
        session.setUser(user);
        session.setSessionId(UUID.randomUUID().toString());
        session.setExpirationDate(LocalDateTime.now().plusMinutes(SESSION_DURATION_MINUTES));

        user.setSession(session);
        sessionRepository.save(session);

        return session.getSessionId();
    }

    public boolean validateSession(String sessionId) {
        UserSession session = sessionRepository.findBySessionId(sessionId);
        if (session != null && session.getExpirationDate().isAfter(LocalDateTime.now())) {
            // Refresh session expiration date
            session.setExpirationDate(LocalDateTime.now().plusMinutes(SESSION_DURATION_MINUTES));
            sessionRepository.save(session);
            return true;
        }
        return false;
    }

    public void invalidateSession(String sessionId) {
        UserSession session = sessionRepository.findBySessionId(sessionId);
        if (session != null) {
            sessionRepository.delete(session);
        }
    }

    public UserSession findBySessionId(String sessionId) {
        return sessionRepository.findBySessionId(sessionId);
    }


}
