package es.upm.tennis.tournament.manager.repo;

import es.upm.tennis.tournament.manager.model.EnrollmentStatus;
import es.upm.tennis.tournament.manager.model.TournamentEnrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TournamentEnrollmentRepository extends JpaRepository<TournamentEnrollment, Long> {
    boolean existsByTournamentIdAndPlayerId(Long tournamentId, Long playerId);

    @Query(
            "SELECT e FROM TournamentEnrollment e WHERE e.tournament.id = :tournamentId " +
                    "ORDER BY CASE e.status " +
                    "WHEN 'SELECTED' THEN 1 " +
                    "WHEN 'PENDING' THEN 2 " +
                    "WHEN 'DECLINED' THEN 3 " +
                    "END"
    )
    Page<TournamentEnrollment> findAllByTournamentIdOrderByCustomStatus(@Param("tournamentId") Long tournamentId, Pageable pageable);

    Optional<TournamentEnrollment> findByTournamentIdAndPlayerId(Long tournamentId, Long playerId);
    long countByTournamentIdAndStatus(Long tournamentId, EnrollmentStatus status);
    List<TournamentEnrollment> findByTournamentIdAndPlayerIdIn(Long tournamentId, List<Long> playerIds);
}
