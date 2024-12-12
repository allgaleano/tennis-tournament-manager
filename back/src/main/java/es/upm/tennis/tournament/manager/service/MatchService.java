package es.upm.tennis.tournament.manager.service;

import es.upm.tennis.tournament.manager.DTO.MatchDTO;
import es.upm.tennis.tournament.manager.exceptions.CustomException;
import es.upm.tennis.tournament.manager.exceptions.ErrorCode;
import es.upm.tennis.tournament.manager.model.Match;
import es.upm.tennis.tournament.manager.model.Tournament;
import es.upm.tennis.tournament.manager.model.TournamentStatus;
import es.upm.tennis.tournament.manager.repo.MatchRepository;
import es.upm.tennis.tournament.manager.repo.TournamentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class MatchService {

    private final PermissionChecker permissionChecker;
    private final MatchRepository matchRepository;
    private final TournamentRepository tournamentRepository;

    public MatchService(
            PermissionChecker permissionChecker,
            MatchRepository matchRepository,
            TournamentRepository tournamentRepository
    ) {
        this.permissionChecker = permissionChecker;
        this.matchRepository = matchRepository;
        this.tournamentRepository = tournamentRepository;
    }

    public List<MatchDTO> getTournamentMatches(Long tournamentId, String sessionId) {
        permissionChecker.validateSession(sessionId);

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.TOURNAMENT_NOT_FOUND,
                        "Torneo no encontrado"
                ));

        if (!tournament.getStatus().equals(TournamentStatus.IN_PROGRESS) && !tournament.getStatus().equals(TournamentStatus.FINISHED)) {
            throw new CustomException(
                    ErrorCode.INVALID_TOURNAMENT_STATUS,
                    "Estado incorrecto del torneo",
                    "El torneo aún no ha comenzado"
            );
        }

        List<Match> matches =  matchRepository.findByTournamentIdOrderedByRoundAndPlayers(tournamentId);

        return matches.stream().map(MatchDTO::fromEntity).toList();
    }
}
