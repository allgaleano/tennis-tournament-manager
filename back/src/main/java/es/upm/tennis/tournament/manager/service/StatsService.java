package es.upm.tennis.tournament.manager.service;

import es.upm.tennis.tournament.manager.DTO.PlayerStatsDTO;
import es.upm.tennis.tournament.manager.DTO.TournamentParticipationDTO;
import es.upm.tennis.tournament.manager.exceptions.CustomException;
import es.upm.tennis.tournament.manager.exceptions.ErrorCode;
import es.upm.tennis.tournament.manager.model.*;
import es.upm.tennis.tournament.manager.repo.PlayerStatsRepository;
import es.upm.tennis.tournament.manager.repo.TournamentParticipationRepository;
import es.upm.tennis.tournament.manager.repo.TournamentRepository;
import es.upm.tennis.tournament.manager.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
public class StatsService {

    private final TournamentParticipationRepository tournamentParticipationRepository;
    private final PlayerStatsRepository playerStatsRepository;
    private final PermissionChecker permissionChecker;
    private final TournamentRepository tournamentRepository;
    private final UserRepository userRepository;

    public  StatsService (
            TournamentParticipationRepository tournamentParticipationRepository,
            PlayerStatsRepository playerStatsRepository,
            PermissionChecker permissionChecker,
            TournamentRepository tournamentRepository,
            UserRepository userRepository
    ) {
        this.tournamentParticipationRepository = tournamentParticipationRepository;
        this.playerStatsRepository = playerStatsRepository;
        this.permissionChecker = permissionChecker;
        this.tournamentRepository = tournamentRepository;
        this.userRepository = userRepository;
    }

    public void update(Match match) {
        log.info("Updating stats for match {}", match.getId());

        TournamentParticipation player1Participation = getParticipation(match.getPlayer1(), match.getTournament());
        TournamentParticipation player2Participation = getParticipation(match.getPlayer2(), match.getTournament());

        updateMatchStats(match, player1Participation, player2Participation);

        if (match.getRound() == TournamentRound.FINAL) {
            PlayerStats winnerStats = match.getWinner().equals(match.getPlayer1()) ? player1Participation.getPlayerStats() : player2Participation.getPlayerStats();
            winnerStats.incrementTournamentsWon();
            Tournament tournament = match.getTournament();
            tournament.distributeTournamentPoints();
            tournament.setStatus(TournamentStatus.FINISHED);
        }
    }

    private TournamentParticipation getParticipation(User player, Tournament tournament) {
        PlayerStats playerStats = playerStatsRepository.findByPlayer(player)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.PLAYER_STATS_NOT_FOUND,
                        "Estadísticas de jugador no encontradas",
                        "No se encontraron estadísticas para el jugador " + player.getId()
                ));

        return tournamentParticipationRepository.findByTournamentAndPlayerStats(tournament, playerStats)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.TOURNAMENT_STATS_NOT_FOUND,
                        "Participación en torneo no encontrada",
                        "No se encontró la participación del jugador " + player.getId() + " en el torneo " + tournament.getId()
        ));
    }

    private void updateMatchStats(
            Match match,
            TournamentParticipation p1Participation,
            TournamentParticipation p2Participation
    ) {
        p1Participation.incrementMatchesPlayed();
        p2Participation.incrementMatchesPlayed();

        if (match.getWinner().equals(match.getPlayer1())) {
            p1Participation.incrementMatchesWon();
            p2Participation.incrementMatchesLost();
        } else {
            p1Participation.incrementMatchesLost();
            p2Participation.incrementMatchesWon();
        }

        for (Set set: match.getSets()) {
            updateSetStats(set, p1Participation, p2Participation);
        }
    }

    private void updateSetStats(Set set, TournamentParticipation p1Participation, TournamentParticipation p2Participation) {
        p1Participation.incrementGamesWon(set.getPlayer1Games());
        p1Participation.incrementGamesLost(set.getPlayer2Games());
        p2Participation.incrementGamesWon(set.getPlayer2Games());
        p2Participation.incrementGamesLost(set.getPlayer1Games());

        boolean p1Won = set.getSetWinner().equals(set.getMatch().getPlayer1());

        if (p1Won) {
            p1Participation.incrementSetsWon(1);
            p2Participation.incrementSetsLost(1);
        } else {
            p1Participation.incrementSetsLost(1);
            p2Participation.incrementSetsWon(1);
        }

        if (set.isTiebreak()) {
            p1Participation.incrementTiebreakGamesWon(set.getPlayer1TiebreakGames());
            p1Participation.incrementTiebreakGamesLost(set.getPlayer2TiebreakGames());
            p2Participation.incrementTiebreakGamesWon(set.getPlayer2TiebreakGames());
            p2Participation.incrementTiebreakGamesLost(set.getPlayer1TiebreakGames());
        }
    }


    public List<TournamentParticipationDTO> getTournamentStats(Long tournamentId, String sessionId) {

        permissionChecker.validateSession(sessionId);

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.TOURNAMENT_NOT_FOUND,
                        "Torneo no encontrado"
                ));

        List<TournamentParticipation> participants = tournamentParticipationRepository.findAllByTournamentOrderByPointsDesc(tournament);

        return participants.stream()
                .map(TournamentParticipationDTO::fromEntity)
                .toList();
    }

    public PlayerStatsDTO getPlayerStats(Long playerId, String sessionId) {
        User player = userRepository.findById(playerId)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.USER_NOT_FOUND,
                        "Usuario no encontrado"
                ));
        permissionChecker.validateUserPermission(player, sessionId);

        PlayerStats playerStats = playerStatsRepository.findByPlayer(player)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.PLAYER_STATS_NOT_FOUND,
                        "Estadísticas de jugador no encontradas",
                        "No se encontraron estadísticas para el jugador " + player.getId()
                ));

        return PlayerStatsDTO.fromEntity(playerStats);
    }

    public Page<PlayerStatsDTO> getAllPlayersStats(String sessionId, Pageable pageable) {
        permissionChecker.validateSession(sessionId);
        return playerStatsRepository.findAllOrderedByStats(pageable).map(PlayerStatsDTO::fromEntity);
    }
}
