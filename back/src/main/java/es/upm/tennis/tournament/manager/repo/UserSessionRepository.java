package es.upm.tennis.tournament.manager.repo;

import es.upm.tennis.tournament.manager.model.User;
import es.upm.tennis.tournament.manager.model.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    UserSession findBySessionId(String sessionId);
    UserSession findByUser(User user);
}
