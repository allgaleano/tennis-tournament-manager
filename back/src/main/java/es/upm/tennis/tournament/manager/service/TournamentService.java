package es.upm.tennis.tournament.manager.service;

import es.upm.tennis.tournament.manager.model.Tournament;
import es.upm.tennis.tournament.manager.repo.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class TournamentService {

    @Autowired
    TournamentRepository tournamentRepository;

    public Page<Tournament> getAllTournaments(Pageable pageable) {
        return tournamentRepository.findAll(pageable);
    }
}
