package com.dbhstudios.akdmvm.auth.service;


import com.dbhstudios.akdmvm.auth.domain.model.*;
import com.dbhstudios.akdmvm.domain.dto.auth.UserDTO;
import com.dbhstudios.akdmvm.domain.dto.auth.UserDetailsAK;
import com.dbhstudios.akdmvm.infraestructure.event.AuditEvent;
import com.dbhstudios.akdmvm.infraestructure.auth.UserAlreadyExistsException;
import com.dbhstudios.akdmvm.auth.domain.repository.PasswordResetTokenRepository;
import com.dbhstudios.akdmvm.auth.domain.repository.RoleRepository;
import com.dbhstudios.akdmvm.auth.domain.repository.UserRepository;
import com.dbhstudios.akdmvm.auth.domain.repository.VerificationTokenRepository;
import com.dbhstudios.akdmvm.service.mail.MailService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The UserService provides a lot of the logic for this framework.
 */
@Log4j2
@Service
@Transactional
public class UserService extends DefaultOAuth2UserService {

    private static final String USER_ROLE_NAME = "ROLE_USER";

    /**
     * The Constant TOKEN_INVALID.
     */
    public static final String TOKEN_INVALID = "invalidToken";

    /**
     * The Constant TOKEN_EXPIRED.
     */
    public static final String TOKEN_EXPIRED = "expired";

    /**
     * The Constant TOKEN_VALID.
     */
    public static final String TOKEN_VALID = "valid";


    /**
     * The user repository.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * The token repository.
     */
    @Autowired
    private VerificationTokenRepository tokenRepository;

    /**
     * The password token repository.
     */
    @Autowired
    private PasswordResetTokenRepository passwordTokenRepository;

    /**
     * The role repository.
     */
    @Autowired
    private RoleRepository roleRepository;

    /**
     * The session registry.
     */
    @Autowired
    private SessionRegistry sessionRegistry;

    /**
     * The event publisher.
     */
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    /**
     * The mail service.
     */
    @Autowired
    public MailService mailService;

    /**
     * The ds user details service.
     */
    @Autowired
    private UserDetailsServiceAK userDetailsServiceAK;

    /**
     * The send registration verification email.
     */
    @Value("${user.registration.sendVerificationEmail:false}")
    private boolean sendRegistrationVerificationEmail;

    private PasswordEncoder passwordEncoder;

    public UserService(PasswordEncoder encoder) {
        this.passwordEncoder = encoder;
    }

    /**
     * Register new user account.
     *
     * @param newUserDto the new user dto
     * @return the user
     */
    public User registerNewUserAccount(final UserDTO newUserDto) {
        log.debug("UserService.registerNewUserAccount: called with userDto: {}", newUserDto);

        if (emailExists(newUserDto.getEmail())) {
            log.debug("UserService.registerNewUserAccount:" + "email already exists: {}", newUserDto.getEmail());
            throw new UserAlreadyExistsException("There is an account with that email address: " + newUserDto.getEmail());
        }

        // Create a new User entity
        final User user = new User();
        user.setFirstName(newUserDto.getFirstName());
        user.setLastName(newUserDto.getLastName());
        user.setPassword(passwordEncoder.encode(newUserDto.getPassword()));
        user.setEmail(newUserDto.getEmail());
        user.setRoles(Arrays.asList(roleRepository.findByName(USER_ROLE_NAME)));

        // If we are not sending a verification email
        if (!sendRegistrationVerificationEmail) {
            // Enable the user immediately
            user.setEnabled(true);
        }
        return userRepository.save(user);
    }

    /**
     * Gets the user by verification token.
     *
     * @param verificationToken the verification token
     * @return the user by verification token
     */
    public User getUserByVerificationToken(final String verificationToken) {
        log.debug("UserService.getUserByVerificationToken: called with token: {}", verificationToken);
        final VerificationToken token = tokenRepository.findByToken(verificationToken);
        if (token != null) {
            log.debug("UserService.getUserByVerificationToken: user found: {}", token.getUser());
            return token.getUser();
        }
        log.debug("UserService.getUserByVerificationToken: no user found!");
        return null;
    }

    /**
     * Gets the verification token.
     *
     * @param VerificationToken the verification token
     * @return the verification token
     */
    public VerificationToken getVerificationToken(final String VerificationToken) {
        return tokenRepository.findByToken(VerificationToken);
    }

    /**
     * Save registered user.
     *
     * @param user the user
     */
    public void saveRegisteredUser(final User user) {
        userRepository.save(user);
    }

    /**
     * Delete user.
     *
     * @param user the user
     */
    public void deleteUser(final User user) {
        final VerificationToken verificationToken = tokenRepository.findByUser(user);

        if (verificationToken != null) {
            tokenRepository.delete(verificationToken);
        }

        final PasswordResetToken passwordToken = passwordTokenRepository.findByUser(user);

        if (passwordToken != null) {
            passwordTokenRepository.delete(passwordToken);
        }
        userRepository.delete(user);
    }

    /**
     * Creates the verification token for user.
     *
     * @param user  the user
     * @param token the token
     */
    public void createVerificationTokenForUser(final User user, final String token) {
        final VerificationToken myToken = new VerificationToken(token, user);
        tokenRepository.save(myToken);
    }

    /**
     * Generate new verification token.
     *
     * @param existingVerificationToken the existing verification token
     * @return the verification token
     */
    public VerificationToken generateNewVerificationToken(final String existingVerificationToken) {
        VerificationToken vToken = tokenRepository.findByToken(existingVerificationToken);
        vToken.updateToken(UUID.randomUUID().toString());
        vToken = tokenRepository.save(vToken);
        return vToken;
    }

    /**
     * Creates the password reset token for user.
     *
     * @param user  the user
     * @param token the token
     */
    public void createPasswordResetTokenForUser(final User user, final String token) {
        final PasswordResetToken myToken = new PasswordResetToken(token, user);
        passwordTokenRepository.save(myToken);
    }

    /**
     * Find user by email.
     *
     * @param email the email
     * @return the user
     */
    public User findUserByEmail(final String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Gets the password reset token.
     *
     * @param token the token
     * @return the password reset token
     */
    public PasswordResetToken getPasswordResetToken(final String token) {
        return passwordTokenRepository.findByToken(token);
    }

    /**
     * Gets the user by password reset token.
     *
     * @param token the token
     * @return the user by password reset token
     */
    public Optional<User> getUserByPasswordResetToken(final String token) {
        return Optional.ofNullable(passwordTokenRepository.findByToken(token).getUser());
    }

    /**
     * Gets the user by ID.
     *
     * @param id the id
     * @return the user by ID
     */
    public Optional<User> getUserByID(final long id) {
        return userRepository.findById(id);
    }

    /**
     * Change user password.
     *
     * @param user     the user
     * @param password the password
     */
    public void changeUserPassword(final User user, final String password) {
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    /**
     * Check if valid old password.
     *
     * @param user        the user
     * @param oldPassword the old password
     * @return true, if successful
     */
    public boolean checkIfValidOldPassword(final User user, final String oldPassword) {
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }

    /**
     * Validate verification token.
     *
     * @param token the token
     * @return the string
     */
    public String validateVerificationToken(String token) {
        final VerificationToken verificationToken = tokenRepository.findByToken(token);
        if (verificationToken == null) {
            return TOKEN_INVALID;
        }

        final User user = verificationToken.getUser();
        final Calendar cal = Calendar.getInstance();
        if (verificationToken.getExpiryDate().before(cal.getTime())) {
            tokenRepository.delete(verificationToken);
            return TOKEN_EXPIRED;
        }

        user.setEnabled(true);
        userRepository.save(user);
        return TOKEN_VALID;
    }

    /**
     * Delete verification token.
     *
     * @param token the token
     */
    public void deleteVerificationToken(final String token) {
        log.debug("UserService.deleteVerificationToken:" + "called with token: {}", token);
        final VerificationToken verificationToken = tokenRepository.findByToken(token);
        if (verificationToken != null) {
            tokenRepository.delete(verificationToken);
            log.debug("UserService.deleteVerificationToken:" + "token deleted.");
        } else {
            log.debug("UserService.deleteVerificationToken:" + "token not found.");
        }
    }

    /**
     * Email exists.
     *
     * @param email the email
     * @return true, if successful
     */
    private boolean emailExists(final String email) {
        return userRepository.findByEmail(email) != null;
    }

    /**
     * Validate password reset token.
     *
     * @param token the token
     * @return the string
     */
    public String validatePasswordResetToken(String token) {
        final PasswordResetToken passToken = passwordTokenRepository.findByToken(token);
        if (passToken == null) {
            return TOKEN_INVALID;
        }
        final Calendar cal = Calendar.getInstance();
        if (passToken.getExpiryDate().before(cal.getTime())) {
            passwordTokenRepository.delete(passToken);
            return TOKEN_EXPIRED;
        }
        return TOKEN_VALID;
    }

    /**
     * Gets the users from session registry.
     *
     * @return the users from session registry
     */
    public List<String> getUsersFromSessionRegistry() {
        return sessionRegistry.getAllPrincipals().stream()
                .filter((u) -> !sessionRegistry.getAllSessions(u, false).isEmpty()).map(o -> {
                    if (o instanceof User) {
                        return ((User) o).getEmail();
                    } else {
                        return o.toString();
                    }
                }).collect(Collectors.toList());
    }

    /**
     * Auth without password.
     *
     * @param user the user
     */
    public void authWithoutPassword(User user, HttpServletRequest request) {
        List<Privilege> privileges = user.getRoles().stream().map(Role::getPrivileges).flatMap(Collection::stream)
                .distinct().collect(Collectors.toList());

        List<GrantedAuthority> authorities = privileges.stream().map(p -> new SimpleGrantedAuthority(p.getName()))
                .collect(Collectors.toList());

        UserDetailsAK userDetails = userDetailsServiceAK.loadUserByUsername(user.getEmail(), request);

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * Handle the completed registration.
     * <p>
     * Create a Verification token for the user, and send the email out.
     *
     * @param user   the user
     * @param appUrl the app url
     */
    public void sendRegistrationVerificationEmail(final User user, final String appUrl) {
        final String token = UUID.randomUUID().toString();
        createVerificationTokenForUser(user, token);

        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("token", token);
        variables.put("appUrl", appUrl);
        variables.put("confirmationUrl", appUrl + "/auth/registrationConfirm?token=" + token);
        variables.put("user", user);

        mailService.sendTemplateMessage(user.getEmail(), "Registration Confirmation", variables,
                "mail/registration-token.html");
    }

    /**
     * Send forgot password verification email.
     *
     * @param user   the user
     * @param appUrl the app url
     */
    public void sendForgotPasswordVerificaitonEmail(final User user, final String appUrl) {
        final String token = UUID.randomUUID().toString();
        createPasswordResetTokenForUser(user, token);

        AuditEvent sendForgotPasswordEmailAuditEvent = new AuditEvent(this, user, "", "", "",
                "sendForgotPasswordVerificaitonEmail", "Success", "Forgot password email to be sent.", null);
        eventPublisher.publishEvent(sendForgotPasswordEmailAuditEvent);

        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("token", token);
        variables.put("appUrl", appUrl);
        variables.put("confirmationUrl", appUrl + "/user/changePassword?token=" + token);
        variables.put("user", user);

        mailService.sendTemplateMessage(user.getEmail(), "Password Reset", variables,
                "mail/forgot-password-token.html");
    }



    public void updateAuthenticationType(String username, String oauth2ClientName) {
        Provider authType = Provider.valueOf(oauth2ClientName.toUpperCase());
        userRepository.updateAuthenticationType(username, authType);
    }
}

