package com.dbhstudios.akdmvm.application.service.auth;


import com.dbhstudios.akdmvm.domain.dto.auth.UserDetailsAK;
import com.dbhstudios.akdmvm.infraestructure.event.AuditEvent;
import com.dbhstudios.akdmvm.domain.entity.auth.User;
import com.dbhstudios.akdmvm.util.HttpUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * The LoginSuccessService is called after a user successfully logs in.
 */
@Log4j2
@Service
public class LoginSuccessService extends SavedRequestAwareAuthenticationSuccessHandler {

    /**
     * The login success uri.
     */

    private final String loginSuccessUri;

    private final ApplicationEventPublisher eventPublisher;

    private final UserService userService;


    public LoginSuccessService(UserService userService, ApplicationEventPublisher eventPublisher, @Value("${user.security.loginSuccessURI}") String loginSuccessUri) {
        this.userService = userService;
        this.eventPublisher = eventPublisher;
        this.loginSuccessUri = loginSuccessUri;
    }

    /**
     * On authentication success. Send AuditEvent and redirect to user.security.loginSuccessURI
     *
     * @param request        the request
     * @param response       the response
     * @param authentication the authentication
     * @throws IOException      Signals that an I/O exception has occurred.
     * @throws ServletException the servlet exception
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        final String targetUrl = super.determineTargetUrl(request, response);

        log.debug("LoginSuccessService.onAuthenticationSuccess:" + "called with authentication: {}", authentication);
        log.debug("LoginSuccessService.onAuthenticationSuccess:" + "targetUrl: {}", targetUrl);

        User user = null;
        if (authentication != null && authentication.getPrincipal() != null
                && authentication.getPrincipal() instanceof UserDetailsAK) {
            user = ((UserDetailsAK) authentication.getPrincipal()).getUser();
            userService.updateAuthenticationType(user.getEmail(), "database");
        }


        AuditEvent loginAuditEvent = new AuditEvent(this, user, request.getSession().getId(),
                HttpUtils.getClientIP(request), request.getHeader("User-Agent"), "Login", "Success", "Success", null);

        eventPublisher.publishEvent(loginAuditEvent);


        if (StringUtils.isEmptyOrWhitespace(targetUrl) || StringUtils.equals(targetUrl, "/")) {
            log.debug("la url a la que intenta redireccionar {}", this.loginSuccessUri);
            this.setDefaultTargetUrl(this.loginSuccessUri);

            log.debug("LoginSuccessService.onAuthenticationSuccess:" + "set defaultTargetUrl to: {}", this.getDefaultTargetUrl());
            //log.debug("LoginSuccessService.onAuthenticationSuccess:" + "defaultTargetParam: {}", this.getTargetUrlParameter());
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }

}