package com.auth.testlogin;

import com.auth.testlogin.model.UserCredentials;
import com.auth.testlogin.model.dto.TokenDto;
import com.auth.testlogin.service.KeyCloakService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URISyntaxException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;


@SpringBootTest
class AuthServiceTests {


    @Value("${keycloak.auth-server-url}")
    private String AUTHURL;

    @Value("${keycloak.realm}")
    private String REALM;

    @Mock
    private RestTemplate restTemplate;

    @MockBean
    private KeyCloakService service;

    @Test
    public void givenMockingIsDoneByMockRestServiceServer_whenGetIsCalled_thenReturnsMockedObject() throws Exception {
        UserCredentials userCredentials = UserCredentials.builder().username("username").password("password").build();
        TokenDto tokenDto = TokenDto.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .tokenType("Bearer")
                .expires_in("3600")
                .scope("openId")
                .userInfo(null)
                .build();

        Mockito
                .when(service.getToken(userCredentials))
                .thenReturn(tokenDto);
        Mockito
                .when(restTemplate.exchange(
                        eq(AUTHURL + "/realms/" + REALM + "/protocol/openid-connect/token"),
                        eq(HttpMethod.POST),
                        any(),
                        ArgumentMatchers.<Class<Object>>any(),
                        (Object) any()))
                .thenReturn(new ResponseEntity<>(tokenDto, HttpStatus.OK));

        TokenDto response = service.getToken(userCredentials);
        Assert.assertEquals(tokenDto, response);
    }

}
