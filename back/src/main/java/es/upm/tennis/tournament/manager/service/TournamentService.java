package es.upm.tennis.tournament.manager.service;

import es.upm.tennis.tournament.manager.DTO.PlayerIdsRequest;
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
        Tournament tournament = tournamentRepository.findById(tournamentId).orElseThrow();

        User user = userRepository.findById(playerId).orElseThrow();

        UserSession userSession = userSessionRepository.findBySessionId(sessionId);

        permissionChecker.validateUserPermission(user, userSession);

        if (!user.getRole().getType().name().equals("ADMIN") && !tournament.getStatus().name().equals("ENROLLMENT_OPEN")) {
            throw new IllegalStateException("Tournament is closed for enrollments");
        }

        if (!userSession.getUser().getRole().getType().name().equals("ADMIN") && tournament.getRegistrationDeadline() != null && tournament.getRegistrationDeadline().isBefore(Instant.now())) {
            throw new IllegalStateException("Tournament registration deadline has passed");
        }

        TournamentEnrollment tournamentEnrollment = new TournamentEnrollment();
        tournamentEnrollment.setPlayer(user);
        tournamentEnrollment.setTournament(tournament);

        if (tournamentEnrollmentRepository.existsByTournamentIdAndPlayerId(tournamentId, playerId)) {
            throw new PlayerAlreadyEnrolledException("Player already enrolled in the tournament");
        }

        tournamentEnrollmentRepository.save(tournamentEnrollment);
    }

    public Page<TournamentEnrollmentDTO> getTournamentEnrollments(Long tournamentId, Pageable pageable) {
        if (!tournamentRepository.existsById(tournamentId)) {
            throw new NoSuchElementException("Tournament not found");
        }
        Page<TournamentEnrollment> enrollments = tournamentEnrollmentRepository.findAllByTournamentId(tournamentId, pageable);
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
        Tournament tournament = tournamentRepository.findById(tournamentId).orElseThrow();

        User user = userRepository.findById(playerId).orElseThrow();

        UserSession userSession = userSessionRepository.findBySessionId(sessionId);

        permissionChecker.validateUserPermission(user, userSession);

        if (!userSession.getUser().getRole().getType().name().equals("ADMIN") && tournament.getRegistrationDeadline() != null && tournament.getRegistrationDeadline().isBefore(Instant.now())) {
            throw new IllegalStateException("Tournament registration deadline has passed");
        }
        Optional<TournamentEnrollment> tournamentEnrollment = tournamentEnrollmentRepository.findByTournamentIdAndPlayerId(tournamentId, playerId);
        if (tournamentEnrollment.isEmpty()) {
            throw new PlayerNotEnrolledException("Player is not enrolled in the tournament");
        }

        if (tournamentEnrollment.get().getStatus().equals(EnrollmentStatus.SELECTED)) {
            throw new PlayerAlreadyAcceptedException("Player is already accepted in the tournament");
        }

        tournamentEnrollmentRepository.delete(tournamentEnrollment.get());
    }

    public Tournament getTournament(Long tournamentId) {
        return tournamentRepository.findById(tournamentId).orElseThrow();
    }

    public boolean isPlayerEnrolled(Long tournamentId, Long playerId) {
        return tournamentEnrollmentRepository.existsByTournamentIdAndPlayerId(tournamentId, playerId);
    }

    public void selectPlayer(Long tournamentId, PlayerIdsRequest playerIds, String sessionId) {
        UserSession userSession = userSessionRepository.findBySessionId(sessionId);
        if (userSession == null || userSession.getExpirationDate().isBefore(Instant.now())) {
            throw new InvalidCodeException("Invalid or expired session");
        }

        if (!userSession.getUser().getRole().getType().name().equals("ADMIN")) {
            throw new UnauthorizedUserAction("Only administrators can select players");
        }

        Tournament tournament = tournamentRepository.findById(tournamentId).orElseThrow();

        long currentSelectedCount = tournamentEnrollmentRepository
                .countByTournamentIdAndStatus(tournamentId, EnrollmentStatus.SELECTED);
        if (currentSelectedCount + playerIds.getPlayerIds().size() > tournament.getMaxPlayers()) {
            throw new IllegalStateException(
                    String.format("Cannot select %d players. Only %d spots remaining (Maximum: %d, Current: %d)",
                            playerIds.getPlayerIds().size(),
                            tournament.getMaxPlayers() - currentSelectedCount,
                            tournament.getMaxPlayers(),
                            currentSelectedCount)
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
                errors.add("Player " + playerId + " is not enrolled in the tournament");
            } else if (enrollment.getStatus().equals(EnrollmentStatus.SELECTED)) {
                errors.add("Player " + playerId + " is already selected");
            }
        }

        if (!errors.isEmpty()) {
            throw new BadEnrollmentStatusException(String.join(", ", errors));
        }

        enrollments.forEach(enrollment -> enrollment.setStatus(EnrollmentStatus.SELECTED));
        tournamentEnrollmentRepository.saveAll(enrollments);
    }

    public void deselectPlayer(Long tournamentId, PlayerIdsRequest playerIds, String sessionId) {
        UserSession userSession = userSessionRepository.findBySessionId(sessionId);
        if (userSession == null || userSession.getExpirationDate().isBefore(Instant.now())) {
            throw new UnauthorizedUserAction("Invalid or expired session");
        }

        if (!userSession.getUser().getRole().getType().name().equals("ADMIN")) {
            throw new UnauthorizedUserAction("Only administrators can deselect players");
        }

        List<TournamentEnrollment> enrollments = tournamentEnrollmentRepository
                .findByTournamentIdAndPlayerIdIn(tournamentId, playerIds.getPlayerIds());

        Map<Long, TournamentEnrollment> enrollmentMap = enrollments.stream()
                .collect(Collectors.toMap(e -> e.getPlayer().getId(), e -> e));

        List<String> errors = new ArrayList<>();
        for (Long playerId : playerIds.getPlayerIds()) {
            TournamentEnrollment enrollment = enrollmentMap.get(playerId);
            if (enrollment == null) {
                errors.add("Player " + playerId + " is not enrolled in the tournament");
            } else if (!enrollment.getStatus().equals(EnrollmentStatus.SELECTED)) {
                errors.add("Player " + playerId + " is not currently selected");
            }
        }

        if (!errors.isEmpty()) {
            throw new BadEnrollmentStatusException(String.join(", ", errors));
        }

        enrollments.forEach(enrollment -> enrollment.setStatus(EnrollmentStatus.PENDING));
        tournamentEnrollmentRepository.saveAll(enrollments);
    }
}
