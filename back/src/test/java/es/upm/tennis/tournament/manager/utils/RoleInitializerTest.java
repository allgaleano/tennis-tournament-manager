package es.upm.tennis.tournament.manager.utils;

import es.upm.tennis.tournament.manager.model.ERole;
import es.upm.tennis.tournament.manager.model.Role;
import es.upm.tennis.tournament.manager.repo.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Role Initializer Tests")
class RoleInitializerTest {

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        roleRepository.deleteAll();
    }

    @Test
    @DisplayName("Should initialize roles correctly")
    void testRolesInitialized() {
        // Act


        // Assert
        List<Role> roles = roleRepository.findAll();
        assertEquals(0, roles.size());
        assertFalse(roles.stream().anyMatch(role -> role.getType().equals(ERole.ADMIN)));
        assertFalse(roles.stream().anyMatch(role -> role.getType().equals(ERole.USER)));
    }
}