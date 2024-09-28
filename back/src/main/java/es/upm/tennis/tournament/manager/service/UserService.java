package es.upm.tennis.tournament.manager.service;

import es.upm.tennis.tournament.manager.model.ERole;
import es.upm.tennis.tournament.manager.model.Role;
import es.upm.tennis.tournament.manager.model.User;
import es.upm.tennis.tournament.manager.repo.RoleRepository;
import es.upm.tennis.tournament.manager.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
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
        User user = new User();
        user.setUsername(username);
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
}
