package es.upm.tennis.tournament.manager.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String description;
    private final HttpStatus httpStatus;

    public CustomException(ErrorCode errorCode, String title, String description) {
        super(title);
        this.errorCode = errorCode;
        this.description = description;
        this.httpStatus = errorCode.getHttpStatus();
    }

    public CustomException(ErrorCode errorCode, String title) {
        super(title);
        this.errorCode = errorCode;
        this.httpStatus = errorCode.getHttpStatus();
        description = null;
    }
}
