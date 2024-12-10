package es.upm.tennis.tournament.manager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "player1_sets")
    private Integer player1SetsWon;

    @Column(name = "player2_sets")
    private Integer player2SetsWon;

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderBy("setNumber ASC")
    private List<Set> sets = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "next_match_id")
    private Match nextMatch;
}
