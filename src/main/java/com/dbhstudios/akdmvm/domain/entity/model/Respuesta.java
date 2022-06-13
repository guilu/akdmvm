package com.dbhstudios.akdmvm.domain.entity.model;

import com.dbhstudios.akdmvm.domain.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "TB04_RESPUESTA", schema = "BDD_AKDMVM")
public class Respuesta extends BaseEntity {

    private static final long serialVersionUID = 4L;

    @ManyToOne
    @JoinColumn(name = "PREGUNTA_ID", referencedColumnName = "ID")
    @JsonIgnore
    private Pregunta pregunta;

    @Size(max = 1024)
    @Column
    private String texto;
    @Column
    private boolean correcta;

    public Respuesta(){

    }
    public Respuesta(String texto, boolean correcta, Pregunta pregunta){
        this.texto = texto;
        this.correcta = correcta;
        this.pregunta = pregunta;
    }
    
    public Pregunta getPregunta() {
        return pregunta;
    }

    public void setPregunta(Pregunta pregunta) {
        this.pregunta = pregunta;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public boolean isCorrecta() {
        return correcta;
    }

    public void setCorrecta(boolean correcta) {
        this.correcta = correcta;
    }

}
