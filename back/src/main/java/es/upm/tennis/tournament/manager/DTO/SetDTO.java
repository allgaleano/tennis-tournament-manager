package es.upm.tennis.tournament.manager.DTO;

import es.upm.tennis.tournament.manager.model.Set;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SetDTO {
    private int setNumber;
    private int player1Games;
    private int player2Games;
    private boolean tiebreak;
    private Integer player1TiebreakGames;
    private Integer player2TiebreakGames;

    public static List<SetDTO> fromEntityList(List<Set> sets) {
        List<SetDTO> setsDTO = new ArrayList<>();
        for (Set set : sets) {
            SetDTO setDTO = new SetDTO();
            setDTO.setSetNumber(set.getSetNumber());
            setDTO.setPlayer1Games(set.getPlayer1Games());
            setDTO.setPlayer2Games(set.getPlayer2Games());
            if (set.isTiebreak()) {
                setDTO.setPlayer1TiebreakGames(set.getPlayer1TiebreakGames());
                setDTO.setPlayer2TiebreakGames(set.getPlayer2TiebreakGames());
            }
            setDTO.setTiebreak(set.isTiebreak());
            setsDTO.add(setDTO);
        }
        return setsDTO;
    }
}
