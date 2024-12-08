package es.upm.tennis.tournament.manager.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // Authentication errors
    BAD_CREDENTIALS("AUTH_001", HttpStatus.UNAUTHORIZED),
    ACCOUNT_DISABLED("AUTH_002", HttpStatus.FORBIDDEN),
    ACCOUNT_NOT_CONFIRMED("AUTH_003", HttpStatus.FORBIDDEN),
    INVALID_TOKEN("AUTH_004", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED_ACTION("AUTH_005", HttpStatus.FORBIDDEN),
    CONFIRMATION_CONFLICT("AUTH_006", HttpStatus.CONFLICT),

    // User related errors
    USER_NOT_FOUND("USER_001", HttpStatus.NOT_FOUND),
    USERNAME_ALREADY_EXISTS("USER_002", HttpStatus.CONFLICT),
    EMAIL_ALREADY_EXISTS("USER_003", HttpStatus.CONFLICT),
    EMAIL_NOT_SENT("USER_004", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_ROLE("USER_005", HttpStatus.BAD_REQUEST),
    INVALID_ACCOUNT_STATUS("USER_006", HttpStatus.BAD_REQUEST),

    // Tournament related errors
    TOURNAMENT_NOT_FOUND("TOUR_001", HttpStatus.NOT_FOUND),
    MAX_PLAYERS_EXCEEDED("TOUR_002", HttpStatus.CONFLICT),
    MIN_PLAYERS_NOT_REACHED("TOUR_003", HttpStatus.CONFLICT),
    BAD_ENROLLMENT_STATUS("TOUR_004", HttpStatus.BAD_REQUEST),
    INVALID_TOURNAMENT_STATUS("TOUR_005", HttpStatus.BAD_REQUEST),
    INVALID_TOURNAMENT_ROUND("TOUR_006", HttpStatus.BAD_REQUEST),

    // Match related errors
    MATCH_NOT_FOUND("MATCH_001", HttpStatus.NOT_FOUND),
    INVALID_MATCH_STATUS("MATCH_002", HttpStatus.BAD_REQUEST),

    // Set related errors
    INVALID_SET_STATUS("SET_001", HttpStatus.BAD_REQUEST),
    INVALID_SCORE("SET_002", HttpStatus.BAD_REQUEST),

    // Generic errors
    INTERNAL_ERROR("GEN_001", HttpStatus.INTERNAL_SERVER_ERROR),
    BAD_REQUEST("GEN_002", HttpStatus.BAD_REQUEST);

    private final String code;
    private final HttpStatus httpStatus;

    ErrorCode(String code, HttpStatus httpStatus) {
        this.code = code;
        this.httpStatus = httpStatus;
    }
}
