package es.upm.tennis.tournament.manager.controller;

import es.upm.tennis.tournament.manager.DTO.PlayerStatsDTO;
import es.upm.tennis.tournament.manager.DTO.TournamentParticipationDTO;
import es.upm.tennis.tournament.manager.service.StatsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@Slf4j
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/tournaments/{tournamentId}/stats")
    public ResponseEntity<List<TournamentParticipationDTO>> getTournamentStats(
            @PathVariable Long tournamentId,
            @RequestHeader("Session-Id") String sessionId
    ) {
        return ResponseEntity.ok(statsService.getTournamentStats(tournamentId, sessionId));
    }

    @GetMapping("/players/{playerId}/stats")
    public ResponseEntity<PlayerStatsDTO> getPlayerStats(
            @PathVariable Long playerId,
            @RequestHeader("Session-Id") String sessionId
    ) {
        return ResponseEntity.ok(statsService.getPlayerStats(playerId, sessionId));
    }

    @GetMapping("/players/stats")
    public ResponseEntity<PagedModel<EntityModel<PlayerStatsDTO>>> getAllPlayersStats(
            @RequestHeader("Session-Id") String sessionId,
            Pageable pageable,
            PagedResourcesAssembler<PlayerStatsDTO> pagedResourcesAssembler
    ) {
        Page<PlayerStatsDTO> playerStats = statsService.getAllPlayersStats(sessionId, pageable);

        PagedModel<EntityModel<PlayerStatsDTO>> pagedModel = pagedResourcesAssembler.toModel(playerStats, EntityModel::of);
        return ResponseEntity.ok(pagedModel);
    }
}
