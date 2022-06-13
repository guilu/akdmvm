package com.dbhstudios.akdmvm.auth.domain.repository;

import com.dbhstudios.akdmvm.auth.domain.model.Provider;
import com.dbhstudios.akdmvm.auth.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find by email.
     *
     * @param email the email
     * @return the user
     */
    User findByEmail(String email);

    /**
     * Delete.
     *
     * @param user the user
     */
    @Override
    void delete(User user);

    @Modifying
    @Query("UPDATE User u SET u.provider = ?2 WHERE u.email = ?1")
    void updateAuthenticationType(String username, Provider authType);
}