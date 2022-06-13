package com.dbhstudios.akdmvm.auth.infraestructure.controller;

import com.dbhstudios.akdmvm.domain.dto.auth.UserDTO;
import com.dbhstudios.akdmvm.domain.dto.auth.UserDetailsAK;
import com.dbhstudios.akdmvm.auth.domain.model.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class AuthenticationFrontEndController {


    @GetMapping("/login")
    public String login(){
        return "auth/login";
    }


    @GetMapping("/register")
    public String register(){
        return "auth/register";
    }

    @GetMapping("/profile")
    public String profile(){
        return "auth/profile";
    }

    
    @GetMapping("/registration-pending-verification")
    public String RegistrationPendingVerification(){
        return "auth/registration-pending-verification";
    }


    /**
     * Registration complete.
     *
     * @return the string
     */
    @GetMapping("/registration-complete")
    public String registrationComplete() {
        return "auth/registration-complete";
    }

    /**
     * Request new verification E mail.
     *
     * @return the string
     */
    @GetMapping("/request-new-verification-email")
    public String requestNewVerificationEMail() {
        return "auth/request-new-verification-email";
    }

    /**
     * Forgot password.
     *
     * @return the string
     */
    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "auth/forgot-password";
    }

    /**
     * Forgot password pending verification.
     *
     * @return the string
     */
    @GetMapping("/forgot-password-pending-verification")
    public String forgotPasswordPendingVerification() {
        return "auth/forgot-password-pending-verification";
    }

    /**
     * Forgot password change.
     *
     * @return the string
     */
    @GetMapping("/forgot-password-change")
    public String forgotPasswordChange() {
        return "auth/forgot-password-change";
    }

    @GetMapping("/update-user")
    public String updateUser(@AuthenticationPrincipal UserDetailsAK userDetails, final HttpServletRequest request, final ModelMap model) {
        if (userDetails != null) {
            User user = userDetails.getUser();
            UserDTO userDto = new UserDTO();
            userDto.setFirstName(user.getFirstName());
            userDto.setLastName(user.getLastName());
            model.addAttribute("user", userDto);
        }

        return "auth/update-user";
    }

    @GetMapping("/update-password")
    public String updatePassword() {
        return "auth/update-password";
    }

}
