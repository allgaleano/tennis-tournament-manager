package es.upm.tennis.tournament.manager.repo;

import es.upm.tennis.tournament.manager.model.Match;
import es.upm.tennis.tournament.manager.model.TournamentRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {
    @Query("SELECT m FROM Match m WHERE m.tournament.id = :tournamentId " +
            "ORDER BY CASE m.round " +
            "  WHEN 'ROUND_16' THEN 1 " +
            "  WHEN 'QUARTER_FINALS' THEN 2 " +
            "  WHEN 'SEMIFINAL' THEN 3 " +
            "  WHEN 'FINAL' THEN 4 " +
            "END, " +
            "m.completed DESC, " +
            "CASE " +
            "  WHEN m.player1 IS NOT NULL AND m.player2 IS NOT NULL THEN 1 " +
            "  WHEN m.player1 IS NOT NULL OR m.player2 IS NOT NULL THEN 2 " +
            "  ELSE 3 " +
            "END")
    List<Match> findByTournamentIdOrderedByRoundAndPlayers(@Param("tournamentId") Long tournamentId);

    boolean existsByRoundAndTournamentIdAndCompletedFalse(TournamentRound round, Long tournamentId);
}
