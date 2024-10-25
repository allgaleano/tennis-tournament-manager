package es.upm.tennis.tournament.manager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    private String name;

    private String surname;

    @Column(length = 5)
    private String phonePrefix;

    @Column(length = 15)
    private String phoneNumber;

    @Column(nullable = false)
    private String password;

    private boolean isEnabled;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @OneToOne(targetEntity = Role.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "role")
    private Role role;
}
