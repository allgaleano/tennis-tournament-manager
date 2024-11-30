package es.upm.tennis.tournament.manager.DTO;

import lombok.Getter;

import java.util.List;

@Getter
public class PlayerIdsRequest {
    private List<Long> playerIds;
}
