package com.dbhstudios.akdmvm.domain.entity.auth;

import com.dbhstudios.akdmvm.domain.entity.BaseEntity;
import com.dbhstudios.akdmvm.domain.entity.DomainModelNames;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = DomainModelNames.TB00_VERIFICATION_TOKEN)
public class VerificationToken extends BaseEntity {
    /**
     * The Constant EXPIRATION.
     */
    private static final int EXPIRATION = 60 * 24;

    /**
     * The token.
     */
    private String token;

    /**
     * The user.
     */
    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(
            nullable = false,
            name = "ID_USER",
            foreignKey = @ForeignKey(name = "FK_VERIFY_USER")
    )
    private User user;

    /**
     * The expiry date.
     */
    private Date expiryDate;

    /**
     * Instantiates a new verification token.
     */
    public VerificationToken() {
        super();
    }

    /**
     * Instantiates a new verification token.
     *
     * @param token the token
     */
    public VerificationToken(final String token) {
        super();
        this.token = token;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }

    /**
     * Instantiates a new verification token.
     *
     * @param token the token
     * @param user  the user
     */
    public VerificationToken(final String token, final User user) {
        super();
        this.token = token;
        this.user = user;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }

    /**
     * Calculate expiry date.
     *
     * @param expiryTimeInMinutes the expiry time in minutes
     * @return the date
     */
    private Date calculateExpiryDate(final int expiryTimeInMinutes) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(new Date().getTime());
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }

    /**
     * Update token.
     *
     * @param token the token
     */
    public void updateToken(final String token) {
        this.token = token;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }

}
