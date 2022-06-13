package com.dbhstudios.akdmvm.auth.domain.model;

import com.dbhstudios.akdmvm.domain.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.Collection;

@Getter
@Setter
@Entity(name = "Privilege")
@Table(schema = "BDD_AKDMVM", name = "TB00_PRIVILEGE")
public class Privilege extends BaseEntity {
    /**
     * The name.
     */
    private String name;

    /**
     * The roles.
     */
    @ManyToMany(mappedBy = "privileges")
    private Collection<Role> roles;
}
