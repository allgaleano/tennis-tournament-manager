package es.upm.tennis.tournament.manager.controller;

import es.upm.tennis.tournament.manager.model.Tournament;
import es.upm.tennis.tournament.manager.service.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
