package es.upm.tennis.tournament.manager.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testUserRegistration() throws Exception {
        String userRegistration = """
                {
                    "username" : "testuser",
                    "password" : "testpassword",
                    "email" : "testuser@example.com"
                }
                """;
        mockMvc
                .perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userRegistration))
                .andExpect(status().isCreated())
                .andExpect(content().string("User registered successfully"));
    }
}
