package es.upm.tennis.tournament.manager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "player_tournament")
public class PlayerTournament {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    private User player;

    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    private int matchesPlayed = 0;
    private int matchesWon = 0;
    private int matchesLost = 0;
    private int setsWon = 0;
    private int setsLost = 0;
    private int gamesWon = 0;
    private int gamesLost = 0;
    private int tieBreaksWon = 0;
    private int tieBreaksLost = 0;
    private int points = 0;
}
