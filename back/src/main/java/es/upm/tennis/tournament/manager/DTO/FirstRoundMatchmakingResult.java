package es.upm.tennis.tournament.manager.DTO;

import es.upm.tennis.tournament.manager.model.TournamentRound;
import es.upm.tennis.tournament.manager.model.User;

public record FirstRoundMatchmakingResult(User playerWithBye, TournamentRound nextRound) {}
