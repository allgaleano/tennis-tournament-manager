package es.upm.tennis.tournament.manager.controller;

import es.upm.tennis.tournament.manager.DTO.LoginRequest;
import es.upm.tennis.tournament.manager.DTO.UserDTO;
import es.upm.tennis.tournament.manager.model.ERole;
import es.upm.tennis.tournament.manager.model.User;
import es.upm.tennis.tournament.manager.service.UserService;
import es.upm.tennis.tournament.manager.service.UserSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import es.upm.tennis.tournament.manager.model.Role;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private UserSessionService sessionService;

    @PostMapping("/register")
    public String register(@RequestBody UserDTO userDTO) {
        Set<ERole> roles = Set.of(ERole.USER);  // Default role is ROLE_USER
        userService.registerUser(userDTO.getUsername(),userDTO.getEmail(), userDTO.getPassword(), roles);
        return "User registered successfully";
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        System.out.println("login request");
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        System.out.println("after authentication");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Fetch the actual User entity from the database
        User user = userService.findByUsername(userDetails.getUsername());

        // Create a session for the user
        String sessionId = sessionService.createSession(user);

        // Return session ID and user details as a response
        return ResponseEntity.ok().body(Map.of(
                "sessionId", sessionId,
                "username", user.getUsername(),
                "roles", user.getRoles().stream().map(Role::getType).collect(Collectors.toList())
        ));
    }

    @PostMapping("/logout")
    public String logout(@RequestHeader("Session-Id") String sessionId) {
        sessionService.invalidateSession(sessionId);
        return "User logged out successfully";
    }
}