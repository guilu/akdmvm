package com.dbhstudios.akdmvm.auth.service;

import com.dbhstudios.akdmvm.domain.dto.auth.UserDetailsAK;
import com.dbhstudios.akdmvm.auth.domain.model.Privilege;
import com.dbhstudios.akdmvm.auth.domain.model.Role;
import com.dbhstudios.akdmvm.auth.domain.model.User;
import com.dbhstudios.akdmvm.auth.domain.repository.UserRepository;
import com.dbhstudios.akdmvm.util.HttpUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * The UserDetailsServiceAK extends the Spring Security UserDetailsService to use the UserDetailsAK object and to use
 * email as username.
 */
@Log4j2
@Transactional
@Service
public class UserDetailsServiceAK implements UserDetailsService {

    protected UserRepository userRepository;
    protected LoginAttemptService loginAttemptService;


    @Autowired
    public UserDetailsServiceAK(UserRepository userRepository, LoginAttemptService loginAttemptService) {
        this.userRepository = userRepository;
        this.loginAttemptService = loginAttemptService;
    }

    /**
     * Load user by email address.
     *
     * @param email the email address
     * @return the DS user details
     * @throws UsernameNotFoundException the email not found exception
     */

    public UserDetailsAK loadUserByUsername(final String email, HttpServletRequest request) throws UsernameNotFoundException {
        log.debug("DSUserDetailsService.loadUserByUsername:" + "called with username: {}", email);

        final String ip = HttpUtils.getClientIP(request);

        if (loginAttemptService.isBlocked(ip)) {
            throw new RuntimeException("blocked");
        }

        return this.loadUserByUsername(email);

    }

    @Override
    public UserDetailsAK loadUserByUsername(final String email) {

        log.debug("intento cargar el usuario con mail: {}", email);

        final User user = userRepository.findByEmail(email);

        if (user == null) {
            log.error("No user found with email/username: {}", email);
            throw new UsernameNotFoundException("No user found with email/username: " + email);
        }

        try {
            // Updating lastActivity date for this login
            user.setLastActivityDate(new Date());
            userRepository.save(user);
            UserDetailsAK userDetails = new UserDetailsAK(user, getAuthorities(user.getRoles()));
            return userDetails;

        } catch (final Exception e) {
            log.error("DSUserDetailsService.loadUserByUsername:" + "Exception!", e);
            throw new RuntimeException(e);
        }
    }


    /**
     * Gets the authorities.
     *
     * @param roles the roles
     * @return the authorities
     */
    private Collection<? extends GrantedAuthority> getAuthorities(final Collection<Role> roles) {
        return getGrantedAuthorities(getPrivileges(roles));
    }

    /**
     * Gets the privileges.
     *
     * @param roles the roles
     * @return the privileges
     */
    private List<String> getPrivileges(final Collection<Role> roles) {
        final List<String> privileges = new ArrayList<>();
        final List<Privilege> collection = new ArrayList<>();
        for (final Role role : roles) {
            privileges.add(role.getName());
            collection.addAll(role.getPrivileges());
        }
        for (final Privilege item : collection) {
            privileges.add(item.getName());
        }

        return privileges;
    }

    /**
     * Gets the granted authorities.
     *
     * @param privileges the privileges
     * @return the granted authorities
     */
    private List<GrantedAuthority> getGrantedAuthorities(final List<String> privileges) {
        final List<GrantedAuthority> authorities = new ArrayList<>();
        for (final String privilege : privileges) {
            authorities.add(new SimpleGrantedAuthority(privilege));
        }
        return authorities;
    }

}