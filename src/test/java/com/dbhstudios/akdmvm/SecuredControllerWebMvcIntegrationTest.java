package com.dbhstudios.akdmvm;


import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Log4j2
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SecuredControllerWebMvcIntegrationTest {


    @Autowired
    private MockMvc mvc;

    @BeforeAll
    static void setup() {
        log.info("@BeforeAll - executes once before all test methods in this class");
    }

    @BeforeEach
    void init() {
        log.info("@BeforeEach - executes before each test method in this class");
    }




    @Test
    @WithMockUser(value = "diego")
    @DisplayName("GET /backstage success")
    public void givenAuthRequestOnPrivatePath_shouldSucceedWith200() throws Exception {
        mvc
                .perform(get("/backstage"))
                .andExpect(status().isOk());

    }


    @Test
    @DisplayName("GET /backstage redirects to login")
    public void givenNotAuthRequestOnPrivatePath_shouldRedirectToLogin() throws Exception {
        mvc
                .perform(get("/backstage"))
                .andExpect(status().is3xxRedirection());
    }
}
