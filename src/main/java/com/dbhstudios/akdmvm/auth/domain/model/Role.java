package com.dbhstudios.akdmvm.auth.domain.model;

import com.dbhstudios.akdmvm.domain.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Collection;

@Getter
@Setter
@Entity(name = "Role")
@EntityListeners(AuditingEntityListener.class)
@Table(schema = "BDD_AKDMVM", name = "TB00_ROLE")
public class Role extends BaseEntity {
    /**
     * The users.
     */
    @ManyToMany(mappedBy = "roles")
    private Collection<User> users;

    /**
     * The privileges.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "TB00_ROLE_PRIVILEGE",
            schema = "BDD_AKDMVM",
            joinColumns = @JoinColumn(name = "ID_ROLE", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "ID_PRIVILEGE", referencedColumnName = "id")
    )
    private Collection<Privilege> privileges;

    /**
     * The name.
     */
    private String name;
}