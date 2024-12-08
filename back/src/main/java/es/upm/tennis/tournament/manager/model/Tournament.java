package es.upm.tennis.tournament.manager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "tournaments")
public class Tournament {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Temporal(TemporalType.TIMESTAMP)
    private Instant registrationDeadline;

    @Column(nullable = false)
    private int maxPlayers = 16;

    @Column(nullable = false)
    private int minPlayers = 4;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TournamentStatus status;

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<TournamentParticipation> participants = new ArrayList<>();

    public void distributeTournamentPoints() {
        List<TournamentParticipation> rankedParticipants = getRankedParticipants();

        int [] pointDistribution = {2000, 1500, 1000, 500};

        for (int i = 0; i < rankedParticipants.size(); i++) {
            TournamentParticipation participation = rankedParticipants.get(i);

            if (i < pointDistribution.length) {
                participation.setPoints(pointDistribution[i]);
                participation.getPlayerStats().addRankingPoints(pointDistribution[i]);
            } else {
                participation.setPoints(475 - (i - pointDistribution.length) * 25);
                participation.getPlayerStats().addRankingPoints(475 - (i - pointDistribution.length) * 25);
            }
        }
    }

    private List<TournamentParticipation> getRankedParticipants() {
        return participants.stream()
                .sorted((p1, p2) -> {
                    int setsComparison = Integer.compare(p1.getSetsWon(), p2.getSetsWon());
                    if (setsComparison != 0) return setsComparison;

                    int gamesComparison = Integer.compare(p1.getGamesWon(), p2.getGamesWon());
                    if (gamesComparison != 0) return gamesComparison;

                    int gamesLostComparison = Integer.compare(p2.getGamesLost(), p1.getGamesLost());
                    if (gamesLostComparison != 0) return gamesLostComparison;

                    int tieBreaksWonComparison = Integer.compare(p1.getTieBreaksWon(), p2.getTieBreaksWon());
                    if (tieBreaksWonComparison != 0) return tieBreaksWonComparison;

                    int tieBreaksLostComparison = Integer.compare(p2.getTieBreaksLost(), p1.getTieBreaksLost());
                    if (tieBreaksLostComparison != 0) return tieBreaksLostComparison;


                    return Math.random() < 0.5 ? -1 : 1;
                })
                .toList();
    }
}
