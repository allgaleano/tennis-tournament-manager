package es.upm.tennis.tournament.manager.repo;

import es.upm.tennis.tournament.manager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
