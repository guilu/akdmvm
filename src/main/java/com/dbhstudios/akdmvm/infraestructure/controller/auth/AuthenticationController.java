package com.dbhstudios.akdmvm.infraestructure.controller.auth;

import com.dbhstudios.akdmvm.domain.dto.auth.PasswordDTO;
import com.dbhstudios.akdmvm.domain.dto.auth.UserDTO;
import com.dbhstudios.akdmvm.domain.dto.auth.UserDetailsAK;
import com.dbhstudios.akdmvm.infraestructure.event.AuditEvent;
import com.dbhstudios.akdmvm.infraestructure.event.OnRegistrationCompleteEvent;
import com.dbhstudios.akdmvm.infraestructure.auth.InvalidOldPasswordException;
import com.dbhstudios.akdmvm.infraestructure.auth.UserAlreadyExistsException;
import com.dbhstudios.akdmvm.domain.entity.auth.User;
import com.dbhstudios.akdmvm.application.service.auth.UserService;
import com.dbhstudios.akdmvm.util.HttpUtils;
import com.dbhstudios.akdmvm.util.JSONResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.Optional;

@Log4j2
@Controller
public class AuthenticationController {

    /**
     * The registration pending URI.
     */
    @Value("${user.security.registrationPendingURI}")
    private String registrationPendingURI;

    /**
     * The registration success URI.
     */
    @Value("${user.security.registrationSuccessURI}")
    private String registrationSuccessURI;

    /**
     * The registration new verification URI.
     */
    @Value("${user.security.registrationNewVerificationURI}")
    private String registrationNewVerificationURI;

    /**
     * The forgot password pending URI.
     */
    @Value("${user.security.forgotPasswordPendingURI}")
    private String forgotPasswordPendingURI;

    /**
     * The forgot password change URI.
     */
    @Value("${user.security.forgotPasswordChangeURI}")
    private String forgotPasswordChangeURI;



    private UserService userService;
    private MessageSource messages;
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    public AuthenticationController(UserService userService, MessageSource messages, ApplicationEventPublisher eventPublisher) {
        this.userService = userService;
        this.messages = messages;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Register a new user account.
     *
     * @param userDto the userDTO object is used for passing the form data in
     * @param request the request
     * @return A JSONResponse. In addition to success status, message, and code in the response body, this method also
     * returns a 200 status on success, a 409 status if the email address is already in use, and a 502 if there
     * is an error.
     */
    @PostMapping(value = "/auth/registration", produces = "application/json")
    public ResponseEntity<JSONResponse> registerUserAccount(@Valid final UserDTO userDto,HttpServletRequest request) {

        log.debug("Registering user account with information: {}", userDto);
        log.debug("recibida llamada con la ip {}",request.getRemoteAddr());

        User registeredUser = null;
        try {
            registeredUser = userService.registerNewUserAccount(userDto);

            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registeredUser, request.getLocale(), HttpUtils.getAppUrl(request)));
            AuditEvent registrationAuditEvent = new AuditEvent(
                    this,
                    registeredUser,
                    request.getSession().getId(),
                    HttpUtils.getClientIP(request),
                    request.getHeader("User-Agent"),
                    "Registration",
                    "Success",
                    "Registration Successful",
                    null);
            eventPublisher.publishEvent(registrationAuditEvent); //Publish AuditEven on Registration success.

        } catch (UserAlreadyExistsException uaee) {

            log.warn("UserAPI.registerUserAccount:" + "UserAlreadyExistException on registration with email: {}!", userDto.getEmail());

            AuditEvent registrationAuditEvent = new AuditEvent(
                    this,
                    registeredUser,
                    request.getSession().getId(),
                    HttpUtils.getClientIP(request),
                    request.getHeader("User-Agent"),
                    "Registration",
                    "Failure",
                    "User Already Exists",
                    null);
            eventPublisher.publishEvent(registrationAuditEvent); //Publish AuditEven User Already Exists.

            return new ResponseEntity<JSONResponse>(new JSONResponse(false, null, 02, "An account already exists for that email address!"), HttpStatus.CONFLICT);

        } catch (Exception e) {

            log.error("UserAPI.registerUserAccount:" + "Exception!", e);

            AuditEvent registrationAuditEvent = new AuditEvent(
                    this,
                    registeredUser,
                    request.getSession().getId(),
                    HttpUtils.getClientIP(request),
                    request.getHeader("User-Agent"),
                    "Registration",
                    "Failure",
                    e.getMessage(),
                    null);
            eventPublisher.publishEvent(registrationAuditEvent);  //Publish AuditEven Rgistration Exception

            return new ResponseEntity<JSONResponse>(new JSONResponse(false, null, 05, "System Error!"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // If there were no exceptions then the registration was a success!
        return new ResponseEntity<JSONResponse>( new JSONResponse(true, registrationPendingURI, 0, "Registration Successful!"), HttpStatus.OK);
    }



    /**
     * Validate a forgot password token link from an email, and if valid, show the registration success page.
     *
     * @param request the request
     * @param model   the model
     * @param token   the token
     * @return the model and view
     * @throws UnsupportedEncodingException the unsupported encoding exception
     */
    @GetMapping("/auth/registrationConfirm")
    public ModelAndView confirmRegistration(HttpServletRequest request, final ModelMap model, @RequestParam("token") final String token) throws UnsupportedEncodingException {

        log.debug("UserAPI.confirmRegistration: called with token: {}", token);
        log.debug("en el auth controller la ip es {}", request.getRemoteAddr());

        Locale locale = request.getLocale();
        model.addAttribute("lang", locale.getLanguage());
        final String result = userService.validateVerificationToken(token);

        if (result.equals("valid")) {
            final User user = userService.getUserByVerificationToken(token);
            if (user != null) {
                userService.authWithoutPassword(user, request);
                userService.deleteVerificationToken(token);

                AuditEvent registrationAuditEvent = new AuditEvent(this,
                        user,
                        request.getSession().getId(),
                        HttpUtils.getClientIP(request),
                        request.getHeader("User-Agent"),
                        "Registration Confirmation",
                        "Success",
                        "Registration Confirmed. User logged in.",
                        null);
                eventPublisher.publishEvent(registrationAuditEvent);
            }

            model.addAttribute("message", messages.getMessage("message.accountVerified", null, locale));

            log.debug("UserAPI.confirmRegistration: account verified and user logged in!");

            String redirectString = "redirect:" + registrationSuccessURI;
            return new ModelAndView(redirectString, model);
        }

        model.addAttribute("messageKey", "auth.message." + result);
        model.addAttribute("expired", "expired".equals(result));
        model.addAttribute("token", token);

        log.debug("UserAPI.confirmRegistration: failed.  Token not found or expired.");

        String redirectString = "redirect:" + registrationNewVerificationURI;
        return new ModelAndView(redirectString, model);
    }




    /**
     * Re-send registration verification token email.
     *
     * @param userDto the userDTO for passing in the email address from the form
     * @param request the request
     * @return the generic response
     */
    @PostMapping(value = "/auth/resendRegistrationToken", produces = "application/json")
    public ResponseEntity<JSONResponse> resendRegistrationToken(final UserDTO userDto,
                                                                final HttpServletRequest request) {
        log.debug("UserAPI.resendRegistrationToken:" + "email: {}", userDto.getEmail());

        // Lookup User by email
        User user = userService.findUserByEmail(userDto.getEmail());
        log.debug("UserAPI.resendRegistrationToken:" + "user: {}", user);

        // If user exists
        if (user != null) {
            // If user is enabled
            if (user.isEnabled()) {
                log.debug("UserAPI.resendRegistrationToken:" + "user is already enabled.");
                // Send response with message and recommendation to login/forgot password
                return new ResponseEntity<JSONResponse>(
                        new JSONResponse(false, null, 1, "Account is already verified."), HttpStatus.CONFLICT);
            } else {
                // Else send new token email
                log.debug("UserAPI.resendRegistrationToken:" + "sending a new verification token email.");
                String appUrl = HttpUtils.getAppUrl(request);
                userService.sendRegistrationVerificationEmail(user, appUrl);
                // Return happy path response
                AuditEvent resendRegTokenAuditEvent = new AuditEvent(this, user, request.getSession().getId(),
                        HttpUtils.getClientIP(request), request.getHeader("User-Agent"), "Resend Reg Token", "Success",
                        "Success", null);
                eventPublisher.publishEvent(resendRegTokenAuditEvent);
                return new ResponseEntity<JSONResponse>(
                        new JSONResponse(true, registrationPendingURI, 0, "Verification Email Resent Successfully!"),
                        HttpStatus.OK);
            }
        }
        // Return generic error response (don't leak too much info)
        return new ResponseEntity<JSONResponse>(new JSONResponse(false, null, 2, "System Error!"),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping(value = "/auth/updateUser", produces = "application/json")
    public ResponseEntity<JSONResponse> updateUserAccount(@AuthenticationPrincipal UserDetailsAK userDetails,
                                                          @Valid final UserDTO userDto, final HttpServletRequest request, final Locale locale) {

        log.debug("UserAPI.updateUserAccount:" + "called with userDetails: {} and  userDto: {}", userDetails, userDto);

        // If the userDetails is not available, or if the user is not logged in, log an error and return a failure.
        if (userDetails == null || SecurityContextHolder.getContext().getAuthentication() == null
                || !SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
            log.error("UserAPI.updateUserAccount:" + "updateUser called without logged in user state!");
            return new ResponseEntity<JSONResponse>(new JSONResponse(false, null, 0, "User Not Logged In!"),
                    HttpStatus.OK);
        }

        User user = userDetails.getUser();

        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        userService.saveRegisteredUser(user);

        AuditEvent userUpdateAuditEvent = new AuditEvent(this, userDetails.getUser(), request.getSession().getId(),
                HttpUtils.getClientIP(request), request.getHeader("User-Agent"), "ProfileUpdate", "Success", "Success",
                null);
        eventPublisher.publishEvent(userUpdateAuditEvent);

        return new ResponseEntity<JSONResponse>(
                new JSONResponse(true, null, 0,
                        messages.getMessage("message.updateUserSuccess", null, locale) + "<br /><br />"),
                HttpStatus.OK);

    }

    /**
     * Start of the forgot password flow. This API takes in an email address and, if the user exists, will send a
     * password reset token email to them.
     *
     * @param userDto the userDTO for passing in the email address from the form
     * @param request the request
     * @return a generic success response, so as to not leak information about accounts existing or not.
     */
    @PostMapping(value = "/auth/resetPassword", produces = "application/json")
    public ResponseEntity<JSONResponse> resetPassword(final UserDTO userDto, final HttpServletRequest request) {

        log.debug("UserAPI.resetPassword:" + "email: {}", userDto.getEmail());

        // Lookup User by email
        User user = userService.findUserByEmail(userDto.getEmail());
        log.debug("UserAPI.resendRegistrationToken:" + "user: {}", user);

        if (user != null) {
            String appUrl = HttpUtils.getAppUrl(request);
            userService.sendForgotPasswordVerificaitonEmail(user, appUrl);

            AuditEvent resetPasswordAuditEvent = new AuditEvent(this, user, request.getSession().getId(),
                    HttpUtils.getClientIP(request), request.getHeader("User-Agent"), "Reset Password", "Success",
                    "Success", null);
            eventPublisher.publishEvent(resetPasswordAuditEvent);

        } else {
            AuditEvent resetPasswordAuditEvent = new AuditEvent(this, user, request.getSession().getId(),
                    HttpUtils.getClientIP(request), request.getHeader("User-Agent"), "Reset Password", "Failure",
                    "Invalid EMail Submitted", "Email submitted: " + userDto.getEmail());
            eventPublisher.publishEvent(resetPasswordAuditEvent);
        }

        return new ResponseEntity<JSONResponse>(new JSONResponse(true, forgotPasswordPendingURI, 0,
                "If account exists, password reset email has been sent!"), HttpStatus.OK);
    }

    /**
     * Saves a new password from a password reset flow based on a password reset token.
     *
     * @param locale      the locale
     * @param passwordDto the password dto
     * @param request     the request
     * @return the generic response
     */
    @PostMapping(value = "/auth/savePassword", produces = "application/json")
    public ResponseEntity<JSONResponse> savePassword(@Valid PasswordDTO passwordDto, final HttpServletRequest request,
                                                     final Locale locale) {
        log.debug("UserAPI.savePassword:" + "called with passwordDto: {}", passwordDto);

        final String result = userService.validatePasswordResetToken(passwordDto.getToken());
        log.debug("UserAPI.savePassword:" + "result: {}", result);
        if (UserService.TOKEN_VALID.equals(result)) {
            Optional<User> user = userService.getUserByPasswordResetToken(passwordDto.getToken());
            if (user.isPresent()) {
                userService.changeUserPassword(user.get(), passwordDto.getNewPassword());
                log.debug("UserAPI.savePassword:" + "password updated!");

                AuditEvent savePasswordAuditEvent = new AuditEvent(this, user.get(), request.getSession().getId(),
                        HttpUtils.getClientIP(request), request.getHeader("User-Agent"), "Reset Save Password",
                        "Success", "Success", null);
                eventPublisher.publishEvent(savePasswordAuditEvent);

                // In this case we are returning a success, with multiple messages designed to be displayed on-page,
                // instead of a redirect URL like most of the other calls. The difference is just to provide working
                // examples of each type of response handling.
                return new ResponseEntity<JSONResponse>(new JSONResponse(true, null, 0,
                        messages.getMessage("message.resetPasswordSuccess", null, locale), "<br />",
                        "<a href='/user/login.html'>Login</a>"), HttpStatus.OK);
            } else {
                log.debug("UserAPI.savePassword:" + "user could not be found!");
                return new ResponseEntity<JSONResponse>(
                        new JSONResponse(false, null, 1, messages.getMessage("message.error", null, locale)),
                        HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<JSONResponse>(
                    new JSONResponse(false, null, 2, messages.getMessage("message.error", null, locale)),
                    HttpStatus.OK);
        }
    }

    /**
     * Updates a user's password.
     *
     * @param locale      the locale
     * @param passwordDto the password dto
     * @return the generic response
     */
    // Change user password
    @PostMapping(value = "/user/updatePassword", produces = "application/json")
    public ResponseEntity<JSONResponse> changeUserPassword(@AuthenticationPrincipal UserDetailsAK userDetails,
                                                           final Locale locale, @Valid PasswordDTO passwordDto, final HttpServletRequest request) {
        if (userDetails == null || userDetails.getUser() == null) {
            log.error("UserAPI.changeUserPassword:" + "changeUserPassword called with null userDetails or user.");
            return new ResponseEntity<JSONResponse>(
                    new JSONResponse(false, null, 2, messages.getMessage("message.error", null, locale)),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        final User user = userDetails.getUser();
        if (!userService.checkIfValidOldPassword(user, passwordDto.getOldPassword())) {
            throw new InvalidOldPasswordException();
        }
        userService.changeUserPassword(user, passwordDto.getNewPassword());

        AuditEvent updatePasswordAuditEvent = new AuditEvent(this, user, request.getSession().getId(),
                HttpUtils.getClientIP(request), request.getHeader("User-Agent"), "Update Save Password", "Success",
                "Success", null);
        eventPublisher.publishEvent(updatePasswordAuditEvent);

        return new ResponseEntity<JSONResponse>(
                new JSONResponse(true, null, 0,
                        messages.getMessage("message.updatePasswordSuccess", null, locale) + "<br /><br />"),
                HttpStatus.OK);

    }
}
