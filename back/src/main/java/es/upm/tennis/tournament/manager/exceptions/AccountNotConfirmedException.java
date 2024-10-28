package es.upm.tennis.tournament.manager.exceptions;

public class AccountNotConfirmedException extends RuntimeException {
    public AccountNotConfirmedException(String message) {
        super(message);
    }
}
