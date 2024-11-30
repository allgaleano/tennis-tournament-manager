package es.upm.tennis.tournament.manager.controller;

import es.upm.tennis.tournament.manager.DTO.*;
import es.upm.tennis.tournament.manager.exceptions.*;
import es.upm.tennis.tournament.manager.service.UserService;
import es.upm.tennis.tournament.manager.service.UserSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserSessionService sessionService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(
            @RequestBody UserDTO userDTO
    ) {
        userService.registerUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "title", "Email de confirmación enviado",
                "description", "Por favor, revisa tu bandeja de entrada para confirmar tu cuenta"
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody LoginRequest loginRequest
    ) {
        Map<String, Object> loginResponse = userService.authenticateUser(loginRequest.getUsername(), loginRequest.getPassword());
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader("Session-Id") String sessionId
    ) {
        sessionService.invalidateSession(sessionId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/confirm-email")
    public ResponseEntity<Map<String, String>> confirmEmail(
            @RequestParam("token") String token
    ) {
        userService.confirmUser(token);
        return ResponseEntity.ok(Map.of(
                "title", "Cuenta confirmada",
                "description", "Tu cuenta ha sido confirmada correctamente"
        ));
    }

    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> forgotPassword(
            @RequestBody ChangePasswordRequest changePasswordRequest
    ) {
        userService.changePassword(changePasswordRequest.getEmail());
        return ResponseEntity.ok(Map.of(
                "title", "Email de recuperación enviado",
                "description", "Por favor, revisa tu bandeja de entrada para recuperar tu contraseña"
        ));
    }

    @PostMapping("/confirm-password")
    public ResponseEntity<Map<String, String>> changePassword(
            @RequestBody ConfirmPasswordRequest confirmPasswordRequest,
            @RequestParam("token") String token
    ) {
        userService.confirmPassword(confirmPasswordRequest.getPassword(), token);
        return ResponseEntity.ok(Map.of(
                "title", "Contraseña cambiada",
                "description", "Tu contraseña ha sido cambiada correctamente"
        ));
    }
}