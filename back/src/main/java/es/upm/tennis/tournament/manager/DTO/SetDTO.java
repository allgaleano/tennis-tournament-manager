package es.upm.tennis.tournament.manager.DTO;

import lombok.Data;

@Data
public class SetDTO {
    private int setNumber;
    private int player1Games;
    private int player2Games;
    private boolean tiebreak;
    private Integer player1TiebreakPoints;
    private Integer player2TiebreakPoints;
}
