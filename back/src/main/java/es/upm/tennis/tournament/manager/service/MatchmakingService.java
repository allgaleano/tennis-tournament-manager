package es.upm.tennis.tournament.manager.service;

import es.upm.tennis.tournament.manager.model.*;
import es.upm.tennis.tournament.manager.repo.MatchRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@Transactional
public class MatchmakingService {

    private final MatchRepository matchRepository;

    public MatchmakingService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    public void createRoundMatches(Tournament tournament, List<TournamentParticipation> players) {
        List<User> shuffledPlayers = new ArrayList<>(players.stream()
                .map(participant -> participant.getPlayerStats().getPlayer())
                .toList());
        Collections.shuffle(shuffledPlayers);

        log.info("Creating matches for tournament {} with {} players", tournament.getId(), shuffledPlayers.size());

        TournamentRound firstRound = determineFirstRound(shuffledPlayers.size());
        boolean hasPlayerWithBye = shuffledPlayers.size() % 2 != 0;
        int firstRoundMatchCount = hasPlayerWithBye ? (shuffledPlayers.size() - 1) / 2 : shuffledPlayers.size() / 2;

        List<List<Match>> allRoundsMatches = new ArrayList<>();
        TournamentRound currentRound = firstRound;
        int currentRoundMatches = firstRoundMatchCount;

        while (currentRound != null) {
            List<Match> roundMatches = new ArrayList<>();
            for (int i = 0; i < currentRoundMatches; i++) {
                Match match = new Match();
                match.setTournament(tournament);
                match.setRound(currentRound);
                roundMatches.add(match);
            }
            allRoundsMatches.add(roundMatches);

            currentRoundMatches = (int) Math.ceil(currentRoundMatches / 2.0);
            currentRound = currentRound == TournamentRound.FINAL ? null : currentRound.getNextRound();
        }

        for (int roundIndex = 0; roundIndex < allRoundsMatches.size() - 1; roundIndex++) {
            List<Match> currentMatches = allRoundsMatches.get(roundIndex);
            List<Match> nextRoundMatches = allRoundsMatches.get(roundIndex + 1);

            for (int matchIndex = 0; matchIndex < currentMatches.size(); matchIndex++) {
                Match currentMatch = currentMatches.get(matchIndex);
                int nextMatchIndex = matchIndex / 2;
                currentMatch.setNextMatch(nextRoundMatches.get(nextMatchIndex));
            }
        }

        List<Match> firstRoundMatches = allRoundsMatches.getFirst();
        for (int i = 0; i < firstRoundMatchCount; i++) {
            Match match = firstRoundMatches.get(i);
            match.setPlayer1(shuffledPlayers.get(i * 2));
            if (i * 2 + 1 < shuffledPlayers.size()) {
                match.setPlayer2(shuffledPlayers.get(i * 2 + 1));
            }
        }

        if (hasPlayerWithBye) {
            User byePlayer = shuffledPlayers.getLast();
            List<Match> secondRoundMatches = allRoundsMatches.get(1);

            Match byeMatch = secondRoundMatches.getLast();
            byeMatch.setPlayer1(byePlayer);
        }

        for (List<Match> roundMatches : allRoundsMatches) {
            matchRepository.saveAll(roundMatches);
        }
    }

    private TournamentRound determineFirstRound(int playerCount) {
        if (playerCount <= 4) return TournamentRound.SEMIFINAL;
        if (playerCount <= 8) return TournamentRound.QUARTER_FINALS;
        return TournamentRound.ROUND_16;
    }
}
