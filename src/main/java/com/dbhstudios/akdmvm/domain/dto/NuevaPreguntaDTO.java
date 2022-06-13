package com.dbhstudios.akdmvm.domain.dto;

public class NuevaPreguntaDTO {

    int idTema;
    String textoPregunta;
    String texto1Respuesta;
    String texto2Respuesta;
    String texto3Respuesta;
    String texto4Respuesta;

    int respuestaCorrecta;

    public NuevaPreguntaDTO() {

    }

    public NuevaPreguntaDTO(int idTema, String textoPregunta, String texto1Respuesta, String texto2Respuesta, String texto3Respuesta, String texto4Respuesta, int respuestaCorrecta) {
        this.idTema = idTema;
        this.textoPregunta = textoPregunta;
        this.texto1Respuesta = texto1Respuesta;
        this.texto2Respuesta = texto2Respuesta;
        this.texto3Respuesta = texto3Respuesta;
        this.texto4Respuesta = texto4Respuesta;
        this.respuestaCorrecta = respuestaCorrecta;
    }

    public int getIdTema() {
        return idTema;
    }

    public void setIdTema(int idTema) {
        this.idTema = idTema;
    }

    public String getTextoPregunta() {
        return textoPregunta;
    }

    public void setTextoPregunta(String textoPregunta) {
        this.textoPregunta = textoPregunta;
    }

    public String getTexto1Respuesta() {
        return texto1Respuesta;
    }

    public void setTexto1Respuesta(String texto1Respuesta) {
        this.texto1Respuesta = texto1Respuesta;
    }

    public String getTexto2Respuesta() {
        return texto2Respuesta;
    }

    public void setTexto2Respuesta(String texto2Respuesta) {
        this.texto2Respuesta = texto2Respuesta;
    }

    public String getTexto3Respuesta() {
        return texto3Respuesta;
    }

    public void setTexto3Respuesta(String texto3Respuesta) {
        this.texto3Respuesta = texto3Respuesta;
    }

    public String getTexto4Respuesta() {
        return texto4Respuesta;
    }

    public void setTexto4Respuesta(String texto4Respuesta) {
        this.texto4Respuesta = texto4Respuesta;
    }


    public int getRespuestaCorrecta() {
        return respuestaCorrecta;
    }

    public void setRespuestaCorrecta(int respuestaCorrecta) {
        this.respuestaCorrecta = respuestaCorrecta;
    }
}
