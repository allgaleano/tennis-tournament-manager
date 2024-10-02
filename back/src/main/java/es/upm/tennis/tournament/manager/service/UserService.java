package es.upm.tennis.tournament.manager.service;

import es.upm.tennis.tournament.manager.DTO.UserDTO;
import es.upm.tennis.tournament.manager.exceptions.*;
import es.upm.tennis.tournament.manager.model.ConfirmationCode;
import es.upm.tennis.tournament.manager.model.ERole;
import es.upm.tennis.tournament.manager.model.Role;
import es.upm.tennis.tournament.manager.model.User;
import es.upm.tennis.tournament.manager.repo.ConfirmationCodeRepository;
import es.upm.tennis.tournament.manager.repo.RoleRepository;
import es.upm.tennis.tournament.manager.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ConfirmationCodeRepository confirmationCodeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private  UserSessionService sessionService;

    @Autowired
    private EmailService emailService;

    public void registerUser(UserDTO userDTO) {
        User userExists = userRepository.findByUsername(userDTO.getUsername());
        if (userExists != null) throw new UsernameAlreadyExistsException("Username already taken");

        userExists = userRepository.findByEmail(userDTO.getEmail());
        if (userExists != null) throw new EmailAlreadyExistsException("An account associated to that email already exists");

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setEnabled(false);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        Role userRole = roleRepository.findByType(ERole.USER);
        user.setRoles(Set.of(userRole));

        userRepository.save(user);

        ConfirmationCode confirmationCode = new ConfirmationCode(user);
        confirmationCodeRepository.save(confirmationCode);

        try {
            sendConfirmationEmail(user, confirmationCode.getCode());
        } catch (Exception e) {
            throw new EmailNotSentException("Error sending the confirmation email");
        }
    }

    private void sendConfirmationEmail(User user, String code) {
        String emailBody = "Hello, " + user.getUsername() + ",\n\n" +
                "Please use the following code to verify your email address and complete your registration:\n" +
                code + "\n\n" +
                "This code is valid for 30 minutes.";
        emailService.sendEmail(user.getEmail(), "Confirm your email", emailBody);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }


    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Map<String, Object> authenticateUser(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = findByUsername(userDetails.getUsername());

        if (!user.isEnabled()) {
            throw new AccountNotEnabledException("Confirm your email to activate your account");
        }

        String sessionId = sessionService.createSession(user);

        return Map.of(
                "sessionId", sessionId,
                "username", user.getUsername(),
                "roles", user.getRoles().stream().map(Role::getType).collect(Collectors.toList())
        );
    }

    public void confirmUser(String email, String code) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        ConfirmationCode confirmationCode = confirmationCodeRepository.findByCode(code);
        if (confirmationCode == null || !confirmationCode.getCode().equals(code)) {
            throw new InvalidCodeException("Invalid or expired confirmation code");
        }

        user.setEnabled(true);
        userRepository.save(user);
        confirmationCodeRepository.delete(confirmationCode);
    }
}
