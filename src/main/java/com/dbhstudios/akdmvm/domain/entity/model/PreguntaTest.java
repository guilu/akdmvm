package com.dbhstudios.akdmvm.domain.entity.model;


import com.dbhstudios.akdmvm.domain.entity.BaseEntity;
import com.dbhstudios.akdmvm.domain.entity.DomainModelNames;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = DomainModelNames.TB07_PREGUNTA_TEST)
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

    public PreguntaTest() {

    }

    public PreguntaTest(Pregunta pregunta) {
        this.pregunta = pregunta;
        this.acertada = false;
    }

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    public Pregunta getPregunta() {
        return pregunta;
    }

    public void setPregunta(Pregunta pregunta) {
        this.pregunta = pregunta;
    }

    public boolean isAcertada() {
        return acertada;
    }

    public void setAcertada(boolean acertada) {
        this.acertada = acertada;
    }

    public String toString() {
        String json = pregunta.toString();
        return "{" + json + "}";
    }
}
