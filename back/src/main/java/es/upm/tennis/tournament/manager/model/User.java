package es.upm.tennis.tournament.manager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import javax.xml.stream.events.StartDocument;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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

    @OneToOne(targetEntity = Role.class, fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JoinColumn(nullable = false, name = "role")
    private Role role;
}
