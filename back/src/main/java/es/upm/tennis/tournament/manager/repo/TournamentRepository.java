package es.upm.tennis.tournament.manager.repo;

import es.upm.tennis.tournament.manager.model.Tournament;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    Optional<Tournament> findByName (String name);
    Page<Tournament> findAllByOrderByRegistrationDeadlineAsc(Pageable pageable);
}
