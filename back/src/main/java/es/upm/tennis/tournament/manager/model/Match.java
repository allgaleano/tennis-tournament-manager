package es.upm.tennis.tournament.manager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "matches")
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @ManyToOne
    @JoinColumn(name = "player1_id")
    private User player1;

    @ManyToOne
    @JoinColumn(name = "player2_id")
    private User player2;

    @Enumerated(EnumType.STRING)
    @Column(name = "round", nullable = false)
    private TournamentRound round;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_id")
    private User winner;

    @Column(name = "completed", nullable = false)
    private boolean completed = false;

    private Integer set1Player1Games;
    private Integer set1Player2Games;
    private Integer set1Player1TieBreakGames;
    private Integer set1Player2TieBreakGames;

    private Integer set2Player1Games;
    private Integer set2Player2Games;
    private Integer set2Player1TieBreakGames;
    private Integer set2Player2TieBreakGames;

    private Integer set3Player1Games;
    private Integer set3Player2Games;
    private Integer set3Player1TieBreakGames;
    private Integer set3Player2TieBreakGames;

    private Integer set4Player1Games;
    private Integer set4Player2Games;
    private Integer set4Player1TieBreakGames;
    private Integer set4Player2TieBreakGames;

    private Integer set5Player1Games;
    private Integer set5Player2Games;
    private Integer set5Player1TieBreakGames;
    private Integer set5Player2TieBreakGames;
}
