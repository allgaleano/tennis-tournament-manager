package es.upm.tennis.tournament.manager.controller;

import es.upm.tennis.tournament.manager.DTO.MatchScoreDTO;
import es.upm.tennis.tournament.manager.service.SetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/tournaments/{tournamentId}/matches/{matchId}/sets")
@Slf4j
public class SetController {
    private final SetService setService;

    public SetController(SetService setService) {
        this.setService = setService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> setScore(
            @PathVariable Long tournamentId,
            @PathVariable Long matchId,
            @RequestHeader ("Session-Id") String sessionId,
            @RequestBody MatchScoreDTO matchScoreDTO
    ) {
        setService.setMatchScore(tournamentId, matchId, sessionId, matchScoreDTO);
        return ResponseEntity.ok(Map.of(
                "title", "Puntuaciones del partido registradas con éxito"
        ));
    }
}
