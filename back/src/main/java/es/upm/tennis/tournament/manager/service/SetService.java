package es.upm.tennis.tournament.manager.service;

import es.upm.tennis.tournament.manager.DTO.MatchScoreDTO;
import es.upm.tennis.tournament.manager.DTO.SetDTO;
import es.upm.tennis.tournament.manager.exceptions.CustomException;
import es.upm.tennis.tournament.manager.exceptions.ErrorCode;
import es.upm.tennis.tournament.manager.model.Match;
import es.upm.tennis.tournament.manager.model.Set;
import es.upm.tennis.tournament.manager.model.User;
import es.upm.tennis.tournament.manager.repo.MatchRepository;
import es.upm.tennis.tournament.manager.repo.SetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetService {

    private final PermissionChecker permissionChecker;
    private final MatchRepository matchRepository;
    private final SetRepository setRepository;

    public SetService(
            PermissionChecker permissionChecker,
            MatchRepository matchRepository,
            SetRepository setRepository
    ) {
        this.permissionChecker = permissionChecker;
        this.matchRepository = matchRepository;
        this.setRepository = setRepository;
    }

    public void setMatchScore(Long tournamentId, Long matchId, String sessionId, MatchScoreDTO matchScoreDTO) {

        permissionChecker.validateAdminPermission(sessionId);

        Match match = matchRepository.findById(matchId).orElseThrow(() -> new CustomException(
                ErrorCode.MATCH_NOT_FOUND,
                "Partido no encontrado",
                "El partido con id " + matchId + " no existe"
        ));

        if (!match.getTournament().getId().equals(tournamentId)) {
            throw new CustomException(
                    ErrorCode.MATCH_NOT_FOUND,
                    "Partido no encontrado",
                    "El partido con id " + matchId + " no pertenece al torneo con id " + tournamentId
            );
        }

        if (match.isCompleted()) {
            throw new CustomException(
                    ErrorCode.INVALID_MATCH_STATUS,
                    "Partido completado",
                    "El partido con id " + matchId + " ya ha sido completado"
            );
        }

        validateSetsSequence(matchScoreDTO.getSets());

        match.getSets().clear();

        matchScoreDTO.getSets().stream()
                .sorted(Comparator.comparingInt(SetDTO::getSetNumber))
                .forEach(setDTO -> {
                    Set set = createSet(setDTO);
                    set.setMatch(match);
                    validateSetScore(set);
                    match.getSets().add(set);
                });

        validateMatchScore(match);
        updateMatchStatus(match);

        matchRepository.save(match);
    }

    private void validateSetsSequence(List<SetDTO> sets) {
        if (sets.isEmpty()) {
            throw new CustomException(
                    ErrorCode.INVALID_SET_STATUS,
                    "Al menos un set es requerido"
            );
        }

        if (sets.size() > 5) {
            throw new CustomException(
                    ErrorCode.INVALID_SET_STATUS,
                    "No se pueden registrar más de 5 sets"
            );
        }

        // Verify set numbers are sequential and start from 1
        List<Integer> setNumbers = sets.stream()
                .map(SetDTO::getSetNumber)
                .sorted()
                .toList();

        for (int i = 0; i < setNumbers.size(); i++) {
            if (setNumbers.get(i) != i + 1) {
                throw new CustomException(
                        ErrorCode.INVALID_SET_STATUS,
                        "Los sets deben ser consecutivos y empezar desde 1"
                );
            }
        }
    }

    private Set createSet(SetDTO setDTO) {
        Set set = new Set();
        set.setSetNumber(setDTO.getSetNumber());
        set.setPlayer1Games(setDTO.getPlayer1Games());
        set.setPlayer2Games(setDTO.getPlayer2Games());

        if (setDTO.isTiebreak()) {
            if (setDTO.getPlayer1TiebreakPoints() == null || setDTO.getPlayer2TiebreakPoints() == null) {
                throw new CustomException(
                        ErrorCode.INVALID_SCORE,
                        "Puntos de tiebreak requeridos",
                        "Los puntos de tiebreak son requeridos para el set" + setDTO.getSetNumber()
                );
            }
            set.setTiebreak(true);
            set.setPlayer1TiebreakPoints(setDTO.getPlayer1TiebreakPoints());
            set.setPlayer2TiebreakPoints(setDTO.getPlayer2TiebreakPoints());
        }
        return set;
    }

    private void validateSetScore(Set set) {
        if (set.isInvalidScore()) {
            throw new CustomException(
                    ErrorCode.INVALID_SCORE,
                    "Puntuación inválida",
                    "La puntuación del set " + set.getSetNumber() + " es inválida"
            );
        }
    }

    private void validateMatchScore(Match match) {
        Map<User, Long> setsWon = match.getSets().stream()
                .map(Set::getSetWinner)
                .collect(Collectors.groupingBy(winner -> winner, Collectors.counting()));

        long maxSetsWon = setsWon.values().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0);

        if (maxSetsWon == 3 && match.getSets().size() > 5) {
            throw new CustomException(
                    ErrorCode.INVALID_MATCH_STATUS,
                    "Sets inválidos",
                    "El partido debería terminar si un jugador ya ha ganado 3 sets"
            );
        }
    }

    private void updateMatchStatus(Match match) {
        Map<User, Long> setsWon = match.getSets().stream()
                .map(Set::getSetWinner)
                .collect(Collectors.groupingBy(winner -> winner, Collectors.counting()));

        setsWon.forEach((player, wins) -> {
           if (wins >= 3) {
               match.setWinner(player);
               match.setCompleted(true);
           }
        });
    }
}
