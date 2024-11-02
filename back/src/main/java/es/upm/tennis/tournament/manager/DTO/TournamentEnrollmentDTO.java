package es.upm.tennis.tournament.manager.DTO;

import es.upm.tennis.tournament.manager.model.EnrollmentStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TournamentEnrollmentDTO {
    private Long id;
    private UserEnrolledDTO player;
    private EnrollmentStatus status;
}
