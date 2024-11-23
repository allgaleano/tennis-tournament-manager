package es.upm.tennis.tournament.manager.repo;

import es.upm.tennis.tournament.manager.model.EnrollmentStatus;
import es.upm.tennis.tournament.manager.model.TournamentEnrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TournamentEnrollmentRepository extends JpaRepository<TournamentEnrollment, Long> {
    boolean existsByTournamentIdAndPlayerId(Long tournamentId, Long playerId);
    Page<TournamentEnrollment> findAllByTournamentId(Long tournamentId, Pageable pageable);
    Optional<TournamentEnrollment> findByTournamentIdAndPlayerId(Long tournamentId, Long playerId);
    long countByTournamentIdAndStatus(Long tournamentId, EnrollmentStatus status);
}
