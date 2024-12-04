package es.upm.tennis.tournament.manager.DTO;

import es.upm.tennis.tournament.manager.model.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPublicDTO {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String username;

    public static UserPublicDTO fromEntity(User user) {
        if (user == null) {
            return null;
        }
        UserPublicDTO userPublicDTO = new UserPublicDTO();
        userPublicDTO.setId(user.getId());
        userPublicDTO.setName(user.getName());
        userPublicDTO.setSurname(user.getSurname());
        userPublicDTO.setEmail(user.getEmail());
        userPublicDTO.setUsername(user.getUsername());
        return userPublicDTO;
    }
}
