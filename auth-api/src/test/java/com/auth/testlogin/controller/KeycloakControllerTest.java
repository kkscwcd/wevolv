package com.auth.testlogin.controller;

import com.auth.testlogin.model.UserCredentials;
import com.auth.testlogin.model.dto.TokenDto;
import com.auth.testlogin.service.KeyCloakService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {KeycloakController.class})
@ExtendWith(SpringExtension.class)
public class KeycloakControllerTest {
    @MockBean
    private KeyCloakService keyCloakService;

    @Autowired
    private KeycloakController keycloakController;

    @Test
    public void testGetHealthCheck() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/health");
        MockMvcBuilders.standaloneSetup(this.keycloakController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("{\"HEALTH\":\"OK\"}"));
    }

    @Test
    public void testGetHealthCheck2() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/health", "Uri Vars");
        MockMvcBuilders.standaloneSetup(this.keycloakController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("{\"HEALTH\":\"OK\"}"));
    }

    @Test
    public void testGetHealthCheck3() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/health");
        MockMvcBuilders.standaloneSetup(this.keycloakController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("{\"HEALTH\":\"OK\"}"));
    }

    @Test
    public void testGetHealthCheck4() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/health", "Uri Vars");
        MockMvcBuilders.standaloneSetup(this.keycloakController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("{\"HEALTH\":\"OK\"}"));
    }

    @Test
    public void testGetTokenUsingCredentials() throws Exception {
        UserCredentials userCredentials = new UserCredentials();
        userCredentials.setPassword("iloveyou");
        userCredentials.setUsername("janedoe");
        String content = (new ObjectMapper()).writeValueAsString(userCredentials);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(this.keycloakController)
                .build()
                .perform(requestBuilder);
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testGetTokenUsingCredentials2() throws Exception {
        UserCredentials userCredentials = new UserCredentials();
        userCredentials.setPassword("");
        userCredentials.setUsername("janedoe");
        String content = (new ObjectMapper()).writeValueAsString(userCredentials);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(this.keycloakController)
                .build()
                .perform(requestBuilder);
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testGetTokenUsingCredentials3() throws Exception {
        UserCredentials userCredentials = new UserCredentials();
        userCredentials.setPassword("iloveyou");
        userCredentials.setUsername("janedoe");
        String content = (new ObjectMapper()).writeValueAsString(userCredentials);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(this.keycloakController)
                .build()
                .perform(requestBuilder);
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testGetTokenUsingCredentials4() throws Exception {
        when(this.keyCloakService.getToken((UserCredentials) any())).thenReturn(new TokenDto());

        UserCredentials userCredentials = new UserCredentials();
        userCredentials.setPassword("Invalid password! Minimum 12 characters.");
        userCredentials.setUsername("");
        String content = (new ObjectMapper()).writeValueAsString(userCredentials);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(this.keycloakController)
                .build()
                .perform(requestBuilder);
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testGetTokenUsingCredentials5() throws Exception {
        UserCredentials userCredentials = new UserCredentials();
        userCredentials.setPassword("UUUUUUUUUU");
        userCredentials.setUsername("janedoe");
        String content = (new ObjectMapper()).writeValueAsString(userCredentials);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(this.keycloakController)
                .build()
                .perform(requestBuilder);
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testGetTokenUsingCredentials6() throws Exception {
        UserCredentials userCredentials = new UserCredentials();
        userCredentials.setPassword("");
        userCredentials.setUsername("janedoe");
        String content = (new ObjectMapper()).writeValueAsString(userCredentials);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(this.keycloakController)
                .build()
                .perform(requestBuilder);
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testGetTokenUsingCredentials7() throws Exception {
        UserCredentials userCredentials = new UserCredentials();
        userCredentials.setPassword("iloveyou");
        userCredentials.setUsername("UUUUUUUUUU");
        String content = (new ObjectMapper()).writeValueAsString(userCredentials);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(this.keycloakController)
                .build()
                .perform(requestBuilder);
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testGetTokenUsingCredentials8() throws Exception {
        UserCredentials userCredentials = new UserCredentials();
        userCredentials.setPassword("iloveyou");
        userCredentials.setUsername("");
        String content = (new ObjectMapper()).writeValueAsString(userCredentials);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(this.keycloakController)
                .build()
                .perform(requestBuilder);
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testGetTokenUsingRefreshToken() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/refreshtoken");
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(this.keycloakController)
                .build()
                .perform(requestBuilder);
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testGetTokenUsingRefreshToken2() throws Exception {
        MockHttpServletRequestBuilder getResult = MockMvcRequestBuilders.get("/refreshtoken");
        getResult.contentType("Not all who wander are lost");
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(this.keycloakController)
                .build()
                .perform(getResult);
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testGetTokenUsingRefreshToken3() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/refreshtoken");
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(this.keycloakController)
                .build()
                .perform(requestBuilder);
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testGetTokenUsingRefreshToken4() throws Exception {
        MockHttpServletRequestBuilder getResult = MockMvcRequestBuilders.get("/refreshtoken");
        getResult.contentType("Not all who wander are lost");
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(this.keycloakController)
                .build()
                .perform(getResult);
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}

