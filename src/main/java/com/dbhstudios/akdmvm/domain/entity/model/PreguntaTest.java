package com.dbhstudios.akdmvm.domain.entity.model;


import com.dbhstudios.akdmvm.domain.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Collection;

@Getter
@Setter
@Entity(name = "PreguntaTest")
@EntityListeners(AuditingEntityListener.class)
@Table(schema = "BDD_AKDMVM", name = "TB07_PREGUNTA_TEST")
public class PreguntaTest extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "TEST_ID", referencedColumnName = "ID")
    @JsonIgnore
    private Test test;

    @ManyToOne
    @JoinColumn(name = "PREGUNTA_ID", referencedColumnName = "ID")
    @JsonIgnore
    private Pregunta pregunta;

    private boolean acertada;


    public PreguntaTest(Pregunta pregunta){
        this.pregunta = pregunta;
        this.acertada = false;
    }
}
