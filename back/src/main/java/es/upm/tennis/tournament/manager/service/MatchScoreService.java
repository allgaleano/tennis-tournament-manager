package es.upm.tennis.tournament.manager.service;

import es.upm.tennis.tournament.manager.DTO.MatchScoreDTO;
import es.upm.tennis.tournament.manager.DTO.SetDTO;
import es.upm.tennis.tournament.manager.exceptions.CustomException;
import es.upm.tennis.tournament.manager.exceptions.ErrorCode;
import es.upm.tennis.tournament.manager.model.Match;
import es.upm.tennis.tournament.manager.model.Set;
import es.upm.tennis.tournament.manager.model.TournamentRound;
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
    private final StatsService statsService;

    public MatchScoreService(
            PermissionChecker permissionChecker,
            MatchRepository matchRepository,
            StatsService statsService
    ) {
        this.permissionChecker = permissionChecker;
        this.matchRepository = matchRepository;
        this.statsService = statsService;
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
        validatePreviousRoundsCompleted(match);

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

        if (match.isCompleted()) {
            statsService.update(match);
            updateNextMatch(match);
        }
        matchRepository.save(match);
    }

    private void updateNextMatch(Match match) {
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
        boolean p1Won = set.getPlayer1Games() > set.getPlayer2Games();
        if (set.getPlayer1Games() == set.getPlayer2Games()) {
            throwInvalidScoreException(
                    "Juegos inválidos para el set " + set.getSetNumber(),
                    "Los juegos de ambos jugadores no pueden ser iguales"
            );
        }
        if (set.isTiebreak()) {
            if (p1Won) {
                if (set.getPlayer1Games() != 7) {
                    throwInvalidScoreException(
                            "Juegos inválidos para el set " + set.getSetNumber(),
                            "Para ganar el set con tiebreak, el jugador 1 debe haber ganado 7 juegos"
                    );
                }

                if (set.getPlayer2Games() != 6) {
                    throwInvalidScoreException(
                            "Juegos inválidos para el set " + set.getSetNumber(),
                            "Si el jugador 1 ganó el set con tiebreak, el jugador 2 debe haber ganado 6 juegos"
                    );
                }

                if (set.getPlayer1TiebreakGames() <= set.getPlayer2TiebreakGames()) {
                    throwInvalidScoreException(
                            "Juegos de tiebreak inválidos para el set "+ set.getSetNumber(),
                            "El jugador 1 debe haber ganado más juegos de tiebreak que el jugador 2"
                    );
                }

                if (set.getPlayer1TiebreakGames() < 7) {
                    throwInvalidScoreException(
                            "Juegos de tiebreak inválidos para el set " + set.getSetNumber(),
                            "El jugador 1 debe haber ganado al menos 7 juegos de tiebreak"
                    );
                }

                if (Math.abs(set.getPlayer1TiebreakGames() - set.getPlayer2TiebreakGames()) < 2) {
                    throwInvalidScoreException(
                            "Juegos de tiebreak inválidos para el set " + set.getSetNumber(),
                            "La diferencia de juegos de tiebreak entre los jugadores debe ser al menos de 2"
                    );
                }

                if ((Math.abs(set.getPlayer1TiebreakGames() - set.getPlayer2TiebreakGames()) > 2) && (set.getPlayer1TiebreakGames() > 7)) {
                    throwInvalidScoreException(
                            "Juegos de tiebreak inválidos para el set " + set.getSetNumber(),
                            "El jugador 1 no puede haber ganado más de 7 juegos de tiebreak si la diferencia es mayor a 2"
                    );
                }
            } else {
                if (set.getPlayer2Games() != 7) {
                    throwInvalidScoreException(
                            "Juegos inválidos para el set " + set.getSetNumber(),
                            "Para ganar el set con tiebreak, el jugador 2 debe haber ganado 7 juegos"
                    );
                }

                if (set.getPlayer1Games() != 6) {
                    throwInvalidScoreException(
                            "Juegos inválidos para el set " + set.getSetNumber(),
                            "Si el jugador 2 ganó el set con tiebreak, el jugador 1 debe haber ganado 6 juegos"
                    );
                }

                if (set.getPlayer2TiebreakGames() <= set.getPlayer1TiebreakGames()) {
                    throwInvalidScoreException(
                            "Juegos de tiebreak inválidos para el set " + set.getSetNumber(),
                            "El jugador 2 debe haber ganado más juegos de tiebreak que el jugador 1"
                    );
                }

                if (set.getPlayer2TiebreakGames() < 7) {
                    throwInvalidScoreException(
                            "Juegos de tiebreak inválidos para el set " + set.getSetNumber(),
                            "El jugador 2 debe haber ganado al menos 7 juegos de tiebreak"
                    );
                }

                if (Math.abs(set.getPlayer1TiebreakGames() - set.getPlayer2TiebreakGames()) < 2) {
                    throwInvalidScoreException(
                            "Juegos de tiebreak inválidos para el set " + set.getSetNumber(),
                            "La diferencia de juegos de tiebreak entre los jugadores debe ser al menos de 2"
                    );
                }

                if ((Math.abs(set.getPlayer1TiebreakGames() - set.getPlayer2TiebreakGames()) > 2) && (set.getPlayer2TiebreakGames() > 7)) {
                    throwInvalidScoreException(
                            "Juegos de tiebreak inválidos para el set " + set.getSetNumber(),
                            "El jugador 2 no puede haber ganado más de 7 juegos de tiebreak si la diferencia es mayor a 2"
                    );
                }
            }
        } else {
            if (Math.max(set.getPlayer1Games(), set.getPlayer2Games()) < 6) {
                throwInvalidScoreException(
                        "Juegos inválidos para el set " + set.getSetNumber(),
                        "Al menos un jugador debe haber ganado 6 juegos"
                );
            }

            if (Math.max(set.getPlayer1Games(), set.getPlayer2Games()) > 7) {
                throwInvalidScoreException(
                        "Juegos inválidos para el set " + set.getSetNumber(),
                        "Ningún jugador puede haber ganado más de 7 juegos"
                );
            }

            if ((set.getPlayer1Games() == 7 && set.getPlayer2Games() == 6) || (set.getPlayer1Games() == 6 && set.getPlayer2Games() == 7)) {
                throwInvalidScoreException(
                        "Juegos inválidos para el set " + set.getSetNumber(),
                        "Para que estos juegos sean válidos, se debe jugar un tiebreak"
                );
            }

            if (p1Won) {
                if (set.getPlayer1Games() == 7 && set.getPlayer2Games() < 5) {
                    throwInvalidScoreException(
                            "Juegos inválidos para el set " + set.getSetNumber(),
                            "El jugador 1 no puede haber ganado 7 juegos si el jugador 2 ganó menos de 5 juegos"
                    );
                }
            } else {
                if (set.getPlayer2Games() == 7 && set.getPlayer1Games() < 5) {
                    throwInvalidScoreException(
                            "Juegos inválidos para el set " + set.getSetNumber(),
                            "El jugador 2 no puede haber ganado 7 juegos si el jugador 1 ganó menos de 5 juegos"
                    );
                }
            }


            if (Math.abs(set.getPlayer1Games() - set.getPlayer2Games()) < 2) {
                throwInvalidScoreException(
                        "Juegos inválidos para el set " + set.getSetNumber(),
                        "La diferencia de juegos entre los jugadores debe ser al menos de 2 en caso de no haber tiebreak"
                );
            }
        }
    }

    private void throwInvalidScoreException(String title, String description) {
        throw new CustomException(
                ErrorCode.INVALID_SCORE,
                title,
                description
        );
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

        if (maxSetsWon > 3) {
            throw new CustomException(
                    ErrorCode.INVALID_MATCH_STATUS,
                    "Sets inválidos",
                    "El partido debería terminar si un jugador ya ha ganado 3 sets"
            );
        }
    }

    private void validatePreviousRoundsCompleted(Match match) {
        TournamentRound currentRound = match.getRound();
        Long tournamentId = match.getTournament().getId();

        for (TournamentRound round: TournamentRound.values()) {
            if (round.getRoundNumber() > currentRound.getRoundNumber()) {
                if (matchRepository.existsByRoundAndTournamentIdAndCompletedFalse(round, tournamentId)) {
                    throw new CustomException(
                            ErrorCode.INVALID_MATCH_STATUS,
                            "Rondas anteriores incompletas",
                            "No se pueden completar partidos de rondas futuras si las rondas anteriores no están completas"
                    );
                }
            }
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
