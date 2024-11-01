package es.upm.tennis.tournament.manager.service;

import es.upm.tennis.tournament.manager.exceptions.PlayerAlreadyEnrolledException;
import es.upm.tennis.tournament.manager.model.PlayerTournament;
import es.upm.tennis.tournament.manager.model.Tournament;
import es.upm.tennis.tournament.manager.model.User;
import es.upm.tennis.tournament.manager.model.UserSession;
import es.upm.tennis.tournament.manager.repo.PlayerTournamentRepository;
import es.upm.tennis.tournament.manager.repo.TournamentRepository;
import es.upm.tennis.tournament.manager.repo.UserRepository;
import es.upm.tennis.tournament.manager.repo.UserSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class TournamentService {

    @Autowired
    TournamentRepository tournamentRepository;

    @Autowired
    PermissionChecker permissionChecker;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserSessionRepository userSessionRepository;

    @Autowired
    PlayerTournamentRepository playerTournamentRepository;

    public Page<Tournament> getAllTournaments(Pageable pageable) {
        return tournamentRepository.findAll(pageable);
    }

    public void enrollPlayerToTournament(Long tournamentId, Long playerId, String sessionId) {
        Tournament tournament = tournamentRepository.findById(tournamentId).orElseThrow();

        User user = userRepository.findById(playerId).orElseThrow();

        UserSession userSession = userSessionRepository.findBySessionId(sessionId);

        permissionChecker.validateUserPermission(user, userSession);

        PlayerTournament playerTournament = new PlayerTournament();

        playerTournament.setPlayer(user);
        playerTournament.setTournament(tournament);

        if (playerTournamentRepository.existsByTournamentIdAndPlayerId(tournamentId, playerId)) {
            throw new PlayerAlreadyEnrolledException("Player already enrolled in the tournament");
        }

        playerTournamentRepository.save(playerTournament);
    }
}
