package es.upm.tennis.tournament.manager.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserEnrolledDTO {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String username;
}
