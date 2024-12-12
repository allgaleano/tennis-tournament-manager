package es.upm.tennis.tournament.manager.DTO;

import es.upm.tennis.tournament.manager.model.TournamentParticipation;
import es.upm.tennis.tournament.manager.model.User;
import lombok.Data;

@Data
public class TournamentParticipationDTO {
    private UserPublicDTO player;
    private int matchesPlayed;
    private int matchesWon;
    private int matchesLost;
    private int setsWon;
    private int setsLost;
    private int gamesWon;
    private int gamesLost;
    private int tiebreakGamesWon;
    private int tiebreakGamesLost;
    private int points;

    public static TournamentParticipationDTO fromEntity(TournamentParticipation participation) {
        if (participation == null) {
            return null;
        }

        TournamentParticipationDTO dto = new TournamentParticipationDTO();
        dto.setPlayer(UserPublicDTO.fromEntity(participation.getPlayerStats().getPlayer()));
        dto.setMatchesPlayed(participation.getMatchesPlayed());
        dto.setMatchesWon(participation.getMatchesWon());
        dto.setMatchesLost(participation.getMatchesLost());
        dto.setSetsWon(participation.getSetsWon());
        dto.setSetsLost(participation.getSetsLost());
        dto.setGamesWon(participation.getGamesWon());
        dto.setGamesLost(participation.getGamesLost());
        dto.setTiebreakGamesWon(participation.getTiebreakGamesWon());
        dto.setTiebreakGamesLost(participation.getTiebreakGamesLost());
        dto.setPoints(participation.getPoints());
        return dto;
    }
}
