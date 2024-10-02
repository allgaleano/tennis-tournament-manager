package es.upm.tennis.tournament.manager.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Getter
@Service
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {
    private String code;
    private String password;
}
