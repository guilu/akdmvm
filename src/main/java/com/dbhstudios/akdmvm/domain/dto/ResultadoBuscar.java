package com.dbhstudios.akdmvm.domain.dto;

import com.dbhstudios.akdmvm.domain.entity.model.Agrupacion;
import com.dbhstudios.akdmvm.domain.entity.model.Pregunta;
import com.dbhstudios.akdmvm.domain.entity.model.Respuesta;
import com.dbhstudios.akdmvm.domain.entity.model.Tema;

import java.util.List;

public class ResultadoBuscar {

    private int numAgrupaciones;
    private int numTemas;
    private int numPreguntas;
    private int numRespuestas;

    private List<Agrupacion> agrupaciones;
    private List<Tema> temas;
    private List<Pregunta> preguntas;
    private List<Respuesta> respuestas;

    public ResultadoBuscar() {
    }

    public ResultadoBuscar(int numAgrupaciones, int numTemas, int numPreguntas, int numRespuestas, List<Agrupacion> agrupaciones, List<Tema> temas, List<Pregunta> preguntas, List<Respuesta> respuestas) {
        this.numAgrupaciones = numAgrupaciones;
        this.numTemas = numTemas;
        this.numPreguntas = numPreguntas;
        this.numRespuestas = numRespuestas;
        this.agrupaciones = agrupaciones;
        this.temas = temas;
        this.preguntas = preguntas;
        this.respuestas = respuestas;
    }

    public int getNumAgrupaciones() {
        return numAgrupaciones;
    }

    public void setNumAgrupaciones(int numAgrupaciones) {
        this.numAgrupaciones = numAgrupaciones;
    }

    public int getNumTemas() {
        return numTemas;
    }

    public void setNumTemas(int numTemas) {
        this.numTemas = numTemas;
    }

    public int getNumPreguntas() {
        return numPreguntas;
    }

    public void setNumPreguntas(int numPreguntas) {
        this.numPreguntas = numPreguntas;
    }

    public int getNumRespuestas() {
        return numRespuestas;
    }

    public void setNumRespuestas(int numRespuestas) {
        this.numRespuestas = numRespuestas;
    }

    public List<Agrupacion> getAgrupaciones() {
        return agrupaciones;
    }

    public void setAgrupaciones(List<Agrupacion> agrupaciones) {
        this.agrupaciones = agrupaciones;
    }

    public List<Tema> getTemas() {
        return temas;
    }

    public void setTemas(List<Tema> temas) {
        this.temas = temas;
    }

    public List<Pregunta> getPreguntas() {
        return preguntas;
    }

    public void setPreguntas(List<Pregunta> preguntas) {
        this.preguntas = preguntas;
    }

    public List<Respuesta> getRespuestas() {
        return respuestas;
    }

    public void setRespuestas(List<Respuesta> respuestas) {
        this.respuestas = respuestas;
    }


    @Override
    public String toString() {
        return "ResultadoBuscar{" +
                "numAgrupaciones=" + numAgrupaciones +
                ", numTemas=" + numTemas +
                ", numPreguntas=" + numPreguntas +
                ", numRespuestas=" + numRespuestas +
                '}';
    }
}
