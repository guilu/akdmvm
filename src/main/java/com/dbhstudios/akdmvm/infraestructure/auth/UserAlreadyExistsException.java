package com.dbhstudios.akdmvm.infraestructure.auth;

import java.io.Serial;

/**
 * The Class UserAlreadyExistException.
 */
public class UserAlreadyExistsException extends RuntimeException{

        /**
         * The Constant serialVersionUID.
         */
        @Serial
        private static final long serialVersionUID = -1644467856346594869L;

        /**
         * Instantiates a new user already exist exception.
         */
        public UserAlreadyExistsException() {
            super();
        }

        /**
         * Instantiates a new user already exist exception.
         *
         * @param message the message
         * @param cause   the cause
         */
        public UserAlreadyExistsException(final String message, final Throwable cause) {
            super(message, cause);
        }

        /**
         * Instantiates a new user already exist exception.
         *
         * @param message the message
         */
        public UserAlreadyExistsException(final String message) {
            super(message);
        }

        /**
         * Instantiates a new user already exist exception.
         *
         * @param cause the cause
         */
        public UserAlreadyExistsException(final Throwable cause) {
            super(cause);
        }
}

