package es.upm.tennis.tournament.manager.service;

import es.upm.tennis.tournament.manager.DTO.FirstRoundMatchmakingResult;
import es.upm.tennis.tournament.manager.exceptions.CustomException;
import es.upm.tennis.tournament.manager.exceptions.ErrorCode;
import es.upm.tennis.tournament.manager.model.*;
import es.upm.tennis.tournament.manager.repo.MatchRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class MatchmakingService {

    private final MatchRepository matchRepository;

    public MatchmakingService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    public FirstRoundMatchmakingResult createFirstRoundMatches(Tournament tournament, List<PlayerTournament> players) {
        List<User> shuffledPlayers = new ArrayList<>(players.stream()
                .map(PlayerTournament::getPlayer)
                .toList());
        Collections.shuffle(shuffledPlayers);

        TournamentRound firstRound = determineFirstRound(shuffledPlayers.size());

        boolean hasPlayerWithBye = shuffledPlayers.size() % 2 != 0;
        int matchCount = hasPlayerWithBye ? (shuffledPlayers.size() - 1) / 2 : shuffledPlayers.size() / 2;

        for (int i = 0; i < matchCount; i++) {
            Match match = new Match();
            match.setTournament(tournament);
            match.setRound(firstRound);
            match.setPlayer1(shuffledPlayers.get(i * 2));
            match.setPlayer2(shuffledPlayers.get(i * 2 + 1));
            matchRepository.save(match);
        }

        if (hasPlayerWithBye) {
            User playerWithBye = shuffledPlayers.get(shuffledPlayers.size() - 1);
            log.info("Player {} advances directly to round {}", playerWithBye.getId(), firstRound.getNextRound().getValue());
            return new FirstRoundMatchmakingResult(playerWithBye, firstRound.getNextRound());
        }

        return new FirstRoundMatchmakingResult(null, firstRound.getNextRound());
    }

    private TournamentRound determineFirstRound(int playerCount) {
        if (playerCount <= 4) return TournamentRound.SEMIFINAL;
        if (playerCount <= 8) return TournamentRound.QUARTER_FINALS;
        return TournamentRound.ROUND_16;
    }
}
