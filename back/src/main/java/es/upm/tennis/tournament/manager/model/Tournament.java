package es.upm.tennis.tournament.manager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import es.upm.tennis.tournament.manager.exceptions.CustomException;
import es.upm.tennis.tournament.manager.exceptions.ErrorCode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.*;

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

    @OneToMany(mappedBy = "tournament")
    @JsonIgnore
    private List<Match> matches = new ArrayList<>();

    public void distributeTournamentPoints() {
        List<TournamentRound> completedRounds = matches.stream()
                .filter(Match::isCompleted)
                .map(Match::getRound)
                .distinct()
                .sorted(Comparator.comparingInt(TournamentRound::getRoundNumber))
                .toList();

        if (completedRounds.isEmpty()) {
            throw new CustomException(
                    ErrorCode.INVALID_TOURNAMENT_STATUS,
                    "No hay rondas completadas en el torneo"
            );
        }

        Map<User, Integer> assignedPoints = new HashMap<>();
        List<TournamentParticipation> rankedParticipants = new ArrayList<>();

        for (TournamentRound round: completedRounds) {
            List<Match> roundMatches = matches.stream()
                    .filter(m -> m.getRound() == round && m.isCompleted())
                    .toList();

            List<User> roundLosers = roundMatches.stream()
                    .map(m -> m.getWinner().equals(m.getPlayer1()) ? m.getPlayer2() : m.getPlayer1())
                    .toList();

            List<TournamentParticipation> roundLosersParticipants = roundLosers.stream()
                    .filter(loser -> !assignedPoints.containsKey(loser))
                    .map(loser -> participants.stream()
                            .filter(p -> p.getPlayerStats().getPlayer().equals(loser))
                            .findFirst()
                            .orElseThrow(() -> new CustomException(
                                    ErrorCode.TOURNAMENT_PARTICIPATION_NOT_FOUND,
                                    "Participación en torneo no encontrada",
                                    "No se encontró la participación del jugador " + loser.getId() + " en el torneo " + id
                            )))
                    .sorted(this::compareParticipants)
                    .toList();

            if (round == completedRounds.getFirst()) {
                Match finalMatch = roundMatches.getFirst();
                TournamentParticipation winnerParticipation = participants.stream()
                        .filter(p -> p.getPlayerStats().getPlayer().equals(finalMatch.getWinner()))
                        .findFirst()
                        .orElseThrow(() -> new CustomException(
                                ErrorCode.TOURNAMENT_PARTICIPATION_NOT_FOUND,
                                "Participación en torneo no encontrada",
                                "No se encontró la participación del jugador " + finalMatch.getWinner().getId() + " en el torneo " + id
                        ));

                rankedParticipants.add(winnerParticipation);
                assignedPoints.put(finalMatch.getWinner(), 2000);
            }

            rankedParticipants.addAll(roundLosersParticipants);
        }

        distributePointsToRankedParticipants(rankedParticipants);
    }

    private void distributePointsToRankedParticipants(List<TournamentParticipation> rankedParticipants) {
        int[] topPoints = {2000, 1500, 1000, 500};

        for (int i = 0; i < rankedParticipants.size(); i++) {
            TournamentParticipation participation = rankedParticipants.get(i);
            int points;

            if (i < topPoints.length) {
                points = topPoints[i];
            } else {
                points = Math.max(0, 475 - ((i - topPoints.length) * 25));
            }

            participation.setPoints(points);
            participation.getPlayerStats().addRankingPoints(points);
        }
    }


    private int compareParticipants(TournamentParticipation p1, TournamentParticipation p2) {
        int setsComparison = Integer.compare(p2.getSetsWon(), p1.getSetsWon());
        if (setsComparison != 0) return setsComparison;

        int gamesComparison = Integer.compare(p2.getGamesWon(), p1.getGamesWon());
        if (gamesComparison != 0) return gamesComparison;

        int gamesLostComparison = Integer.compare(p1.getGamesLost(), p2.getGamesLost());
        if (gamesLostComparison != 0) return gamesLostComparison;

        int tiebreaksWonComparison = Integer.compare(p2.getTiebreakGamesWon(), p1.getTiebreakGamesWon());
        if (tiebreaksWonComparison != 0) return tiebreaksWonComparison;

        int tiebreaksLostComparison = Integer.compare(p1.getTiebreakGamesLost(), p2.getTiebreakGamesLost());
        if (tiebreaksLostComparison != 0) return tiebreaksLostComparison;

        return Math.random() < 0.5 ? -1 : 1;
    }

}
