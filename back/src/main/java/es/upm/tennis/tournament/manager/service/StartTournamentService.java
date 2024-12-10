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

        if (!tournament.getStatus().equals(TournamentStatus.ENROLLMENT_CLOSED)) {
            throw new CustomException(
                    ErrorCode.INVALID_TOURNAMENT_STATUS,
                    "Estado incorrecto del torneo",
                    "Las inscripciones deben estar cerradas para poder iniciar el torneo"
            );
        }

        long selectedPlayersCount = tournamentEnrollmentRepository.countByTournamentIdAndStatus(tournamentId, EnrollmentStatus.SELECTED);

        if (selectedPlayersCount < tournament.getMinPlayers()) {
            throw new CustomException(
                    ErrorCode.MIN_PLAYERS_NOT_REACHED,
                    "Número insuficiente de jugadores seleccionados",
                    String.format(
                            "Se necesitan al menos %d jugadores seleccionados para iniciar el torneo",
                            tournament.getMinPlayers()
                    )
            );
        } else if (selectedPlayersCount > tournament.getMaxPlayers()) {
            throw new CustomException(
                    ErrorCode.MAX_PLAYERS_EXCEEDED,
                    "Número excesivo de jugadores seleccionados",
                    String.format(
                            "Se han seleccionado %d jugadores, pero el máximo permitido es %d",
                            selectedPlayersCount,
                            tournament.getMaxPlayers()
                    )
            );
        }

        List<TournamentEnrollment> pendingEnrollments = tournamentEnrollmentRepository
                .findByTournamentIdAndStatus(tournamentId, EnrollmentStatus.PENDING);
        pendingEnrollments.forEach(enrollment -> enrollment.setStatus(EnrollmentStatus.DECLINED));
        tournamentEnrollmentRepository.saveAll(pendingEnrollments);

        List<TournamentEnrollment> selectedEnrollments = tournamentEnrollmentRepository.findByTournamentIdAndStatus(tournamentId, EnrollmentStatus.SELECTED);


        List<TournamentParticipation> tournamentParticipants = selectedEnrollments.stream()
                .map(enrollment -> getOrCreateTournamentParticipation(enrollment, tournament))
                .toList();

        tournamentParticipationRepository.saveAll(tournamentParticipants);

        matchmakingService.createRoundMatches(tournament, tournamentParticipants);

        tournament.setStatus(TournamentStatus.IN_PROGRESS);
        tournamentRepository.save(tournament);
    }

    private TournamentParticipation getOrCreateTournamentParticipation (TournamentEnrollment enrollment, Tournament tournament) {
        PlayerStats playerStats = playerStatsRepository.findByPlayer(enrollment.getPlayer())
                .orElseGet(() -> {
                    PlayerStats newPlayerStats = new PlayerStats();
                    newPlayerStats.setPlayer(enrollment.getPlayer());
                    return playerStatsRepository.save(newPlayerStats);
                });

        Optional<TournamentParticipation> existingParticipation =
                tournamentParticipationRepository.findByTournamentAndPlayerStats(tournament, playerStats);

        if (existingParticipation.isPresent()) {
            return existingParticipation.get();
        }

        TournamentParticipation tournamentParticipation = new TournamentParticipation();
        tournamentParticipation.setPlayerStats(playerStats);
        tournamentParticipation.setTournament(tournament);
        return tournamentParticipation;
    }
}
