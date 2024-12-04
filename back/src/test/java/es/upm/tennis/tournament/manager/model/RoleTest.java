package es.upm.tennis.tournament.manager.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create Role with valid type")
        void shouldCreateRoleWithValidType() {
            ERole type = ERole.ADMIN;

            Role role = new Role();
            role.setType(type);

            assertNotNull(role.getType());
            assertEquals(type, role.getType());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should get and set id")
        void shouldGetAndSetId() {
            Role role = new Role();
            Long id = 1L;

            role.setId(id);

            assertEquals(id, role.getId());
        }

        @Test
        @DisplayName("Should get and set type")
        void shouldGetAndSetType() {
            Role role = new Role();
            ERole type = ERole.USER;

            role.setType(type);

            assertEquals(type, role.getType());
        }
    }
}