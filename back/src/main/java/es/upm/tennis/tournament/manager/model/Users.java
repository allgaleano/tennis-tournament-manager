package es.upm.tennis.tournament.manager.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class Users {
    @Id
    private int id;
    private String username;
    private String email;
    private String password;
}
