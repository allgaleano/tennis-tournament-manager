package es.upm.tennis.tournament.manager.controller;

import es.upm.tennis.tournament.manager.DTO.LoginRequest;
import es.upm.tennis.tournament.manager.DTO.UserDTO;
import es.upm.tennis.tournament.manager.exceptions.EmailAlreadyExistsException;
import es.upm.tennis.tournament.manager.exceptions.InvalidCodeException;
import es.upm.tennis.tournament.manager.exceptions.UsernameAlreadyExistsException;
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
    public ResponseEntity<String> register(@RequestBody UserDTO userDTO) {
        try {
            userService.registerUser(userDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("Confirmation email sent");
        } catch (UsernameAlreadyExistsException | EmailAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Registration failed");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        try {
            Map<String, Object> loginResponse = userService.authenticateUser(loginRequest.getUsername(), loginRequest.getPassword());
            return ResponseEntity.ok(loginResponse);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "error", "Invalid username or password"
            ));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "error", "User not found"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "An unexpected error occurred",
                    "details", e.getMessage()
            ));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Session-Id") String sessionId) {
        if (sessionService.invalidateSession(sessionId)) {
            return ResponseEntity.ok("User logged out successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Session not found");
        }
    }

    @GetMapping("/confirm-email")
    public ResponseEntity<String> confirmEmail(@RequestParam("email") String email, @RequestParam("code") String code) {
        try {
            userService.confirmUser(email, code);
            return ResponseEntity.ok("Account verified successfully");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InvalidCodeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}