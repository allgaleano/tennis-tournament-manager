package es.upm.tennis.tournament.manager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Random;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ConfirmationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String code;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime expirationDate;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    public ConfirmationCode(User user, int validMinutes) {
        this.user = user;
        this.code = generateConfirmationCode();
        this.expirationDate = LocalDateTime.now().plusMinutes(validMinutes);
    }

    private String generateConfirmationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}
