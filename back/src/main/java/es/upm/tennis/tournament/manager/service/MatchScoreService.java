package es.upm.tennis.tournament.manager.service;

import es.upm.tennis.tournament.manager.DTO.MatchScoreDTO;
import es.upm.tennis.tournament.manager.DTO.SetDTO;
import es.upm.tennis.tournament.manager.exceptions.CustomException;
import es.upm.tennis.tournament.manager.exceptions.ErrorCode;
import es.upm.tennis.tournament.manager.model.Match;
import es.upm.tennis.tournament.manager.model.Set;
import es.upm.tennis.tournament.manager.model.User;
import es.upm.tennis.tournament.manager.repo.MatchRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class MatchScoreService {

    private final PermissionChecker permissionChecker;
    private final MatchRepository matchRepository;

    public MatchScoreService(
            PermissionChecker permissionChecker,
            MatchRepository matchRepository
    ) {
        this.permissionChecker = permissionChecker;
        this.matchRepository = matchRepository;
    }

    public void set(Long tournamentId, Long matchId, String sessionId, MatchScoreDTO matchScoreDTO) {

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

        if (match.getPlayer1() == null || match.getPlayer2() == null) {
            throw new CustomException(
                    ErrorCode.INVALID_MATCH_STATUS,
                    "Jugadores no asignados",
                    "Todos los jugadores del partido con id " + matchId + " no han sido asignados"
            );
        }

        validateSetsSequence(matchScoreDTO.getSets());

        match.getSets().clear();

        match.setPlayer1SetsWon(0);
        match.setPlayer2SetsWon(0);

        matchScoreDTO.getSets().stream()
                .sorted(Comparator.comparingInt(SetDTO::getSetNumber))
                .forEach(setDTO -> {
                    Set set = createSet(setDTO);
                    set.setMatch(match);
                    validateSetScore(set);
                    match.getSets().add(set);
                    updateSetsWonCount(match, set);
                });

        validateMatchScore(match);
        updateMatchStatus(match);

        Match nextMatch = match.getNextMatch();

        if (nextMatch != null) {
            if (nextMatch.getPlayer1() == null) {
                nextMatch.setPlayer1(match.getWinner());
            } else if (nextMatch.getPlayer2() == null) {
                nextMatch.setPlayer2(match.getWinner());
            } else {
                throw new CustomException(
                        ErrorCode.INVALID_MATCH_STATUS,
                        "Jugadores ya asignados",
                        "Los jugadores del siguiente partido ya han sido asignados"
                );
            }
            matchRepository.save(nextMatch);
        }
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
            if (setDTO.getPlayer1TiebreakGames() == null || setDTO.getPlayer2TiebreakGames() == null) {
                throw new CustomException(
                        ErrorCode.INVALID_SCORE,
                        "Juegos de tiebreak requeridos",
                        "Los juegos de tiebreak son requeridos para el set" + setDTO.getSetNumber()
                );
            }
            set.setTiebreak(true);
            set.setPlayer1TiebreakGames(setDTO.getPlayer1TiebreakGames());
            set.setPlayer2TiebreakGames(setDTO.getPlayer2TiebreakGames());
        }
        return set;
    }

    private void validateSetScore(Set set) {
        log.info("Validating set score for set {}", set.toString());
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

        if (maxSetsWon < 3) {
            throw new CustomException(
                    ErrorCode.INVALID_MATCH_STATUS,
                    "Sets insuficientes",
                    "Al menos un jugador debe ganar 3 sets para completar el partido"
            );
        }

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

    private void updateSetsWonCount(Match match, Set set) {
        User setWinner = set.getSetWinner();
        if (setWinner != null) {
            if (setWinner.equals(match.getPlayer1())) {
                match.setPlayer1SetsWon(match.getPlayer1SetsWon() + 1);
            } else if (setWinner.equals(match.getPlayer2())) {
                match.setPlayer2SetsWon(match.getPlayer2SetsWon() + 1);
            }
        }
    }
}
