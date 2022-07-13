package com.dbhstudios.akdmvm;

import com.dbhstudios.akdmvm.domain.respository.auth.UserRepository;
import com.dbhstudios.akdmvm.application.service.auth.LoginAttemptService;
import com.dbhstudios.akdmvm.application.service.auth.LogoutSuccessService;
import com.dbhstudios.akdmvm.application.service.auth.UserDetailsServiceAK;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.ArrayList;
import java.util.Arrays;

@Log4j2
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {

    private static final String DEFAULT_ACTION_DENY = "deny";
    private static final String DEFAULT_ACTION_ALLOW = "allow";

    @Value("${user.security.defaultAction}")
    private String defaultAction;

    @Value("#{'${user.security.protectedURIs}'.split(',')}")
    private String[] protectedURIsArray;

    @Value("#{'${user.security.unprotectedURIs}'.split(',')}")
    private String[] unprotectedURIsArray;

    @Value("#{'${user.security.disableCSRFdURIs}'.split(',')}")
    private String[] disableCSRFURIsArray;


    @Value("${user.security.logoutSuccessURI}")
    private String logoutSuccessURI;

    @Value("${user.security.loginURI}")
    private String loginURI;

    @Value("${user.security.logoutURI}")
    private String logoutRI;



    //injecciones
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LoginAttemptService loginAttemptService;
    @Autowired
    private ApplicationEventPublisher eventPublisher;



    @Bean
    @Order(1)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        log.debug("SecurityConfiguration.filterChain:" + "unprotectedURIs: {}", Arrays.toString(unprotectedURIsArray));
        log.debug("SecurityConfiguration.filterChain:" + "protectedURIs: {}", Arrays.toString(protectedURIsArray));
        log.debug("SecurityConfiguration.filterChain:" + "defaultAction: {}", defaultAction);
        log.debug("SecurityConfiguration.filterChain:" + "disableCSRFdURIs: {}", Arrays.toString(disableCSRFURIsArray));

        ArrayList<String> unprotectedURIs = new ArrayList<>(Arrays.asList(unprotectedURIsArray));


        ArrayList<String> disableCSRFURIs = new ArrayList<>(Arrays.asList(disableCSRFURIsArray));
        //remove empty strings and null
        disableCSRFURIs.removeAll(Arrays.asList("", null));
        //ALERT: don't enable this in production!!
        if (disableCSRFURIs.size() > 0) {
            http.csrf().ignoringAntMatchers(disableCSRFURIs.toArray(new String[0]));
            http.headers().frameOptions().disable(); //required for h2-console
        }


        //LEVEL OF AUTHORIZATION
        if (defaultAction.equals(DEFAULT_ACTION_ALLOW)) {

            http
                    .authorizeHttpRequests((authz) -> authz
                            .anyRequest().permitAll()
                    );

        } else if (defaultAction.equals(DEFAULT_ACTION_DENY)) {

            http
                    .authorizeHttpRequests((authz) -> authz
                            .antMatchers(unprotectedURIsArray).permitAll()
                            .anyRequest().authenticated()

                    );
        } else {
            log.error("SecurityConfiguration.filterChain: user.security.defaultAction must be set to either {} or {}!!!  Denying access to all resources to force intentional configuration.", DEFAULT_ACTION_ALLOW, DEFAULT_ACTION_DENY);
            http.authorizeHttpRequests().anyRequest().denyAll();
        }


        //login sequence
        http
                .formLogin(form -> form
                        .loginPage(loginURI).permitAll())
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher(logoutRI,"GET")).permitAll()
                        .logoutSuccessHandler(new LogoutSuccessService(eventPublisher,logoutSuccessURI))
                );


        return http.build();
    }


    @Bean
    public DaoAuthenticationProvider authProvider(){
        final DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(new UserDetailsServiceAK(userRepository,loginAttemptService));
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }


    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(11);
    }

    @Bean
    SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

}
