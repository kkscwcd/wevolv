package com.auth.testlogin.controller;

import com.auth.testlogin.config.GenericApiResponse;
import com.auth.testlogin.exceptions.TokenNotValidException;
import com.auth.testlogin.model.UserCredentials;
import com.auth.testlogin.model.dto.SearchDto;
import com.auth.testlogin.model.dto.TokenDto;
import com.auth.testlogin.service.KeyCloakService;
import io.swagger.annotations.ApiOperation;
import org.json.simple.JSONObject;
import org.keycloak.representations.idm.GroupRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Keycloak controller for Adopt a pet project
 * Acceptance criterias:
 * 1)get token for the first time when user log in
 * 2)get new access token.
 * 3)health check
 */
@RestController
@RequestMapping(value = "/")
public class KeycloakController
{

  static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$";
  private static final Logger LOG = LoggerFactory.getLogger(KeycloakController.class);


  @Autowired
  private KeyCloakService keyClockService;

  /**
   * 1) Get token for the first time when user log in. We need to pass
   * credentials only once. Later communication will be done by sending token.
   */
  @RequestMapping(value = "/login", method = RequestMethod.POST)
  @ApiOperation(
    notes = "${operation1.description}",
    value = "${operation1.value}",
    responseContainer = "${operation1.responseContainer}",
    response = TokenDto.class
  )
  public GenericApiResponse getTokenUsingCredentials(@RequestBody UserCredentials userCredentials) throws Exception
  {

    LOG.info("Get token method called.");

    TokenDto responseToken = keyClockService.getToken(userCredentials);

    LOG.info("Get token method finished.");

    return GenericApiResponse.builder()
      .statusCode(HttpStatus.OK.value())
      .response(responseToken)
      .message("Token successfully sent.")
      .build();
  }

  /**
   * 2) When access token get expired than send refresh token to get new access
   * token. We will receive new refresh token also in this response.Update
   * client cookie with updated refresh and access token
   */
  @RequestMapping(value = "/refreshtoken", method = RequestMethod.GET)
  @ApiOperation(
    notes = "${operation2.description}",
    value = "${operation2.value}",
    responseContainer = "${operation2.responseContainer}",
    response = TokenDto.class
  )
  public GenericApiResponse getTokenUsingRefreshToken(HttpServletRequest request)
  {

    LOG.info("Get token using refresh token method started.");

    TokenDto responseToken;

    String header = request.getHeader("Authorization");
    if (header == null
      || header.equalsIgnoreCase(""))
    {
      LOG.error("Refresh token is not present in header.");
      throw new TokenNotValidException("Refresh token is not present!");
    }

    responseToken = keyClockService.getByRefreshToken(header);

    LOG.info("Get token using refresh token method finished.");
    return GenericApiResponse.builder()
      .statusCode(HttpStatus.OK.value())
      .response(responseToken)
      .message("Token successfully sent.")
      .build();

  }

  /**
   * 3) Health check, returning simple JSON object
   */
  @RequestMapping(value = "/health", method = RequestMethod.GET)
  public JSONObject getHealthCheck()
  {

    Map<String, String> map = new HashMap<>();
    map.put("HEALTH", "OK");

    return new JSONObject(map);
  }

  @GetMapping(value = "/groups")
  public ResponseEntity<List<GroupRepresentation>> getRealmGroups()
  {
    return ResponseEntity.ok(keyClockService.getRealmGroups());
  }

  @PostMapping(value = "/groups/join/{groupId}/user/{userId}")
  public ResponseEntity<Void> assignGroupToUser(@PathVariable String groupId, @PathVariable String userId)
  {
    keyClockService.joinGroup(groupId, userId);
    return ResponseEntity.ok().build();
  }

  @PostMapping(value = "/groups/find")
  public ResponseEntity<GroupRepresentation> findGroupByPath(@RequestBody SearchDto searchDto)
  {
    return ResponseEntity.ok(keyClockService.findGroupByPath(searchDto.getTerm()));
  }
}
