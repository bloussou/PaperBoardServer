package com.paperboard.server;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mvc;

    /**
     * Before each test the data are mooved away thanks to the clearData method
     */
    @Before
    public void setup() {
        ServerApplication.clearData();
    }

    /**
     * Check that the creation of a user is working
     */
    @Test
    public void createUser() {
        try {
            mvc.perform(post("/user?pseudo=john"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pseudo", is("john")));
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Check that if you create two users with the same pseudo an error is triggered
     */
    @Test
    public void triggerErrorUserCreationWithSameName() {
        try {
            mvc.perform(post("/user?pseudo=john")).andExpect(status().isOk());
            // Check that the conflict error is triggered
            mvc.perform(post("/user?pseudo=john")).andExpect(status().is4xxClientError());
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
