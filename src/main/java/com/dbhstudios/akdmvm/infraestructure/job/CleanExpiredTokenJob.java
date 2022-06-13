package com.dbhstudios.akdmvm.infraestructure.job;

import com.dbhstudios.akdmvm.auth.domain.repository.PasswordResetTokenRepository;
import com.dbhstudios.akdmvm.auth.domain.repository.VerificationTokenRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;

/**
 * The ExpiredTokenCleanJob is a Service which purges expired registration email verification tokens and password reset
 * tokens based on the schedule defined in user.purgetokens.cron.expression in your application.properties.
 */
@Log4j2
@Service
@Transactional
public class CleanExpiredTokenJob {

    /**
     * The registration email verificaiton token repository.
     */
    @Autowired
    VerificationTokenRepository verificaitonTokenRepository;

    /**
     * The password reset token repository.
     */
    @Autowired
    PasswordResetTokenRepository passwordTokenRepository;

    /**
     * Purge expired.
     */
    @Scheduled(cron = "${user.purgetokens.cron.expression}")
    public void purgeExpired() {
        log.info("CleanExpiredTokenJob.purgeExpired:" + "running....");
        Date now = Date.from(Instant.now());

        passwordTokenRepository.deleteAllExpiredSince(now);
        verificaitonTokenRepository.deleteAllExpiredSince(now);
        log.info("CleanExpiredTokenJob.purgeExpired:" + "all expired tokens have been deleted.");
    }
}

