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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static es.upm.tennis.tournament.manager.utils.Endpoints.FRONTEND_URI;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
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
        logger.info("Registering user");
        User userExists = userRepository.findByEmail(userDTO.getEmail());

        if (userExists != null) {
            if (!userExists.isEnabled() &&
                    Duration.between(LocalDateTime.now(), userExists.getCreatedAt())
                            .compareTo(Duration.ofMinutes(30)) > 0)
            {
                ConfirmationCode existingUserCode = confirmationCodeRepository.findByUser(userExists);
                confirmationCodeRepository.delete(existingUserCode);
                userRepository.delete(userExists);
            } else {
                throw new EmailAlreadyExistsException("An account associated to that email already exists");
            }
        }

        userExists = userRepository.findByUsername(userDTO.getUsername());
        if (userExists != null) throw new UserAlreadyExistsException("Username already taken");


        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setEnabled(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        Role userRole = roleRepository.findByType(ERole.USER);
        user.setRoles(Set.of(userRole));

        userRepository.save(user);

        int validMinutes = 30;
        ConfirmationCode confirmationCode = new ConfirmationCode(user, validMinutes);
        confirmationCodeRepository.save(confirmationCode);

        try {
            sendConfirmationEmail(user, confirmationCode.getCode(), validMinutes);
        } catch (Exception e) {
            throw new EmailNotSentException("Error sending the confirmation email");
        }
    }

    private void sendConfirmationEmail(User user, String code, int validMinutes) {
        String emailBody = "Hola, " + user.getUsername() + ",\n\n" +
                "Haz click en este enlace para verficar tu cuenta:\n" +
                FRONTEND_URI + "/confirm-email?token=" + code + "\n\n" +
                "Este enlace es válido durante " + validMinutes + " minutos.";
        emailService.sendEmail(user.getEmail(), "Verifica tu cuenta", emailBody);
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

    public void confirmUser(String code) {

        ConfirmationCode confirmationCode = confirmationCodeRepository.findByCode(code);
        if (confirmationCode == null) {
            throw  new InvalidCodeException("Invalid code");
        }

        if (LocalDateTime.now().isAfter(confirmationCode.getExpirationDate())) {
            confirmationCodeRepository.delete(confirmationCode);
            throw new InvalidCodeException("Expired code");
        }

        User user = confirmationCode.getUser();

        if (user.isEnabled()) {
            throw new InvalidCodeException("User already verified");
        }

        user.setEnabled(true);
        userRepository.save(user);
        confirmationCodeRepository.delete(confirmationCode);
    }

    public void changePassword(String email) {
        logger.info("email: {}", email);
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        ConfirmationCode existingCode = confirmationCodeRepository.findByUser(user);
        if (existingCode != null) {
            confirmationCodeRepository.delete(existingCode);
        }

        int validMinutes = 5;
        ConfirmationCode code = new ConfirmationCode(user, validMinutes);
        confirmationCodeRepository.save(code);

        String body = "Hola, " + user.getUsername() + ",\n\n" +
        "Haz click en este enlace para cambiar tu contraseña:\n" +
                FRONTEND_URI + "/change-password?token=" + code.getCode() + "\n\n" +
                "Este enlace es válido durante " + validMinutes + " minutos.";
        try {
            emailService.sendEmail(user.getEmail(), "Cambia tu contraseña", body);
        } catch (Exception e) {
            throw new EmailNotSentException("Error sending the password modification email");
        }
    }

    public void confirmPassword(String password, String token) {

        ConfirmationCode passCode = confirmationCodeRepository.findByCode(token);
        logger.info("passCode {}",passCode);
        if (passCode == null) {
            throw  new InvalidCodeException("Invalid code");
        } else if (LocalDateTime.now().isAfter(passCode.getExpirationDate())) {
            confirmationCodeRepository.delete(passCode);
            throw new InvalidCodeException("Expired code");
        }
        User user = passCode.getUser();

        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        confirmationCodeRepository.delete(passCode);
    }
}
