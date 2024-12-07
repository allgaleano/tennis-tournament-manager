package es.upm.tennis.tournament.manager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
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

    @Column(name = "player1_tiebreak_points")
    private Integer player1TiebreakPoints;

    @Column(name = "player2_tiebreak_points")
    private Integer player2TiebreakPoints;

    public boolean isInvalidScore() {
        if (tiebreak) {
            return player1TiebreakPoints == null || player2TiebreakPoints == null ||
                    player1Games != 6 || player2Games != 6 ||
                    Math.max(player1TiebreakPoints, player2TiebreakPoints) != 7;
        }

        return Math.max(player1Games, player2Games) != 7 || Math.abs(player1Games - player2Games) < 2;
    }

    public User getSetWinner() {
        if (isInvalidScore()) {
            return null;
        }

        if (tiebreak) {
            return player1TiebreakPoints > player2TiebreakPoints ?
                    match.getPlayer1() : match.getPlayer2();
        }

        return player1Games > player2Games ? match.getPlayer1() : match.getPlayer2();
    }
}
