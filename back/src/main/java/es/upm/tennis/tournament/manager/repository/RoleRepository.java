package es.upm.tennis.tournament.manager.repository;

import es.upm.tennis.tournament.manager.entity.ERole;
import es.upm.tennis.tournament.manager.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
