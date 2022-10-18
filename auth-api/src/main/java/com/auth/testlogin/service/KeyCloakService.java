package com.auth.testlogin.service;

import com.auth.testlogin.model.UserCredentials;
import com.auth.testlogin.model.dto.ForgotPasswordDto;
import com.auth.testlogin.model.dto.ResetPasswordDto;
import com.auth.testlogin.model.dto.TokenDto;
import com.auth.testlogin.model.dto.UserInfoDto;
import org.keycloak.representations.idm.GroupRepresentation;

import java.util.List;

/**
 * @author Djordje
 * @version 1.0
 */
public interface KeyCloakService {

    TokenDto getToken(UserCredentials userCredentials) throws Exception;

    UserInfoDto getUserInfo(String token);

    TokenDto getByRefreshToken(String refreshToken);

    void logoutUser(String userId);

    void resetPasswordFromAdmin(ResetPasswordDto resetPasswordDto, String jwtToken);

    String forgotPasswordReset(ForgotPasswordDto forgotPasswordDto) throws Exception;

    List<GroupRepresentation> getRealmGroups();

    void joinGroup(String groupId, String userId);

    GroupRepresentation findGroupByPath(String path);
}