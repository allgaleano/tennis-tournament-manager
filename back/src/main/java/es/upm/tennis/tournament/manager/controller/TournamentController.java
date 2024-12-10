package es.upm.tennis.tournament.manager.controller;

import es.upm.tennis.tournament.manager.DTO.PlayerIdsRequest;
import es.upm.tennis.tournament.manager.DTO.TournamentDTO;
import es.upm.tennis.tournament.manager.DTO.TournamentEnrollmentDTO;
import es.upm.tennis.tournament.manager.model.Tournament;
import es.upm.tennis.tournament.manager.service.StartTournamentService;
import es.upm.tennis.tournament.manager.service.TournamentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/tournaments")
@Slf4j
public class TournamentController {

    private final TournamentService tournamentService;
    private final StartTournamentService startTournamentService;

    public TournamentController(
            TournamentService tournamentService,
            StartTournamentService startTournamentService
    ) {
        this.tournamentService = tournamentService;
        this.startTournamentService = startTournamentService;
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Tournament>>> getAllTournaments(
            Pageable pageable,
            PagedResourcesAssembler<Tournament> pagedResourcesAssembler
    ) {
        log.info("Getting all tournaments");
        Page<Tournament> tournaments = tournamentService.getAllTournaments(pageable);
        PagedModel<EntityModel<Tournament>> pagedModel = pagedResourcesAssembler.toModel(tournaments, EntityModel::of);
        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/{tournamentId}")
    public ResponseEntity<TournamentDTO> getTournament(
            @PathVariable Long tournamentId
    ) {
        return ResponseEntity.ok(tournamentService.getTournament(tournamentId));
    }

    @PostMapping("/{tournamentId}/enroll/{playerId}")
    public ResponseEntity<Map<String, String>> enrollPlayerToTournament(
            @PathVariable Long tournamentId,
            @PathVariable Long playerId,
            @RequestHeader("Session-Id") String sessionId
    ) {
        tournamentService.enrollPlayerToTournament(tournamentId, playerId, sessionId);
        return ResponseEntity.ok(Map.of(
                "title", "Inscripción realizada con éxito"
        ));
    }

    @GetMapping("/{tournamentId}/enrollments")
    public ResponseEntity<PagedModel<EntityModel<TournamentEnrollmentDTO>>> getTournamentEnrollments(
            @PathVariable Long tournamentId,
            Pageable pageable,
            PagedResourcesAssembler<TournamentEnrollmentDTO> pagedResourcesAssembler
    ) {
            Page<TournamentEnrollmentDTO> enrollments = tournamentService.getTournamentEnrollments(tournamentId, pageable);

            PagedModel<EntityModel<TournamentEnrollmentDTO>> pagedModel = pagedResourcesAssembler.toModel(enrollments, EntityModel::of);
            return ResponseEntity.ok(pagedModel);
    }

    @DeleteMapping("/{tournamentId}/unenroll/{playerId}")
    public ResponseEntity<Map<String, Object>> unenrollPlayerFromTournament(
            @PathVariable Long tournamentId,
            @PathVariable Long playerId,
            @RequestHeader("Session-Id") String sessionId
    ) {
        tournamentService.unenrollPlayerFromTournament(tournamentId, playerId, sessionId);
        return ResponseEntity.ok(Map.of(
                "title", "Desinscripción realizada con éxito"
        ));
    }

    @GetMapping("/{tournamentId}/enrolled/{playerId}")
    public ResponseEntity<Map<String, Object>> isPlayerEnrolled(
            @PathVariable Long tournamentId,
            @PathVariable Long playerId
    ) {
        boolean isEnrolled = tournamentService.isPlayerEnrolled(tournamentId, playerId);
        return ResponseEntity.ok(Map.of(
                "enrolled", isEnrolled
        ));
    }

    @PostMapping("/{tournamentId}/selectPlayers")
    public ResponseEntity<Map<String, Object>> selectPlayer(
            @PathVariable Long tournamentId,
            @RequestBody PlayerIdsRequest playerIds,
            @RequestHeader("Session-Id") String sessionId
    ) {
        tournamentService.selectPlayer(tournamentId, playerIds, sessionId);
        return ResponseEntity.ok(Map.of(
                "title", "Jugadores seleccionados con éxito",
                "description", String.format("%d jugadores seleccionados", playerIds.getPlayerIds().size())
        ));
    }

    @PostMapping("/{tournamentId}/deselectPlayers")
    public ResponseEntity<Map<String, Object>> deselectPlayer(
            @PathVariable Long tournamentId,
            @RequestBody PlayerIdsRequest playerIds,
            @RequestHeader("Session-Id") String sessionId
    ) {
        tournamentService.deselectPlayer(tournamentId, playerIds, sessionId);
        return ResponseEntity.ok(Map.of(
                "title", "Juagadores deseleccionados con éxito",
                "description", String.format("%d jugadores deseleccionados", playerIds.getPlayerIds().size())
        ));
    }

    @PutMapping("/{tournamentId}/closeEnrollments")
    public ResponseEntity<Map<String, String>> closeEnrollments(
            @PathVariable Long tournamentId,
            @RequestHeader("Session-Id") String sessionId
    ) {
        tournamentService.closeEnrollments(tournamentId, sessionId);
        return ResponseEntity.ok(Map.of(
                "title", "Inscripciones cerradas con éxito"
        ));
    }

    @PutMapping("/{tournamentId}/openEnrollments")
    public ResponseEntity<Map<String, String>> reopenEnrollments(
            @PathVariable Long tournamentId,
            @RequestHeader("Session-Id") String sessionId
    ) {
        tournamentService.openEnrollments(tournamentId, sessionId);
        return ResponseEntity.ok(Map.of(
                "title", "Inscripciones abiertas con éxito"
        ));
    }

    @PostMapping("/{tournamentId}/start")
    public ResponseEntity<Map<String, String>> startTournament(
            @PathVariable Long tournamentId,
            @RequestHeader("Session-Id") String sessionId
    ) {
        startTournamentService.startTournament(tournamentId, sessionId);
        return ResponseEntity.ok(Map.of(
                "title", "Torneo iniciado con éxito"
        ));
    }
}
