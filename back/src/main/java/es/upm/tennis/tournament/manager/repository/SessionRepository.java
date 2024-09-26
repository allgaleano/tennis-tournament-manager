package es.upm.tennis.tournament.manager.repository;

import es.upm.tennis.tournament.manager.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {
    Optional<Session> findBySessionId(String sessionId);
}
