package es.upm.tennis.tournament.manager.repo;

import es.upm.tennis.tournament.manager.model.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SetRepository extends JpaRepository<Set, Long> {
}
