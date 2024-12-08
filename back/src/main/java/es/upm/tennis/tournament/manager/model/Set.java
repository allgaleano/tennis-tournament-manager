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

    @Column(name = "player1_tiebreak_games")
    private Integer player1TiebreakGames;

    @Column(name = "player2_tiebreak_games")
    private Integer player2TiebreakGames;

    public boolean isInvalidScore() {
        if (tiebreak) {
            if (player1TiebreakGames == null || player2TiebreakGames == null) {
                return true;
            }

            // Check if game scores are valid (6-7 or 7-6)
            if (!((player1Games == 6 && player2Games == 7) || (player1Games == 7 && player2Games == 6))) {
                return true;
            }

            if (player1Games == 7) {
                return player1TiebreakGames == 7 && player2TiebreakGames < 7;
            } else { // player2Games == 7
                return player1TiebreakGames > 7 && player2TiebreakGames == 7;
            }
        }

        return Math.max(player1Games, player2Games) != 7 || Math.abs(player1Games - player2Games) < 2;
    }

    public User getSetWinner() {
        if (isInvalidScore()) {
            return null;
        }

        return player1Games > player2Games ? match.getPlayer1() : match.getPlayer2();
    }
}
