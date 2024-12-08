package es.upm.tennis.tournament.manager.DTO;

import lombok.Data;

import java.util.List;

@Data
public class MatchScoreDTO {
    private List<SetDTO> sets;
}
