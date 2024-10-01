package es.upm.tennis.tournament.manager.service;

import es.upm.tennis.tournament.manager.model.ConfirmationCode;
import es.upm.tennis.tournament.manager.repo.ConfirmationCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfirmationCodeService {

    @Autowired
    private ConfirmationCodeRepository confirmationCodeRepository;

    public ConfirmationCode getConfirmationToken(String token) {
        return confirmationCodeRepository.findByCode(token);
    }
}
