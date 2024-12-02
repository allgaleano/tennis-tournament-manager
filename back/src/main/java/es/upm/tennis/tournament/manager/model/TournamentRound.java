package es.upm.tennis.tournament.manager.model;

import es.upm.tennis.tournament.manager.exceptions.CustomException;
import es.upm.tennis.tournament.manager.exceptions.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum TournamentRound {
    ROUND_16(4, "ROUND_16"),
    QUARTER_FINALS(3,"QUARTER_FINALS"),
    SEMIFINAL(2, "SEMIFINAL"),
    FINAL(1,"FINAL");

    private final int roundNumber;
    private final String value;

    public static TournamentRound fromValue(String value) {
        for (TournamentRound round : TournamentRound.values()) {
            if (Objects.equals(round.getValue(), value)) {
                return round;
            }
        }
        throw new CustomException(
                ErrorCode.INVALID_TOURNAMENT_ROUND,
                "Invalid tournament round: " + value
        );
    }

    public static TournamentRound fromRoundNumber(int roundNumber) {
        for (TournamentRound round : TournamentRound.values()) {
            if (round.getRoundNumber() == roundNumber) {
                return round;
            }
        }
        throw new CustomException(
                ErrorCode.INVALID_TOURNAMENT_ROUND,
                "Invalid round number: " + roundNumber
        );
    }

    public TournamentRound getNextRound() {
        return fromRoundNumber(this.roundNumber - 1);
    }
}
