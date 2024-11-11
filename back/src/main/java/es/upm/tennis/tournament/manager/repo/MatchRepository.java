package es.upm.tennis.tournament.manager.repo;

import es.upm.tennis.tournament.manager.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByTournamentIdAndRound(Long tournamentId, Integer round);
}
