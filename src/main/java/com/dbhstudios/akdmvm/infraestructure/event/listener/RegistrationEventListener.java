package com.dbhstudios.akdmvm.infraestructure.event.listener;


import com.dbhstudios.akdmvm.infraestructure.event.OnRegistrationCompleteEvent;
import com.dbhstudios.akdmvm.application.service.auth.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * This listener handles OnRegistrationCompleteEvents, and sends a password verification email, if that feature is
 * enabled.
 *
 * @see OnRegistrationCompleteEvent
 */
@Log4j2
@Component
public class RegistrationEventListener {


    private final UserService userService;

    @Autowired
    public RegistrationEventListener(UserService userService) {
        this.userService = userService;
    }

    @Value("${user.registration.sendVerificationEmail:false}")
    private boolean sendRegistrationVerificationEmail;

    /**
     * Handles the OnRegistrationCompleteEvent. In this case calling the confirmRegistration method.
     *
     * @param event the event
     */
    @Async
    @EventListener
    public void onApplicationEvent(final OnRegistrationCompleteEvent event) {
        log.debug("RegistrationEventListener.onApplicationEvent: called with event: {}", event.toString());
        if (sendRegistrationVerificationEmail) {
            this.sendRegistrationVerificationEmail(event);
        }
    }

    /**
     * Handle the completed registration.
     * <p>
     * Create a Verification token for the user, and send the email out.
     *
     * @param event the event
     */
    private void sendRegistrationVerificationEmail(final OnRegistrationCompleteEvent event) {
        userService.sendRegistrationVerificationEmail(event.getUser(), event.getAppUrl());
    }

}
