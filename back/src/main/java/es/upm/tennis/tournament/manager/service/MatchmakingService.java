package es.upm.tennis.tournament.manager.service;

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

    public void createRoundMatches(Tournament tournament, List<PlayerTournament> players) {
        List<User> shuffledPlayers = new ArrayList<>(players.stream()
                .map(PlayerTournament::getPlayer)
                .toList());
        Collections.shuffle(shuffledPlayers);

        TournamentRound firstRound = determineFirstRound(shuffledPlayers.size());

        boolean hasPlayerWithBye = shuffledPlayers.size() % 2 != 0;
        int matchCount = hasPlayerWithBye ? (shuffledPlayers.size() - 1) / 2 : shuffledPlayers.size() / 2;

        List<Match> firstRoundMatches = new ArrayList<>();
        for (int i = 0; i < matchCount; i++) {
            Match match = new Match();
            match.setTournament(tournament);
            match.setRound(firstRound);
            match.setPlayer1(shuffledPlayers.get(i * 2));
            match.setPlayer2(shuffledPlayers.get(i * 2 + 1));
            firstRoundMatches.add(match);
        }
        matchRepository.saveAll(firstRoundMatches);

        User playerWithBye = hasPlayerWithBye ? shuffledPlayers.getLast() : null;
        createNextRoundMatches(tournament, firstRound, firstRoundMatches.size(), playerWithBye);
    }

    private void createNextRoundMatches(Tournament tournament, TournamentRound currentRound,
                                        int currentRoundMatches, User playerWithBye) {
        if (currentRound == TournamentRound.FINAL) {
            return;
        }

        TournamentRound nextRound = currentRound.getNextRound();
        int nextRoundMatches = (currentRoundMatches + (playerWithBye != null ? 1 : 0)) / 2;


        List<Match> nextRoundMatchList = new ArrayList<>();

        // If there's a player with bye, create their match first
        if (playerWithBye != null) {
            Match byeMatch = new Match();
            byeMatch.setTournament(tournament);
            byeMatch.setRound(nextRound);
            byeMatch.setPlayer1(playerWithBye);
            byeMatch.setPlayer2(null); // Will be filled when opponent is determined
            nextRoundMatchList.add(byeMatch);
        }

        // Create remaining matches for the next round
        for (int i = 0; i < nextRoundMatches - (playerWithBye != null ? 1 : 0); i++) {
            Match match = new Match();
            match.setTournament(tournament);
            match.setRound(nextRound);
            match.setPlayer1(null); // Will be filled when players advance
            match.setPlayer2(null);
            nextRoundMatchList.add(match);
        }

        matchRepository.saveAll(nextRoundMatchList);

        createNextRoundMatches(tournament, nextRound, nextRoundMatchList.size(), null);
    }

    private TournamentRound determineFirstRound(int playerCount) {
        if (playerCount <= 4) return TournamentRound.SEMIFINAL;
        if (playerCount <= 8) return TournamentRound.QUARTER_FINALS;
        return TournamentRound.ROUND_16;
    }
}
