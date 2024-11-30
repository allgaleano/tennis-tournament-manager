package es.upm.tennis.tournament.manager.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Map<String, Object>> handleCustomException(CustomException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("title", ex.getMessage());
        body.put("description", ex.getDescription());
        body.put("errorCode", ex.getErrorCode().getCode());
        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpectedException(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("title", "¡Algo ha salido mal!");
        body.put("description", "Ha ocurrido un error inesperado en el servidor, vuelve a intentarlo más tarde");
        body.put("errorCode", ErrorCode.INTERNAL_ERROR.getCode());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(body);
    }
}
