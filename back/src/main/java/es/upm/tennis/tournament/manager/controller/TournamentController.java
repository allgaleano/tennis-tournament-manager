package es.upm.tennis.tournament.manager.controller;

import es.upm.tennis.tournament.manager.exceptions.AccountNotConfirmedException;
import es.upm.tennis.tournament.manager.exceptions.InvalidCodeException;
import es.upm.tennis.tournament.manager.exceptions.UnauthorizedUserAction;
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

    @PostMapping("/{tournamentId}/enroll/{playerId}")
    public ResponseEntity<Map<String, Object>> enrollPlayerToTournament(@PathVariable Long tournamentId, @PathVariable Long playerId, @RequestHeader("Session-Id") String sessionId) {
        try {
            tournamentService.enrollPlayerToTournament(tournamentId, playerId, sessionId);
            return ResponseEntity.ok(Map.of("message", "Player enrolled successfully"));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Tournament or player not found"));
        } catch (InvalidCodeException | AccountNotConfirmedException | UnauthorizedUserAction e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

}
