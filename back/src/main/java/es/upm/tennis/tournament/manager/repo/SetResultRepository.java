package es.upm.tennis.tournament.manager.repo;

import es.upm.tennis.tournament.manager.model.SetResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SetResultRepository extends JpaRepository<SetResult, Long> {
}
