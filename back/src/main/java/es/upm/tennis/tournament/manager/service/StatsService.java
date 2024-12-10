package es.upm.tennis.tournament.manager.service;

import es.upm.tennis.tournament.manager.exceptions.CustomException;
import es.upm.tennis.tournament.manager.exceptions.ErrorCode;
import es.upm.tennis.tournament.manager.model.*;
import es.upm.tennis.tournament.manager.repo.PlayerStatsRepository;
import es.upm.tennis.tournament.manager.repo.TournamentParticipationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class StatsService {

    private final TournamentParticipationRepository tournamentParticipationRepository;
    private final PlayerStatsRepository playerStatsRepository;

    public  StatsService (
            TournamentParticipationRepository tournamentParticipationRepository,
            PlayerStatsRepository playerStatsRepository
    ) {
        this.tournamentParticipationRepository = tournamentParticipationRepository;
        this.playerStatsRepository = playerStatsRepository;
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
}
