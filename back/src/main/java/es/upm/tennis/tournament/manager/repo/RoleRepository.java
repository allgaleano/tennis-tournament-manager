package es.upm.tennis.tournament.manager.repo;

import es.upm.tennis.tournament.manager.model.ERole;
import es.upm.tennis.tournament.manager.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByType(ERole type);
}
