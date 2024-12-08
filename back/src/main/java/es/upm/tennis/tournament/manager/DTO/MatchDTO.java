package es.upm.tennis.tournament.manager.DTO;

import es.upm.tennis.tournament.manager.model.Match;
import es.upm.tennis.tournament.manager.model.TournamentRound;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MatchDTO {
    private Long id;
    private UserPublicDTO player1;
    private UserPublicDTO player2;
    private TournamentRound round;
    private UserPublicDTO winner;
    private boolean completed;
    private Integer player1SetsWon;
    private Integer player2SetsWon;
    private List<SetDTO> sets;

    public static MatchDTO fromEntity(Match match) {
        if (match == null) {
            return null;
        }
        MatchDTO matchDTO = new MatchDTO();
        matchDTO.setId(match.getId());
        matchDTO.setPlayer1(UserPublicDTO.fromEntity(match.getPlayer1()));
        matchDTO.setPlayer2(UserPublicDTO.fromEntity(match.getPlayer2()));
        matchDTO.setRound(match.getRound());
        matchDTO.setWinner(UserPublicDTO.fromEntity(match.getWinner()));
        matchDTO.setCompleted(match.isCompleted());
        matchDTO.setPlayer1SetsWon(match.getPlayer1SetsWon());
        matchDTO.setPlayer2SetsWon(match.getPlayer2SetsWon());
        matchDTO.setSets(SetDTO.fromEntityList(match.getSets()));
        return matchDTO;
    }
}
