package es.upm.tennis.tournament.manager.DTO;

import es.upm.tennis.tournament.manager.model.Tournament;
import es.upm.tennis.tournament.manager.model.TournamentStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class TournamentDTO {
    private Long id;
    private String name;
    private Instant registrationDeadline;
    private int maxPlayers;
    private TournamentStatus status;
    private long selectedPlayersCount;

    public static TournamentDTO fromTournament(Tournament tournament, long selectedPlayersCount) {
        TournamentDTO dto = new TournamentDTO();
        dto.setId(tournament.getId());
        dto.setName(tournament.getName());
        dto.setRegistrationDeadline(tournament.getRegistrationDeadline());
        dto.setMaxPlayers(tournament.getMaxPlayers());
        dto.setStatus(tournament.getStatus());
        dto.setSelectedPlayersCount(selectedPlayersCount);
        return dto;
    }
}
