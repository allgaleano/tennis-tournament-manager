package es.upm.tennis.tournament.manager.repo;

import es.upm.tennis.tournament.manager.model.PlayerStats;
import es.upm.tennis.tournament.manager.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PlayerStatsRepository extends JpaRepository<PlayerStats, Long> {
    Optional<PlayerStats> findByPlayer(User player);

    @Query("SELECT p FROM PlayerStats p ORDER BY " +
            "p.rankingPoints DESC, " +
            "p.totalMatchesWon DESC, " +
            "p.totalSetsWon DESC, " +
            "p.totalGamesWon DESC, " +
            "p.totalGamesLost ASC, " +
            "p.totalTiebreakGamesWon DESC, " +
            "p.totalTiebreakGamesLost ASC")
    Page<PlayerStats> findAllOrderedByStats(Pageable pageable);
}
