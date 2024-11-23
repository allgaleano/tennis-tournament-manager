package es.upm.tennis.tournament.manager.controller;

import es.upm.tennis.tournament.manager.DTO.TournamentEnrollmentDTO;
import es.upm.tennis.tournament.manager.exceptions.*;
import es.upm.tennis.tournament.manager.model.Tournament;
import es.upm.tennis.tournament.manager.service.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/tournaments")
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Tournament>>> getAllTournaments(Pageable pageable, PagedResourcesAssembler<Tournament> pagedResourcesAssembler) {
        Page<Tournament> tournaments = tournamentService.getAllTournaments(pageable);

        PagedModel<EntityModel<Tournament>> pagedModel = pagedResourcesAssembler.toModel(tournaments, EntityModel::of);
        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/{tournamentId}")
    public ResponseEntity<Tournament> getTournament(@PathVariable Long tournamentId) {
        try {
            return ResponseEntity.ok(tournamentService.getTournament(tournamentId));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/{tournamentId}/enroll/{playerId}")
    public ResponseEntity<Map<String, Object>> enrollPlayerToTournament(@PathVariable Long tournamentId, @PathVariable Long playerId, @RequestHeader("Session-Id") String sessionId) {
        try {
            tournamentService.enrollPlayerToTournament(tournamentId, playerId, sessionId);
            return ResponseEntity.ok(Map.of("message", "Player enrolled successfully"));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Tournament or player not found"));
        } catch (InvalidCodeException | AccountNotConfirmedException | UnauthorizedUserAction e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (PlayerAlreadyEnrolledException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{tournamentId}/enrollments")
    public ResponseEntity<PagedModel<EntityModel<TournamentEnrollmentDTO>>> getTournamentEnrollments(@PathVariable Long tournamentId, Pageable pageable, PagedResourcesAssembler<TournamentEnrollmentDTO> pagedResourcesAssembler) {
        try {
            Page<TournamentEnrollmentDTO> enrollments = tournamentService.getTournamentEnrollments(tournamentId, pageable);

            PagedModel<EntityModel<TournamentEnrollmentDTO>> pagedModel = pagedResourcesAssembler.toModel(enrollments, EntityModel::of);
            return ResponseEntity.ok(pagedModel);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{tournamentId}/unenroll/{playerId}")
    public ResponseEntity<Map<String, Object>> unenrollPlayerFromTournament(@PathVariable Long tournamentId, @PathVariable Long playerId, @RequestHeader("Session-Id") String sessionId) {
        try {
            tournamentService.unenrollPlayerFromTournament(tournamentId, playerId, sessionId);
            return ResponseEntity.ok(Map.of("message", "Player unenrolled successfully"));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Tournament or player not found"));
        } catch (InvalidCodeException | AccountNotConfirmedException | UnauthorizedUserAction e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (PlayerNotEnrolledException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "error", e.getMessage(),
                    "type", "PlayerNotEnrolled"
            ));
        } catch (PlayerAlreadyAcceptedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "error", e.getMessage(),
                    "type", "PlayerAlreadyAccepted"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{tournamentId}/enrolled/{playerId}")
    public ResponseEntity<Map<String, Object>> isPlayerEnrolled(@PathVariable Long tournamentId, @PathVariable Long playerId) {
        try {
            boolean isEnrolled = tournamentService.isPlayerEnrolled(tournamentId, playerId);
            return ResponseEntity.ok(Map.of("enrolled", isEnrolled));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Tournament or player not found"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{tournamentId}/selectPlayer/{playerId}")
    public ResponseEntity<Map<String, Object>> selectPlayer(@PathVariable Long tournamentId, @PathVariable Long playerId, @RequestHeader("Session-Id") String sessionId) {
        try {
            tournamentService.selectPlayer(tournamentId, playerId, sessionId);
            return ResponseEntity.ok(Map.of("message", "Player selected successfully"));
        } catch (InvalidCodeException | UnauthorizedUserAction e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

}
