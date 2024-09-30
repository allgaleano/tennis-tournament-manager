package es.upm.tennis.tournament.manager.model;

import es.upm.tennis.tournament.manager.repo.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;


    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.findByType(ERole.USER) == null) {
            Role userRole = new Role();
            userRole.setType(ERole.USER);
            roleRepository.save(userRole);
        }

        if (roleRepository.findByType(ERole.ADMIN) == null) {
            Role adminRole = new Role();
            adminRole.setType(ERole.ADMIN);
            roleRepository.save(adminRole);
        }

        System.out.println("Roles initialized");
    }
}
