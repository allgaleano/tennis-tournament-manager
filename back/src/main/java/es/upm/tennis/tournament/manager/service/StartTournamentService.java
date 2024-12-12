package es.upm.tennis.tournament.manager.service;

import es.upm.tennis.tournament.manager.exceptions.CustomException;
import es.upm.tennis.tournament.manager.exceptions.ErrorCode;
import es.upm.tennis.tournament.manager.model.*;
import es.upm.tennis.tournament.manager.repo.PlayerStatsRepository;
import es.upm.tennis.tournament.manager.repo.TournamentEnrollmentRepository;
import es.upm.tennis.tournament.manager.repo.TournamentParticipationRepository;
import es.upm.tennis.tournament.manager.repo.TournamentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class StartTournamentService {

    private final PermissionChecker permissionChecker;
    private final TournamentRepository tournamentRepository;
    private final TournamentEnrollmentRepository tournamentEnrollmentRepository;
    private final PlayerStatsRepository playerStatsRepository;
    private final TournamentParticipationRepository tournamentParticipationRepository;
    private final MatchmakingService matchmakingService;

    public StartTournamentService(
            PermissionChecker permissionChecker,
            TournamentRepository tournamentRepository,
            TournamentEnrollmentRepository tournamentEnrollmentRepository,
            PlayerStatsRepository playerStatsRepository,
            TournamentParticipationRepository tournamentParticipationRepository,
            MatchmakingService matchmakingService
    ) {
        this.permissionChecker = permissionChecker;
        this.tournamentRepository = tournamentRepository;
        this.tournamentEnrollmentRepository = tournamentEnrollmentRepository;
        this.playerStatsRepository = playerStatsRepository;
        this.tournamentParticipationRepository = tournamentParticipationRepository;
        this.matchmakingService = matchmakingService;
    }

    public void startTournament(Long tournamentId, String sessionId) {
        log.info("Starting tournament {}", tournamentId);

        permissionChecker.validateAdminPermission(sessionId);

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.TOURNAMENT_NOT_FOUND,
                        "Torneo no encontrado"
                ));

        validateTournamentStatus(tournament);
        validatePlayerCount(tournament, tournamentId);
        declinePendingEnrollments(tournamentId);
        createParticipantsAndMatches(tournament);

        tournament.setStatus(TournamentStatus.IN_PROGRESS);
    }

    private void validateTournamentStatus(Tournament tournament) {
        if (!tournament.getStatus().equals(TournamentStatus.ENROLLMENT_CLOSED)) {
            throw new CustomException(
                    ErrorCode.INVALID_TOURNAMENT_STATUS,
                    "Estado incorrecto del torneo",
                    "Las inscripciones deben estar cerradas para poder iniciar el torneo"
            );
        }
    }

    private void validatePlayerCount(Tournament tournament, Long tournamentId) {
        long selectedCount = tournamentEnrollmentRepository.countByTournamentIdAndStatus(tournamentId, EnrollmentStatus.SELECTED);

        if (selectedCount < tournament.getMinPlayers()) {
            throw new CustomException(
                    ErrorCode.MIN_PLAYERS_NOT_REACHED,
                    "Número insuficiente de jugadores seleccionados",
                    String.format(
                            "Se necesitan al menos %d jugadores seleccionados para iniciar el torneo",
                            tournament.getMinPlayers()
                    )
            );
        }

        if (selectedCount > tournament.getMaxPlayers()) {
            throw new CustomException(
                    ErrorCode.MAX_PLAYERS_EXCEEDED,
                    "Número excesivo de jugadores seleccionados",
                    String.format(
                            "Se han seleccionado %d jugadores, pero el máximo permitido es %d",
                            selectedCount,
                            tournament.getMaxPlayers()
                    )
            );
        }
    }

    private void declinePendingEnrollments(Long tournamentId) {
        tournamentEnrollmentRepository.
                findByTournamentIdAndStatus(tournamentId, EnrollmentStatus.PENDING)
                .forEach(enrollment -> enrollment.setStatus(EnrollmentStatus.DECLINED));
    }

    private void createParticipantsAndMatches(Tournament tournament) {
        List<TournamentParticipation> participants = tournamentEnrollmentRepository
                .findByTournamentIdAndStatus(tournament.getId(), EnrollmentStatus.SELECTED)
                .stream()
                .map(enrollment -> getOrCreateTournamentParticipation(enrollment, tournament))
                .toList();

        int numParticipants = participants.size();
        if (!isValidNumberOfParticipants(numParticipants)) {
            throw new CustomException(
                    ErrorCode.INVALID_PARTICIPANT_COUNT,
                    "Número de participantes inválido",
                    "El número de participantes debe ser una potencia de 2, permitiendo únicamente un jugador BYE"
            );
        }

        tournamentParticipationRepository.saveAll(participants);
        matchmakingService.createRoundMatches(tournament, participants);
    }

    private boolean isValidNumberOfParticipants(int numParticipants) {
        return numParticipants == 7 || numParticipants == 8 || numParticipants == 15 || numParticipants == 16 || numParticipants == 4;
    }

    private TournamentParticipation getOrCreateTournamentParticipation (TournamentEnrollment enrollment, Tournament tournament) {
        PlayerStats playerStats = playerStatsRepository
                .findByPlayer(enrollment.getPlayer())
                .orElseGet(() -> createAndSavePlayerStats(enrollment.getPlayer()));

        playerStats.incrementTournamentsPlayed();
        playerStatsRepository.save(playerStats);

        return tournamentParticipationRepository
                .findByTournamentAndPlayerStats(tournament, playerStats)
                .orElseGet(() -> createTournamentParticipation(tournament, playerStats));
    }

    private PlayerStats createAndSavePlayerStats (User player) {
        PlayerStats playerStats = new PlayerStats();
        playerStats.setPlayer(player);
        return playerStats;
    }

    private TournamentParticipation createTournamentParticipation(Tournament tournament, PlayerStats playerStats) {
        TournamentParticipation participation = new TournamentParticipation();
        participation.setTournament(tournament);
        participation.setPlayerStats(playerStats);
        return participation;
    }
}
