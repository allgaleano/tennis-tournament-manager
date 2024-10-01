package es.upm.tennis.tournament.manager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

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
    private Date expirationDate;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    public ConfirmationCode(User user) {
        this.user = user;
        this.code = generateConfirmationCode();
        this.expirationDate = calculateExpirationDate();
    }

    private String generateConfirmationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    private Date calculateExpirationDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, 30);
        return new Date(calendar.getTime().getTime());
    }
}
