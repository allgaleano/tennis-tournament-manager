package es.upm.tennis.tournament.manager.repo;

import es.upm.tennis.tournament.manager.model.PlayerStats;
import es.upm.tennis.tournament.manager.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PlayerStatsRepository extends JpaRepository<PlayerStats, Long> {
    Optional<PlayerStats> findByPlayer(User player);

    @Query(value = """
            WITH ranked_stats AS (
                SELECT *,\s
                ROW_NUMBER() OVER (ORDER BY\s
                    ranking_points DESC,
                    total_matches_won DESC,
                    total_sets_won DESC,
                    total_games_won DESC,
                    total_games_lost ASC,
                    total_tiebreak_games_won DESC,
                    total_tiebreak_games_lost ASC
                ) as position\s
                FROM players_stats
            )
            SELECT rs.id, rs.player_id, rs.ranking_points, rs.tournaments_played,\s
                   rs.tournaments_won, rs.total_matches_played, rs.total_matches_won,
                   rs.total_matches_lost, rs.total_sets_won, rs.total_sets_lost,
                   rs.total_games_won, rs.total_games_lost, rs.total_tiebreak_games_won,
                   rs.total_tiebreak_games_lost, rs.position
            FROM ranked_stats rs
            ORDER BY rs.position
           \s""",
            countQuery = "SELECT count(*) FROM players_stats",
            nativeQuery = true)
    Page<Object[]> findAllWithRankingPosition(Pageable pageable);

    @Query(value = """
            WITH rankings AS (
                SELECT *,
                ROW_NUMBER() OVER (ORDER BY\s
                    ranking_points DESC,
                    total_matches_won DESC,
                    total_sets_won DESC,
                    total_games_won DESC,
                    total_games_lost ASC,
                    total_tiebreak_games_won DESC,
                    total_tiebreak_games_lost ASC
                ) as ranking_position
                FROM players_stats
            )
            SELECT ranking_position\s
            FROM rankings\s
            WHERE player_id = :playerId
           \s""", nativeQuery = true)
    Optional<Integer> findPlayerRankingPosition(@Param("playerId") Long playerId);
}
