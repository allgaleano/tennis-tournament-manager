package es.upm.tennis.tournament.manager.controller;


import es.upm.tennis.tournament.manager.DTO.UserDTO;
import es.upm.tennis.tournament.manager.model.User;
import es.upm.tennis.tournament.manager.model.UserSession;
import es.upm.tennis.tournament.manager.service.UserService;
import es.upm.tennis.tournament.manager.service.UserSessionService;
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

    @Autowired
    private UserSessionService sessionService;

    @GetMapping("/session")
    public ResponseEntity<Map<String, Object>> validateSession(
            Authentication authentication
    ) {
        UserSession activeSession = sessionService.getActiveSession(authentication);
        return ResponseEntity.ok(Map.of(
            "sessionId", activeSession.getSessionId(),
           "expirationDate", activeSession.getExpirationDate()
        ));
    }

    @GetMapping("/userData")
    public ResponseEntity<Map<String, Object>> getUserData(
            Authentication authentication
    ) {
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
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(
            @PathVariable Long id,
            @RequestHeader("Session-Id") String sessionId
    ) {
        userService.deleteUser(id, sessionId);
        return ResponseEntity.ok("User deleted successfully");

    }

    @PutMapping("/users/{id}")
    public ResponseEntity<String> modifyUser(
            @PathVariable Long id,
            @RequestHeader("Session-Id") String sessionId,
            @RequestBody UserDTO userDTO
    ) {
        userService.modifyUser(id, sessionId, userDTO);
        return ResponseEntity.ok("User modified successfully");
    }
}