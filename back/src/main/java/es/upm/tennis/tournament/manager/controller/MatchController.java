package es.upm.tennis.tournament.manager.controller;

import es.upm.tennis.tournament.manager.DTO.MatchDTO;
import es.upm.tennis.tournament.manager.DTO.MatchScoreDTO;
import es.upm.tennis.tournament.manager.model.Match;
import es.upm.tennis.tournament.manager.service.MatchService;
import es.upm.tennis.tournament.manager.service.MatchScoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tournaments/{tournamentId}/matches")
@Slf4j
public class MatchController {

    private final MatchService matchService;
    private final MatchScoreService matchScoreService;

    public MatchController(
            MatchService matchService,
            MatchScoreService matchScoreService
    ) {
        this.matchService = matchService;
        this.matchScoreService = matchScoreService;
    }

    @GetMapping
    public ResponseEntity<List<MatchDTO>> getTournamentMatches(
            @PathVariable Long tournamentId,
            @RequestHeader("Session-Id") String sessionId) {
        return ResponseEntity.ok(matchService.getTournamentMatches(tournamentId, sessionId));
    }

    @PostMapping("/{matchId}/sets")
    public ResponseEntity<Map<String, String>> setScore(
            @PathVariable Long tournamentId,
            @PathVariable Long matchId,
            @RequestHeader ("Session-Id") String sessionId,
            @RequestBody MatchScoreDTO matchScoreDTO
    ) {
        matchScoreService.set(tournamentId, matchId, sessionId, matchScoreDTO);
        return ResponseEntity.ok(Map.of(
                "title", "Puntuaciones del partido registradas con éxito"
        ));
    }
}
