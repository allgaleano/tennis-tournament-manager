package es.upm.tennis.tournament.manager.service;

import es.upm.tennis.tournament.manager.DTO.*;
import es.upm.tennis.tournament.manager.exceptions.*;
import es.upm.tennis.tournament.manager.model.*;
import es.upm.tennis.tournament.manager.repo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class TournamentService {

    private final TournamentRepository tournamentRepository;
    private final PermissionChecker permissionChecker;
    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    private final PlayerTournamentRepository playerTournamentRepository;
    private final TournamentEnrollmentRepository tournamentEnrollmentRepository;
    private final MatchRepository matchRepository;
    private final MatchmakingService matchmakingService;

    public TournamentService(
            TournamentRepository tournamentRepository,
            PermissionChecker permissionChecker,
            UserRepository userRepository,
            UserSessionRepository userSessionRepository,
            PlayerTournamentRepository playerTournamentRepository,
            TournamentEnrollmentRepository tournamentEnrollmentRepository,
            MatchRepository matchRepository,
            MatchmakingService matchmakingService) {
        this.tournamentRepository = tournamentRepository;
        this.permissionChecker = permissionChecker;
        this.userRepository = userRepository;
        this.userSessionRepository = userSessionRepository;
        this.playerTournamentRepository = playerTournamentRepository;
        this.tournamentEnrollmentRepository = tournamentEnrollmentRepository;
        this.matchRepository = matchRepository;
        this.matchmakingService = matchmakingService;
    }


    public Page<Tournament> getAllTournaments(Pageable pageable) {
        return tournamentRepository.findAllByOrderByRegistrationDeadlineAsc(pageable);
    }

    public void enrollPlayerToTournament(Long tournamentId, Long playerId, String sessionId) {
        log.info("Enrolling player {} to tournament {}", playerId, tournamentId);
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(
                        () -> new CustomException(
                                ErrorCode.TOURNAMENT_NOT_FOUND,
                                "Torneo no encontrado"
                        )
                );

        User user = userRepository.findById(playerId)
                .orElseThrow(
                        () -> new CustomException(
                                ErrorCode.USER_NOT_FOUND,
                                "Usuario no encontrado"
                        )
                );

        UserSession userSession = userSessionRepository.findBySessionId(sessionId);

        permissionChecker.validateUserPermission(user, userSession);

        boolean isAdmin = user.getRole().getType().name().equals("ADMIN");

        if (!isAdmin && !tournament.getStatus().name().equals("ENROLLMENT_OPEN")) {
            throw new CustomException(
                    ErrorCode.INVALID_TOURNAMENT_STATUS,
                    "Inscripciones cerradas"
            );
        }

        if (!isAdmin && tournament.getRegistrationDeadline() != null && tournament.getRegistrationDeadline().isBefore(Instant.now())) {
            throw new CustomException(
                    ErrorCode.INVALID_TOURNAMENT_STATUS,
                    "La fecha límite de inscripción ha pasado"
            );
        }

        TournamentEnrollment tournamentEnrollment = new TournamentEnrollment();
        tournamentEnrollment.setPlayer(user);
        tournamentEnrollment.setTournament(tournament);

        if (tournamentEnrollmentRepository.existsByTournamentIdAndPlayerId(tournamentId, playerId)) {
            throw new CustomException(
                    ErrorCode.BAD_ENROLLMENT_STATUS,
                    "Jugador ya inscrito en el torneo"
            );
        }

        tournamentEnrollmentRepository.save(tournamentEnrollment);
    }

    public Page<TournamentEnrollmentDTO> getTournamentEnrollments(Long tournamentId, Pageable pageable) {
        if (!tournamentRepository.existsById(tournamentId)) {
            throw new CustomException(
                    ErrorCode.TOURNAMENT_NOT_FOUND,
                    "Torneo no encontrado"
            );
        }
        Page<TournamentEnrollment> enrollments = tournamentEnrollmentRepository
                .findAllByTournamentIdOrderByCustomStatus(tournamentId, pageable);

        return enrollments.map(enrollment -> {
            TournamentEnrollmentDTO tournamentEnrollmentDTO = new TournamentEnrollmentDTO();
            UserPublicDTO userPublicDTO = new UserPublicDTO();
            userPublicDTO.setId(enrollment.getPlayer().getId());
            userPublicDTO.setName(enrollment.getPlayer().getName());
            userPublicDTO.setSurname(enrollment.getPlayer().getSurname());
            userPublicDTO.setUsername(enrollment.getPlayer().getUsername());
            userPublicDTO.setEmail(enrollment.getPlayer().getEmail());
            tournamentEnrollmentDTO.setId(enrollment.getId());
            tournamentEnrollmentDTO.setPlayer(userPublicDTO);
            tournamentEnrollmentDTO.setStatus(enrollment.getStatus());

            return tournamentEnrollmentDTO;
        });
    }

    public void unenrollPlayerFromTournament(Long tournamentId, Long playerId, String sessionId) {
        log.info("Unenrolling player {} from tournament {}", playerId, tournamentId);
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(
                        () -> new CustomException(
                                ErrorCode.TOURNAMENT_NOT_FOUND,
                                "Torneo no encontrado"
                        )
                );

        User user = userRepository.findById(playerId)
                .orElseThrow(
                        () -> new CustomException(
                                ErrorCode.USER_NOT_FOUND,
                                "Usuario no encontrado"
                        )
                );

        UserSession userSession = userSessionRepository.findBySessionId(sessionId);

        permissionChecker.validateUserPermission(user, userSession);

        boolean isAdmin = user.getRole().getType().name().equals("ADMIN");

        if (
                !isAdmin
                && tournament.getRegistrationDeadline() != null
                && tournament.getRegistrationDeadline().isBefore(Instant.now())
        ) {
            throw new CustomException(
                    ErrorCode.INVALID_TOURNAMENT_STATUS,
                    "La fecha límite de inscripción ha pasado"
            );
        }

        Optional<TournamentEnrollment> tournamentEnrollment = tournamentEnrollmentRepository.findByTournamentIdAndPlayerId(tournamentId, playerId);
        if (tournamentEnrollment.isEmpty()) {
            throw new CustomException(
                    ErrorCode.BAD_ENROLLMENT_STATUS,
                    "Jugador no inscrito en el torneo"
            );
        }

        if (tournamentEnrollment.get().getStatus().equals(EnrollmentStatus.SELECTED)) {
            throw new CustomException(
                    ErrorCode.BAD_ENROLLMENT_STATUS,
                    "Jugador ya aceptado en el torneo"
            );
        }

        tournamentEnrollmentRepository.delete(tournamentEnrollment.get());
    }

    public TournamentDTO getTournament(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.TOURNAMENT_NOT_FOUND,
                        "Torneo no encontrado"
                ));

        long selectedPlayersCount = tournamentEnrollmentRepository.countByTournamentIdAndStatus(tournamentId, EnrollmentStatus.SELECTED);

        return TournamentDTO.fromTournament(tournament, selectedPlayersCount);
    }

    public boolean isPlayerEnrolled(Long tournamentId, Long playerId) {
        return tournamentEnrollmentRepository.existsByTournamentIdAndPlayerId(tournamentId, playerId);
    }

    public void selectPlayer(Long tournamentId, PlayerIdsRequest playerIds, String sessionId) {
        permissionChecker.validateAdminPermission(userSessionRepository.findBySessionId(sessionId));

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.TOURNAMENT_NOT_FOUND,
                        "Torneo no encontrado"
                ));

        if (!tournament.getStatus().equals(TournamentStatus.ENROLLMENT_CLOSED)) {
            throw new CustomException(
                    ErrorCode.INVALID_TOURNAMENT_STATUS,
                    "Las inscripciones deben estar cerradas"
            );
        }

        long currentSelectedCount = tournamentEnrollmentRepository
                .countByTournamentIdAndStatus(tournamentId, EnrollmentStatus.SELECTED);

        if (currentSelectedCount + playerIds.getPlayerIds().size() > tournament.getMaxPlayers()) {
            throw new CustomException(
                    ErrorCode.MAX_PLAYERS_EXCEEDED,
                    "No se pueden seleccionar jugadores",
                    String.format(
                            "Solo quedan %d plazas disponibles y se han seleccionado %d jugadores",
                            tournament.getMaxPlayers() - currentSelectedCount,
                            playerIds.getPlayerIds().size()
                    )
            );
        }

        List<TournamentEnrollment> enrollments = tournamentEnrollmentRepository
                .findByTournamentIdAndPlayerIdIn(tournamentId, playerIds.getPlayerIds());

        Map<Long, TournamentEnrollment> enrollmentMap = enrollments.stream()
                .collect(Collectors.toMap(e -> e.getPlayer().getId(), e -> e));

        List<String> errors = new ArrayList<>();
        for (Long playerId : playerIds.getPlayerIds()) {
            TournamentEnrollment enrollment = enrollmentMap.get(playerId);
            if (enrollment == null) {
                errors.add("Jugador "+ playerId + " no inscrito en el torneo");
            } else if (enrollment.getStatus().equals(EnrollmentStatus.SELECTED)) {
                errors.add(enrollment.getPlayer().getUsername() + " ya seleccionado");
            }
        }

        if (!errors.isEmpty()) {
            throw new CustomException(
                    ErrorCode.BAD_ENROLLMENT_STATUS,
                    "Error al seleccionar jugadores",
                    String.join(", ", errors)
            );
        }

        enrollments.forEach(enrollment -> enrollment.setStatus(EnrollmentStatus.SELECTED));
        tournamentEnrollmentRepository.saveAll(enrollments);
    }

    public void deselectPlayer(Long tournamentId, PlayerIdsRequest playerIds, String sessionId) {

        permissionChecker.validateAdminPermission(userSessionRepository.findBySessionId(sessionId));

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.TOURNAMENT_NOT_FOUND,
                        "Torneo no encontrado"
                ));

        if (!tournament.getStatus().equals(TournamentStatus.ENROLLMENT_CLOSED)) {
            throw new CustomException(
                    ErrorCode.INVALID_TOURNAMENT_STATUS,
                    "Las inscripciones deben estar cerradas"
            );
        }

        List<TournamentEnrollment> enrollments = tournamentEnrollmentRepository
                .findByTournamentIdAndPlayerIdIn(tournamentId, playerIds.getPlayerIds());

        if (enrollments.isEmpty()) {
            throw new CustomException(
                    ErrorCode.BAD_ENROLLMENT_STATUS,
                    "Jugadores no inscritos en el torneo"
            );
        }

        Map<Long, TournamentEnrollment> enrollmentMap = enrollments.stream()
                .collect(Collectors.toMap(e -> e.getPlayer().getId(), e -> e));

        List<String> errors = new ArrayList<>();
        for (Long playerId : playerIds.getPlayerIds()) {
            TournamentEnrollment enrollment = enrollmentMap.get(playerId);
            if (enrollment == null) {
                errors.add("Jugador " + playerId + " no inscrito en el torneo");
            } else if (!enrollment.getStatus().equals(EnrollmentStatus.SELECTED)) {
                errors.add(enrollment.getPlayer().getUsername() + " no seleccionado");
            }
        }

        if (!errors.isEmpty()) {
            throw new CustomException(
                    ErrorCode.BAD_ENROLLMENT_STATUS,
                    "Error al deseleccionar jugadores",
                    String.join(", ", errors)
            );
        }

        enrollments.forEach(enrollment -> enrollment.setStatus(EnrollmentStatus.PENDING));
        tournamentEnrollmentRepository.saveAll(enrollments);
    }

    public void closeEnrollments(Long tournamentId, String sessionId) {
        log.info("Closing enrollments for tournament {}", tournamentId);

        permissionChecker.validateAdminPermission(userSessionRepository.findBySessionId(sessionId));

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.TOURNAMENT_NOT_FOUND,
                        "Torneo no encontrado"
                ));

        if (!tournament.getStatus().equals(TournamentStatus.ENROLLMENT_OPEN)) {
            throw new CustomException(
                    ErrorCode.INVALID_TOURNAMENT_STATUS,
                    "Estado incorrecto del torneo",
                    "Las inscripciones deben estar previamente abiertas para poder cerrarlas"
            );
        }

        tournament.setStatus(TournamentStatus.ENROLLMENT_CLOSED);
        tournamentRepository.save(tournament);
    }

    public void openEnrollments(Long tournamentId, String sessionId) {
        log.info("Reopening enrollments for tournament {}", tournamentId);

        permissionChecker.validateAdminPermission(userSessionRepository.findBySessionId(sessionId));

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.TOURNAMENT_NOT_FOUND,
                        "Torneo no encontrado"
                ));

        if (!tournament.getStatus().equals(TournamentStatus.ENROLLMENT_CLOSED)) {
            throw new CustomException(
                    ErrorCode.INVALID_TOURNAMENT_STATUS,
                    "Estado incorrecto del torneo",
                    "Las inscripciones deben estar previamente cerradas para poder reabrirlas"
            );
        }

        tournament.setStatus(TournamentStatus.ENROLLMENT_OPEN);
        tournamentRepository.save(tournament);
    }

    public void startTournament(Long tournamentId, String sessionId) {
        log.info("Starting tournament {}", tournamentId);

        permissionChecker.validateAdminPermission(userSessionRepository.findBySessionId(sessionId));

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


        List<PlayerTournament> playerTournaments = selectedEnrollments.stream()
                .map(enrollment -> {
                    PlayerTournament playerTournament = new PlayerTournament();
                    playerTournament.setPlayer(enrollment.getPlayer());
                    playerTournament.setTournament(tournament);
                    return playerTournament;
                })
                .toList();

        playerTournamentRepository.saveAll(playerTournaments);

        FirstRoundMatchmakingResult result = matchmakingService.createFirstRoundMatches(tournament, playerTournaments);

        if (result.playerWithBye() != null) {
            Match nextRoundMatch = new Match();
            nextRoundMatch.setTournament(tournament);
            nextRoundMatch.setRound(result.nextRound());
            nextRoundMatch.setPlayer1(result.playerWithBye());
            matchRepository.save(nextRoundMatch);
        }

        tournament.setStatus(TournamentStatus.IN_PROGRESS);
        tournamentRepository.save(tournament);
    }

    public List<Match> getTournamentMatches(Long tournamentId, String sessionId) {
        log.info("Getting matches for tournament {}", tournamentId);

        permissionChecker.validateSession(userSessionRepository.findBySessionId(sessionId));

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

        return matchRepository.findByTournamentIdOrderByRoundDesc(tournamentId);
    }
}
