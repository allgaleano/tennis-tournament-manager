package es.upm.tennis.tournament.manager.repo;

import es.upm.tennis.tournament.manager.model.ConfirmationCode;
import es.upm.tennis.tournament.manager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfirmationCodeRepository extends JpaRepository<ConfirmationCode, Long> {
    ConfirmationCode findByCode(String code);

    ConfirmationCode findByUser(User user);
}
