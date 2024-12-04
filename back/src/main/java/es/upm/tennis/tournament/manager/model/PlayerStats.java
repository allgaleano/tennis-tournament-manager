package es.upm.tennis.tournament.manager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "player_stats")
public class PlayerStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "player_id", nullable = false)
    private User player;

    private int rankingPoints = 0;
    private int rankingPosition;
    private int tournamentsPlayed = 0;
    private int tournamentsWon = 0;
    private int totalMatchesPlayed = 0;
    private int totalMatchesWon = 0;
    private int totalMatchesLost = 0;
    private int totalSetsWon = 0;
    private int totalSetsLost = 0;
    private int totalGamesWon = 0;
    private int totalGamesLost = 0;
    private int totalTieBreaksWon = 0;
    private int totalTieBreaksLost = 0;
}
