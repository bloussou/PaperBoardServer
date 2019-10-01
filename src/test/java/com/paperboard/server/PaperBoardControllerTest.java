package com.paperboard.server;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PaperBoardControllerTest {
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
     * Check that the paperBoard is empty if nothing has been done before, or clearData has been called
     */
    @Test
    public void getAllPaperBoardEmpty() {
        try {
            mvc.perform(get("/paperboard")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Check that if you post two paperboards, those two are well stored in the server
     */
    @Test
    public void getAllPaperBoard() {
        try {
            // Add two paperBoard to the server
            mvc.perform(post("/paperboard?title=paperBoard1")).andExpect(status().isOk());
            mvc.perform(post("/paperboard?title=paperBoard2")).andExpect(status().isOk());

            mvc.perform(get("/paperboard")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].title", is("paperBoard1")))
                    .andExpect(jsonPath("$[1].title", is("paperBoard2")));
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Check that the post of a paperBoard only with a title is working
     */
    @Test
    public void createPaperBoard() {
        try {
            mvc.perform(post("/paperboard?title=paperBoard1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title", is("paperBoard1")))
                    .andExpect(jsonPath("$.backgroundColor", isEmptyOrNullString()))
                    .andExpect(jsonPath("$.drawers", emptyCollectionOf(ConcurrentSkipListSet.class)))
                    .andExpect(jsonPath("$.drawings", emptyCollectionOf(CopyOnWriteArrayList.class)))
                    .andExpect(jsonPath("$.backgroundImage", isEmptyOrNullString()));
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Check that the creation of a paperboard with a title and a background color is working
     */
    @Test
    public void createPaperBoardBackgroundColor() {
        try {
            mvc.perform(post("/paperboard?title=paperBoard1&backgroundColor=blue"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title", is("paperBoard1")))
                    .andExpect(jsonPath("$.backgroundColor", is("blue")))
                    .andExpect(jsonPath("$.drawers", emptyCollectionOf(ConcurrentSkipListSet.class)))
                    .andExpect(jsonPath("$.drawings", emptyCollectionOf(CopyOnWriteArrayList.class)))
                    .andExpect(jsonPath("$.backgroundImage", isEmptyOrNullString()));
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * TODO
     */
    @Test
    public void createPaperBoardBackgroundImage() {

        try {
            // TODO
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Check that the creation of two paperboards with the same name throws an error
     */
    @Test
    public void triggerErrorPaperboardDoubleCreationWithSameName() {
        try {
            mvc.perform(post("/paperboard?title=paperBoard1")).andExpect(status().isOk());
            // Check that the conflict error is triggered
            mvc.perform(post("/paperboard?title=paperBoard1")).andExpect(status().is4xxClientError());
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

}
