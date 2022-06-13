package com.dbhstudios.akdmvm.domain.dto;

import com.dbhstudios.akdmvm.domain.entity.model.Pregunta;

import java.util.List;

public class Simulacro {

    List<Pregunta> preguntas;

    public List<Pregunta> getPreguntas() {
        return preguntas;
    }

    public void setPreguntas(List<Pregunta> preguntas) {
        this.preguntas = preguntas;
    }
}
