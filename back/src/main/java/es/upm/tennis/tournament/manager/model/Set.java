package es.upm.tennis.tournament.manager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@ToString
@Table(name = "sets")
public class Set {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @Column(name = "set_number", nullable = false)
    private int setNumber;

    @Column(name = "player1_games", nullable = false)
    private int player1Games;

    @Column(name = "player2_games", nullable = false)
    private int player2Games;

    @Column(name = "tiebreak")
    private boolean tiebreak = false;

    @Column(name = "player1_tiebreak_games")
    private Integer player1TiebreakGames;

    @Column(name = "player2_tiebreak_games")
    private Integer player2TiebreakGames;

    public User getSetWinner() {
        return player1Games > player2Games ? match.getPlayer1() : match.getPlayer2();
    }
}
