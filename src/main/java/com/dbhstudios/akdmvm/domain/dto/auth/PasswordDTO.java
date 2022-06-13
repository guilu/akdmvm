package com.dbhstudios.akdmvm.domain.dto.auth;

import lombok.Data;

/**
 * A new password dto. This object is used for password form actions (change password, forgot password token, save
 * password, etc..).
 */
@Data
public class PasswordDTO {

        /**
         * The old password.
         */
        private String oldPassword;

        /**
         * The new password.
         */
        private String newPassword;

        /**
         * The token.
         */
        private String token;


}
