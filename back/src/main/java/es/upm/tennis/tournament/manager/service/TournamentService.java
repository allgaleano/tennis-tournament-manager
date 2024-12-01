package es.upm.tennis.tournament.manager.service;

import es.upm.tennis.tournament.manager.DTO.PlayerIdsRequest;
import es.upm.tennis.tournament.manager.DTO.TournamentDTO;
import es.upm.tennis.tournament.manager.DTO.TournamentEnrollmentDTO;
import es.upm.tennis.tournament.manager.DTO.UserEnrolledDTO;
import es.upm.tennis.tournament.manager.exceptions.*;
import es.upm.tennis.tournament.manager.model.*;
import es.upm.tennis.tournament.manager.repo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class TournamentService {

    private static final Logger logger = LoggerFactory.getLogger(TournamentService.class);

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

    @Autowired
    TournamentEnrollmentRepository tournamentEnrollmentRepository;


    public Page<Tournament> getAllTournaments(Pageable pageable) {
        return tournamentRepository.findAll(pageable);
    }

    public void enrollPlayerToTournament(Long tournamentId, Long playerId, String sessionId) {
        logger.info("Enrolling player {} to tournament {}", playerId, tournamentId);
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
            UserEnrolledDTO userEnrolledDTO = new UserEnrolledDTO();
            userEnrolledDTO.setId(enrollment.getPlayer().getId());
            userEnrolledDTO.setName(enrollment.getPlayer().getName());
            userEnrolledDTO.setSurname(enrollment.getPlayer().getSurname());
            userEnrolledDTO.setUsername(enrollment.getPlayer().getUsername());
            userEnrolledDTO.setEmail(enrollment.getPlayer().getEmail());
            tournamentEnrollmentDTO.setId(enrollment.getId());
            tournamentEnrollmentDTO.setPlayer(userEnrolledDTO);
            tournamentEnrollmentDTO.setStatus(enrollment.getStatus());

            return tournamentEnrollmentDTO;
        });
    }

    public void unenrollPlayerFromTournament(Long tournamentId, Long playerId, String sessionId) {
        logger.info("Unenrolling player {} from tournament {}", playerId, tournamentId);
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
        logger.info("Closing enrollments for tournament {}", tournamentId);

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

    public void reopenEnrollments(Long tournamentId, String sessionId) {
        logger.info("Reopening enrollments for tournament {}", tournamentId);

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
}
