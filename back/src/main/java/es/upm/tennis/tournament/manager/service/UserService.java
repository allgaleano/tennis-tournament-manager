package es.upm.tennis.tournament.manager.service;

import es.upm.tennis.tournament.manager.exceptions.EmailAlreadyExistsException;
import es.upm.tennis.tournament.manager.exceptions.UsernameAlreadyExistsException;
import es.upm.tennis.tournament.manager.model.ERole;
import es.upm.tennis.tournament.manager.model.Role;
import es.upm.tennis.tournament.manager.model.User;
import es.upm.tennis.tournament.manager.repo.RoleRepository;
import es.upm.tennis.tournament.manager.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registerUser(String username, String email, String password, Set<ERole> roles) {
        User userExists = userRepository.findByUsername(username);
        if (userExists != null) throw new UsernameAlreadyExistsException("Username already taken");

        userExists = userRepository.findByEmail(email);
        if (userExists != null) throw new EmailAlreadyExistsException("An account associated to that email already exists");

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        Set<Role> userRoles = new HashSet<>();
        for (ERole role : roles) {
            Role dbRole = roleRepository.findByType(role);
            userRoles.add(dbRole);
        }
        user.setRoles(userRoles);

        userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }


    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
}
