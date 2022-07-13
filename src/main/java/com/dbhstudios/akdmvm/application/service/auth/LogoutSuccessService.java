package com.dbhstudios.akdmvm.application.service.auth;

import com.dbhstudios.akdmvm.domain.dto.auth.UserDetailsAK;
import com.dbhstudios.akdmvm.infraestructure.event.AuditEvent;
import com.dbhstudios.akdmvm.domain.entity.auth.User;
import com.dbhstudios.akdmvm.util.HttpUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * The LogoutSuccessService is called when a user logs out successfully.
 */
@Log4j2
@Service
public class LogoutSuccessService extends SimpleUrlLogoutSuccessHandler {

    private final ApplicationEventPublisher eventPublisher;
    private final String logoutSuccessUri;

    public LogoutSuccessService(ApplicationEventPublisher eventPublisher, @Value("${user.security.logoutSuccessURI}") String logoutSuccessUri) {
        this.eventPublisher = eventPublisher;
        this.logoutSuccessUri = logoutSuccessUri;
    }

    /**
     * On logout success.
     *
     * @param request        the request
     * @param response       the response
     * @param authentication the authentication
     * @throws IOException      Signals that an I/O exception has occurred.
     * @throws ServletException the servlet exception
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException,ServletException {
        log.debug("LogoutSuccessService.onLogoutSuccess:" + "called.");
        log.debug("LogoutSuccessService.onAuthenticationSuccess:" + "called with authentiation: {}", authentication);
        log.debug("LogoutSuccessService.onAuthenticationSuccess:" + "targetUrl: {}", super.determineTargetUrl(request, response));

        User user = null;
        if (authentication != null && authentication.getPrincipal() != null && authentication.getPrincipal() instanceof UserDetailsAK) {
            user = ((UserDetailsAK) authentication.getPrincipal()).getUser();
        }

        AuditEvent logoutAuditEvent = new AuditEvent(this, user, request.getSession().getId(), HttpUtils.getClientIP(request),
                request.getHeader("User-Agent"), "Logout", "Success", "Success", null);
        eventPublisher.publishEvent(logoutAuditEvent);

        String targetUrl = super.determineTargetUrl(request, response);
        if (StringUtils.isEmptyOrWhitespace(targetUrl) || StringUtils.equals(targetUrl, "/")) {
            targetUrl = logoutSuccessUri;
            this.setDefaultTargetUrl(targetUrl);
        }

        super.onLogoutSuccess(request, response, authentication);
    }
}
