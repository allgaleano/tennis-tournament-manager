package es.upm.tennis.tournament.manager.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private String username;
    private String email;
    private String name;
    private String surname;
    private String phonePrefix;
    private String phoneNumber;
    private String password;
}
