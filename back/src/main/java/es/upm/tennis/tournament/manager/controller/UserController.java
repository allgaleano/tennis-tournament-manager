package es.upm.tennis.tournament.manager.controller;


import es.upm.tennis.tournament.manager.DTO.UserDTO;
import es.upm.tennis.tournament.manager.exceptions.InvalidCodeException;
import es.upm.tennis.tournament.manager.exceptions.SameRoleException;
import es.upm.tennis.tournament.manager.exceptions.UserAlreadyExistsException;
import es.upm.tennis.tournament.manager.exceptions.UserNotFoundException;
import es.upm.tennis.tournament.manager.model.User;
import es.upm.tennis.tournament.manager.model.UserSession;
import es.upm.tennis.tournament.manager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/session")
    public ResponseEntity<Map<String, Object>> validateSession(Authentication authentication) {
        try {
            UserSession activeSession = userService.getActiveSession(authentication);
            return ResponseEntity.ok(Map.of(
                "sessionId", activeSession.getSessionId(),
               "expirationDate", activeSession.getExpirationDate()
            ));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "error", e.getMessage()
            ));
        } catch (InvalidCodeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/userData")
    public ResponseEntity<Map<String, Object>> getUserData(Authentication authentication) {
        try {
            User user = userService.getUserData(authentication);
            return ResponseEntity.ok(Map.of(
                    "id", user.getId(),
                    "name", user.getName(),
                    "surname", user.getSurname(),
                    "username", user.getUsername(),
                    "email", user.getEmail(),
                    "phoneNumber", String.format("+ %s %s", user.getPhonePrefix(), user.getPhoneNumber()),
                    "role", user.getRole().getType().name(),
                    "createdAt", user.getCreatedAt()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "User data fetching failed unexpectedly"
            ));
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id, @RequestHeader("Session-Id") String sessionId) {
        try {
            userService.deleteUser(id, sessionId);
            return ResponseEntity.ok("User deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<String> modifyUser(@PathVariable Long id, @RequestHeader("Session-Id") String sessionId, @RequestBody UserDTO userDTO) {
        try {
            userService.modifyUser(id, sessionId, userDTO);
            return ResponseEntity.ok("User modified successfully");
        } catch (UserAlreadyExistsException | SameRoleException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}