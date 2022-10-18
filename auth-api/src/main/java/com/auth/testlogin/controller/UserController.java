package com.auth.testlogin.controller;

import com.auth.testlogin.config.ApiResponse;
import com.auth.testlogin.exceptions.TokenNotValidException;
import com.auth.testlogin.exceptions.WrongUserCredentialsException;
import com.auth.testlogin.model.dto.ForgotPasswordDto;
import com.auth.testlogin.model.dto.ResetPasswordDto;
import com.auth.testlogin.service.KeyCloakService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

import static com.auth.testlogin.controller.KeycloakController.PASSWORD_PATTERN;

/**
 * User controller for Adopt a pet project
 * Acceptance criteria:
 * 1)user logout
 * 2)user update password
 */
@RestController
@RequestMapping(value = "/user")
public class UserController
{

  private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

  @Autowired
  private KeyCloakService keyCloakService;

  @Autowired
  private RestTemplate restTemplate;

  /**
   * 1) Logout route logs out user from application.
   * Requires JWT token in header.
   */
  @RequestMapping(value = "/logout", method = RequestMethod.GET)
  @ApiOperation(
    notes = "${operation3.description}",
    value = "${operation3.value}"
  )
  public ApiResponse logoutUser(HttpServletRequest request)
  {

    LOG.info("Logout user started.");

    if (request == null)
    {
      throw new WrongUserCredentialsException("Bad request!");
    }

    String header = request.getHeader("Authorization");

    if (header == null)
    {
      LOG.error("Refresh token is not present in header.");
      throw new TokenNotValidException("Refresh token not provided!");
    }

    keyCloakService.logoutUser(header);

    LOG.info("Logout user finished.");

    return new ApiResponse("Hi!, you have logged out successfully!");

  }

  /**
   * 2) Update password route updates existing password for user.
   * Requires userId, new password, confirm password
   */
  @RequestMapping(value = "/update/password", method = RequestMethod.POST)
  @ApiOperation(
    notes = "${operation4.description}",
    value = "${operation4.value}"
  )
  public ApiResponse updatePassword(@ApiParam(value = "UserId", required = true)
                                    @RequestBody ResetPasswordDto resetPasswordDto,
                                    HttpServletRequest request)
  {

    LOG.info("Update user password started.");

    if (request == null)
    {
      throw new WrongUserCredentialsException("Bad request!");
    }
    if (resetPasswordDto == null)
    {
      LOG.error("Body is null.");
      throw new WrongUserCredentialsException("Body is not present!");
    }
    if (!resetPasswordDto.getPassword().matches(PASSWORD_PATTERN))
    {
      LOG.error("Password is not valid.");
      throw new WrongUserCredentialsException("Password is not valid!");
    }
    if (!resetPasswordDto.getPassword().equals(resetPasswordDto.getConfirm()))
    {
      LOG.error("Password are not same.");
      throw new WrongUserCredentialsException("Provided passwords are not the same!");
    }

    String jwtToken = request.getHeader("Authorization").substring(7);

    keyCloakService.resetPasswordFromAdmin(resetPasswordDto, jwtToken);

    return new ApiResponse("Your password has been successfully updated!");

  }

  @PostMapping("/forgotPassword")
  public ApiResponse forgotPassword(@RequestBody ForgotPasswordDto forgotPasswordDto) throws Exception
  {
    if (forgotPasswordDto == null)
    {
      LOG.error("Body is null.");
      throw new WrongUserCredentialsException("Body is not present!");
    }
    if (!forgotPasswordDto.getPassword().matches(PASSWORD_PATTERN))
    {
      LOG.error("Password is not valid.");
      throw new WrongUserCredentialsException("Password is not valid!");
    }
    if (!forgotPasswordDto.getPassword().equals(forgotPasswordDto.getConfirm()))
    {
      LOG.error("Password are not same.");
      throw new WrongUserCredentialsException("Provided passwords are not the same!");
    }

    String responseMessage = keyCloakService.forgotPasswordReset(forgotPasswordDto);

    return new ApiResponse(responseMessage);
  }
}
