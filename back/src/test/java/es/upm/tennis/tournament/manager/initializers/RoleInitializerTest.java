package es.upm.tennis.tournament.manager.initializers;

import es.upm.tennis.tournament.manager.model.ERole;
import es.upm.tennis.tournament.manager.model.Role;
import es.upm.tennis.tournament.manager.repo.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
class RoleInitializerTest {

	@BeforeEach
	void beforeEach() {
		roleRepository.deleteAll();
		Role user = new Role();
		Role admin = new Role();
		user.setType(ERole.USER);
		admin.setType(ERole.ADMIN);
		roleRepository.save(user);
		roleRepository.save(admin);
	}

	@Autowired
	private RoleRepository roleRepository;

	@Test
	void testRolesInitialized() {
		List<Role> roles = roleRepository.findAll();
		assertEquals(2, roles.size());
		assertTrue(roles.stream().anyMatch(role -> role.getType().equals(ERole.ADMIN)));
		assertTrue(roles.stream().anyMatch(role -> role.getType().equals(ERole.USER)));
	}
}
