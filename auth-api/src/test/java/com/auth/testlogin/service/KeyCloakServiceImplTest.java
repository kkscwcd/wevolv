package com.auth.testlogin.service;

import com.auth.testlogin.exceptions.TokenNotValidException;
import com.auth.testlogin.exceptions.WrongUserCredentialsException;
import com.auth.testlogin.model.UserCredentials;
import com.auth.testlogin.service.impl.KeyCloakServiceImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

public class KeyCloakServiceImplTest {
    @Test
    public void testGetToken() {
        KeyCloakServiceImpl keyCloakServiceImpl = new KeyCloakServiceImpl();
        assertThrows(WrongUserCredentialsException.class,
                () -> keyCloakServiceImpl.getToken(new UserCredentials("iloveyou", "janedoe")));
    }

    @Test
    public void testGetToken2() {
        assertThrows(WrongUserCredentialsException.class, () -> (new KeyCloakServiceImpl()).getToken(null));
    }

    @Test
    public void testGetToken3() {
        assertThrows(WrongUserCredentialsException.class,
                () -> (new KeyCloakServiceImpl()).getToken(mock(UserCredentials.class)));
    }

    @Test
    public void testGetToken4() {
        KeyCloakServiceImpl keyCloakServiceImpl = new KeyCloakServiceImpl();
        assertThrows(WrongUserCredentialsException.class,
                () -> keyCloakServiceImpl.getToken(new UserCredentials("iloveyou", "janedoe")));
    }

    @Test
    public void testGetToken5() {
        assertThrows(WrongUserCredentialsException.class, () -> (new KeyCloakServiceImpl()).getToken(null));
    }

    @Test
    public void testGetToken6() {
        assertThrows(WrongUserCredentialsException.class,
                () -> (new KeyCloakServiceImpl()).getToken(mock(UserCredentials.class)));
    }

    @Test
    public void testGetUserInfo() {
        assertThrows(TokenNotValidException.class, () -> (new KeyCloakServiceImpl()).getUserInfo("ABC123"));
        assertThrows(TokenNotValidException.class, () -> (new KeyCloakServiceImpl()).getUserInfo(null));
    }

    @Test
    public void testGetByRefreshToken() {
        assertThrows(TokenNotValidException.class, () -> (new KeyCloakServiceImpl()).getByRefreshToken("ABC123"));
        assertThrows(TokenNotValidException.class,
                () -> (new KeyCloakServiceImpl()).getByRefreshToken("Get token by refresh token in service layer started."));
    }

    @Test
    public void testLogoutUser() {
        assertThrows(TokenNotValidException.class, () -> (new KeyCloakServiceImpl()).logoutUser("ABC123"));
        assertThrows(TokenNotValidException.class,
                () -> (new KeyCloakServiceImpl()).logoutUser("Logout user from service layer started."));
    }
}

