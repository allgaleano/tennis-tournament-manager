package es.upm.tennis.tournament.manager.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDisplayDTO {
    private Long id;
    private String name;
    private String phoneNumber;
    private String createdAt;
    private String username;
    private String email;
    private boolean enabledAccount;
    private String role;
}
