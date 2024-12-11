package es.upm.tennis.tournament.manager.controller;

import es.upm.tennis.tournament.manager.DTO.TournamentParticipationDTO;
import es.upm.tennis.tournament.manager.service.StatsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("tournaments/{tournamentId}/stats")
@Slf4j
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping
    public ResponseEntity<List<TournamentParticipationDTO>> getTournamentStats(
            @PathVariable Long tournamentId,
            @RequestHeader("Session-Id") String sessionId
    ) {
        return ResponseEntity.ok(statsService.getTournamentStats(tournamentId, sessionId));
    }
}
