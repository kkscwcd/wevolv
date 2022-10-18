package com.auth.testlogin.controller;

import com.auth.testlogin.model.dto.ResetPasswordDto;
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

@ContextConfiguration(classes = {UserController.class})
@ExtendWith(SpringExtension.class)
public class UserControllerTest {
    @MockBean
    private KeyCloakService keyCloakService;

    @Autowired
    private UserController userController;

    @Test
    public void testLogoutUser() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/user/logout");
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(this.userController)
                .build()
                .perform(requestBuilder);
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testUpdatePassword() throws Exception {
        ResetPasswordDto resetPasswordDto = new ResetPasswordDto();
        resetPasswordDto.setConfirm("Confirm");
        resetPasswordDto.setPassword("iloveyou");
        String content = (new ObjectMapper()).writeValueAsString(resetPasswordDto);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/user/update/password")
                .param("userId", "foo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(this.userController)
                .build()
                .perform(requestBuilder);
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testUpdatePassword2() throws Exception {
        ResetPasswordDto resetPasswordDto = new ResetPasswordDto();
        resetPasswordDto.setConfirm("Confirm");
        resetPasswordDto.setPassword("iloveyou");
        String content = (new ObjectMapper()).writeValueAsString(resetPasswordDto);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/user/update/password")
                .param("userId", "foo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(this.userController)
                .build()
                .perform(requestBuilder);
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testUpdatePassword3() throws Exception {
        ResetPasswordDto resetPasswordDto = new ResetPasswordDto();
        resetPasswordDto.setConfirm("Confirm");
        resetPasswordDto.setPassword("iloveyou");
        String content = (new ObjectMapper()).writeValueAsString(resetPasswordDto);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/user/update/password", null)
                .param("userId", "foo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(this.userController)
                .build()
                .perform(requestBuilder);
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testUpdatePassword4() throws Exception {
        ResetPasswordDto resetPasswordDto = new ResetPasswordDto();
        resetPasswordDto.setConfirm("Confirm");
        resetPasswordDto.setPassword("UUUUUUUUUU");
        String content = (new ObjectMapper()).writeValueAsString(resetPasswordDto);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/user/update/password")
                .param("userId", "foo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(this.userController)
                .build()
                .perform(requestBuilder);
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}

