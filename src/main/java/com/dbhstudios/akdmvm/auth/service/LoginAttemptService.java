package com.dbhstudios.akdmvm.auth.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * The LoginAttemptService can be used to track successful and failed logins by IP, and block abusive IP addresses.
 */
@Service
public class LoginAttemptService {

    /**
     * The max attempt.
     */
    private final int MAX_ATTEMPT = 10;

    /**
     * The attempts cache.
     */
    private LoadingCache<String, Integer> attemptsCache;

    /**
     * Instantiates a new login attempt service.
     */
    public LoginAttemptService() {
        super();
        attemptsCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.DAYS)
                .build(new CacheLoader<String, Integer>() {
                    @Override
                    public Integer load(final String key) {
                        return 0;
                    }
                });
    }

    /**
     * Login succeeded.
     *
     * @param ipAddress the ip address of the user
     */
    public void loginSucceeded(final String ipAddress) {
        attemptsCache.invalidate(ipAddress);
    }

    /**
     * Login failed.
     *
     * @param ipAddress the ip address of the user
     */
    public void loginFailed(final String ipAddress) {
        int attempts = 0;
        try {
            attempts = attemptsCache.get(ipAddress);
        } catch (final ExecutionException e) {
            attempts = 0;
        }
        attempts++;
        attemptsCache.put(ipAddress, attempts);
    }

    /**
     * Checks if the IP address is blocked.
     *
     * @param ipAddress the ip address of the user
     * @return true, if the ip is blocked
     */
    public boolean isBlocked(final String ipAddress) {
        try {
            return attemptsCache.get(ipAddress) >= MAX_ATTEMPT;
        } catch (final ExecutionException e) {
            return false;
        }
    }
}
