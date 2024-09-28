package es.upm.tennis.tournament.manager.model;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String sessionId;
    private LocalDateTime expirationDate;
}