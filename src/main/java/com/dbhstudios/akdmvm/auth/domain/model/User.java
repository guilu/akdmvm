package com.dbhstudios.akdmvm.auth.domain.model;

import com.dbhstudios.akdmvm.domain.entity.BaseEntity;
import com.dbhstudios.akdmvm.domain.entity.model.Tema;
import com.dbhstudios.akdmvm.domain.entity.model.Test;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity(name = "User")
@EntityListeners(AuditingEntityListener.class)
@Table(schema = "BDD_AKDMVM", name = "TB00_USER")
public class User extends BaseEntity {
    /**
     * The first name.
     */
    private String firstName;

    /**
     * The last name.
     */
    private String lastName;

    /**
     * The email.
     */
    @NotNull(message = "{validation.user.email.notnull}")
    private String email;

    /**
     * The password.
     */
    @Column(length = 60)
    @NotNull(message = "{validation.user.password.notnull}")
    private String password;

    /**
     * The enabled.
     */
    private boolean enabled;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    /**
     * The registration date.
     */
    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date registrationDate;

    /**
     * The last activity date.
     */
    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastActivityDate;

    /**
     * The locked.
     */
    private boolean locked;

    /**
     * The roles.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "TB00_USER_ROLE",
            schema = "BDD_AKDMVM",
            joinColumns = @JoinColumn(name = "ID_USER", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "ID_ROLE", referencedColumnName = "id"))
    private Collection<Role> roles;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Test> tests;


    /**
     * Sets the last activity date.
     */
    @PreUpdate
    public void setLastActivityDate() {
        this.setLastActivityDate(new Date());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}