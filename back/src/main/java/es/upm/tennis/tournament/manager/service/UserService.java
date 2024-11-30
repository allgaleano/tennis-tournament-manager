package es.upm.tennis.tournament.manager.service;

import es.upm.tennis.tournament.manager.DTO.UserDTO;
import es.upm.tennis.tournament.manager.exceptions.*;
import es.upm.tennis.tournament.manager.model.*;
import es.upm.tennis.tournament.manager.repo.ConfirmationCodeRepository;
import es.upm.tennis.tournament.manager.repo.RoleRepository;
import es.upm.tennis.tournament.manager.repo.UserRepository;
import es.upm.tennis.tournament.manager.repo.UserSessionRepository;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

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
    private UserSessionRepository userSessionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private  UserSessionService sessionService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PermissionChecker permissionChecker;

    public void registerUser(UserDTO userDTO) {
        logger.info("Registering user");
        User existingUser = userRepository.findByEmail(userDTO.getEmail());

        if (existingUser != null) {
            ConfirmationCode existingUserCode = confirmationCodeRepository.findByUser(existingUser);

            boolean isCodeExpired = existingUserCode != null &&
                    Duration.between(Instant.now(), existingUserCode.getExpirationDate())
                            .compareTo(Duration.ZERO) < 0;

            boolean isOlderThanOneDay = Duration.between(existingUser.getCreatedAt(), Instant.now())
                    .compareTo(Duration.ofMinutes(1440)) > 0;

            if (!existingUser.isConfirmed() && isCodeExpired && isOlderThanOneDay) {
                confirmationCodeRepository.delete(existingUserCode);
                userRepository.delete(existingUser);
            } else {
                logger.info("Account already exists");
                throw new CustomException(
                        ErrorCode.EMAIL_ALREADY_EXISTS,
                        "Una cuenta con este email ya existe",
                        "Intente con otro email"
                );
            }
        }

        existingUser = userRepository.findByUsername(userDTO.getUsername());
        if (existingUser != null) {
            throw new CustomException(
                    ErrorCode.USERNAME_ALREADY_EXISTS,
                    "Nombre de usuario no disponible",
                    "Intente con otro nombre de usuario"
            );
        }


        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setName(userDTO.getName());
        user.setSurname(userDTO.getSurname());
        user.setPhonePrefix(userDTO.getPhonePrefix());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setConfirmed(false);
        user.setCreatedAt(Instant.now());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        Role userRole = roleRepository.findByType(ERole.USER);
        user.setRole(userRole);

        logger.info("Account created successfully");
        userRepository.save(user);

        int validMinutes = 30;
        ConfirmationCode confirmationCode = new ConfirmationCode(user, validMinutes);
        confirmationCodeRepository.save(confirmationCode);

        try {
            sendConfirmationEmail(user, confirmationCode.getCode(), validMinutes);
            logger.info("Email sent successfully");
        } catch (Exception e) {
            logger.info("Error sending the email");
            throw new CustomException(
                    ErrorCode.EMAIL_NOT_SENT,
                    "Se ha producido un error al enviar el email de confirmación",
                    "Inténtelo de nuevo más tarde"
            );
        }
    }

    private void sendConfirmationEmail(User user, String code, int validMinutes) throws MessagingException {
        String emailBody = """
        <!DOCTYPE html>
        <html>
        <head><meta charset='UTF-8'></head>
        <body style='font-family: Arial'>
            <p style='font-size:16px; color:#333;'>Hola <strong style='color:#0056b3;'>%s</strong>,</p>
            <p style='font-size:14px; color:#333;'>Haz click en el siguiente enlace para verificar tu cuenta:</p>
            <p><a href='%s/confirm-email?token=%s' style='font-size:14px; color:white; background-color:#363636; padding:10px 15px; text-decoration:none; border-radius:25px;'>Verificar cuenta</a></p>
            <p style='font-size:12px; color:#666;'>Este enlace es válido durante %d minutos.</p>
            <p style='font-size:14px; color:#333;'>Saludos,<br><em>El equipo de soporte</em></p>
        </body>
        </html>
        """.formatted(user.getUsername(), FRONTEND_URI, code, validMinutes);
        emailService.sendEmail(user.getEmail(), "Verifica tu cuenta", emailBody);
    }


    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Map<String, Object> authenticateUser(String username, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userRepository.findByUsername(userDetails.getUsername());

            if (!user.isConfirmed()) {
                throw new CustomException(
                        ErrorCode.ACCOUNT_NOT_CONFIRMED,
                        "Confirma tu email para activar tu cuenta",
                        "Revisa tu bandeja de entrada"
                );
            }

            if (!user.isEnabled()) {
                throw new CustomException(
                        ErrorCode.ACCOUNT_DISABLED,
                        "Cuenta deshabilitada",
                        "Contacta con soporte"
                );
            }

            UserSession session = sessionService.findByUser(user);
            if (session != null) {
                sessionService.invalidateSession(session.getSessionId());
            }

            session = sessionService.createSession(user);

            return Map.of(
                    "sessionId", session.getSessionId(),
                    "sessionExp", session.getExpirationDate()
            );
        } catch (BadCredentialsException e) {
            throw new CustomException(
                    ErrorCode.BAD_CREDENTIALS,
                    "Usuario o contraseña incorrectos"
            );
        }
    }

    public void confirmUser(String code) {

        ConfirmationCode confirmationCode = confirmationCodeRepository.findByCode(code);
        if (confirmationCode == null) {
            throw new CustomException(
                    ErrorCode.INVALID_TOKEN,
                    "Error al verificar la cuenta",
                    "El enlace no es válido"
            );
        }

        if (Instant.now().isAfter(confirmationCode.getExpirationDate())) {
            confirmationCodeRepository.delete(confirmationCode);
            throw new CustomException(
                    ErrorCode.INVALID_TOKEN,
                    "Error al verificar la cuenta",
                    "El enlace ha caducado"
            );
        }

        User user = confirmationCode.getUser();

        if (user.isConfirmed()) {
            throw new CustomException(
                    ErrorCode.CONFIRMATION_CONFLICT,
                    "Usuario ya verificado"
            );
        }

        user.setConfirmed(true);
        userRepository.save(user);
        confirmationCodeRepository.delete(confirmationCode);
    }

    public void changePassword(String email) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new CustomException(
                    ErrorCode.USER_NOT_FOUND,
                    "Usuario no encontrado"
            );
        }
        ConfirmationCode existingCode = confirmationCodeRepository.findByUser(user);
        if (existingCode != null) {
            confirmationCodeRepository.delete(existingCode);
        }

        int validMinutes = 5;
        ConfirmationCode code = new ConfirmationCode(user, validMinutes);
        confirmationCodeRepository.save(code);

        try {
            sendConfirmationPasswordEmail(user, code.getCode(), validMinutes);
        } catch (Exception e) {
            throw new CustomException(
                    ErrorCode.EMAIL_NOT_SENT,
                    "Se ha producido un error al enviar el email de modificación de contraseña",
                    "Inténtelo de nuevo más tarde"
            );
        }
    }

    private void sendConfirmationPasswordEmail(User user, String code, int validMinutes) throws MessagingException {
        String emailBody = """
        <!DOCTYPE html>
        <html>
        <head><meta charset='UTF-8'></head>
        <body style='font-family: Arial'>
            <p style='font-size:16px; color:#333;'>Hola <strong style='color:#0056b3;'>%s</strong>,</p>
            <p style='font-size:14px; color:#333;'>Haz click en el siguiente enlace para cambiar tu contraseña:</p>
            <p><a href='%s/confirm-password?token=%s' style='font-size:14px; color:white; background-color:#363636; padding:10px 15px; text-decoration:none; border-radius:25px;'>Cambiar contraseña</a></p>
            <p style='font-size:12px; color:#666;'>Este enlace es válido durante %d minutos.</p>
            <p style='font-size:14px; color:#333;'>Saludos,<br><em>El equipo de soporte</em></p>
        </body>
        </html>
        """.formatted(user.getUsername(), FRONTEND_URI, code, validMinutes);
        emailService.sendEmail(user.getEmail(), "Cambia tu contraseña", emailBody);
    }

    public void confirmPassword(String password, String token) {
        ConfirmationCode passCode = confirmationCodeRepository.findByCode(token);
        if (passCode == null) {
            throw new CustomException(
                    ErrorCode.INVALID_TOKEN,
                    "Error al cambiar la contraseña",
                    "El enlace no es válido o ha caducado"
            );
        } else if (Instant.now().isAfter(passCode.getExpirationDate())) {
            confirmationCodeRepository.delete(passCode);
            throw new CustomException(
                    ErrorCode.INVALID_TOKEN,
                    "Error al cambiar la contraseña",
                    "El enlace no es válido o ha caducado"
            );
        }
        User user = passCode.getUser();
        user.setPassword(passwordEncoder.encode(password));

        UserSession activeSession = userSessionRepository.findByUser(user);
        if (activeSession != null) {
            userSessionRepository.delete(activeSession);
        }
        userRepository.save(user);
        confirmationCodeRepository.delete(passCode);
    }


    public User getUserData(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return userRepository.findByUsername(user.getUsername());
    }

    public void deleteUser(Long id, String sessionId) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new CustomException(
                    ErrorCode.USER_NOT_FOUND,
                    "Usuario no encontrado"
            );
        }

        UserSession userSession = userSessionRepository.findBySessionId(sessionId);

        permissionChecker.validateUserPermission(user.get(), userSession);

        userSessionRepository.delete(userSession);
        ConfirmationCode code = confirmationCodeRepository.findByUser(user.get());
        if (code != null) {
            confirmationCodeRepository.delete(code);
        }
        userRepository.delete(user.get());
    }

    public void modifyUser(Long id, String sessionId, UserDTO userDTO) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new CustomException(
                    ErrorCode.USER_NOT_FOUND,
                    "Usuario no encontrado"
            );
        }

        UserSession userSession = userSessionRepository.findBySessionId(sessionId);

        permissionChecker.validateUserPermission(user.get(), userSession);

        if (userDTO.getUsername() != null) {
            User existingUser = userRepository.findByUsername(userDTO.getUsername());
            if (existingUser != null) {
                throw new CustomException(
                        ErrorCode.USERNAME_ALREADY_EXISTS,
                        "Nombre de usuario no disponible",
                        "Intente con otro nombre de usuario"
                );
            }
            user.get().setUsername(userDTO.getUsername());
        }

        if (userDTO.getName() != null)
            user.get().setName(userDTO.getName());

        if (userDTO.getSurname() != null)
            user.get().setSurname(userDTO.getSurname());

        if (userDTO.getPhonePrefix() != null && userDTO.getPhoneNumber() != null) {
            user.get().setPhonePrefix(userDTO.getPhonePrefix());
            user.get().setPhoneNumber(userDTO.getPhoneNumber());
        }
        boolean isAdmin = userSession.getUser().getRole().getType().name().equals("ADMIN");
        if (isAdmin) {
            if (userDTO.getRole() != null) {
                if (user.get().getRole().getType().name().equals(userDTO.getRole())) {
                    throw new CustomException(
                            ErrorCode.INVALID_ROLE,
                            "El usuario ya tiene este rol",
                            "Intente con otro rol"
                    );
                }
                ERole roleType = userDTO.getRole().equals("ADMIN") ? ERole.ADMIN : ERole.USER;
                Role role = roleRepository.findByType(roleType);
                user.get().setRole(role);
            }

            if (userDTO.getAccountState() != null) {
                boolean enabledAccount = userDTO.getAccountState().equals("enabledAccount");
                if (user.get().isEnabled() && enabledAccount) {
                    throw new CustomException(
                            ErrorCode.INVALID_ACCOUNT_STATUS,
                            "No se puede cambiar al mismo estado de cuenta",
                            "Intente con otro estado"
                    );
                }
                user.get().setEnabled(enabledAccount);
            }
        }

        userRepository.save(user.get());
    }
}
