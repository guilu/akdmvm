package com.dbhstudios.akdmvm.domain.dto;

import javax.validation.constraints.NotEmpty;

public class NuevoTemaDTO {

    long idAgrupacion;
    @NotEmpty(message = "Texto no puede ser vacía.")
    String textoTema;

    public NuevoTemaDTO() {
    }

    public NuevoTemaDTO(long idAgrupacion, String textoTema) {
        this.idAgrupacion = idAgrupacion;
        this.textoTema = textoTema;
    }

    public long getIdAgrupacion() {
        return idAgrupacion;
    }

    public void setIdAgrupacion(long idAgrupacion) {
        this.idAgrupacion = idAgrupacion;
    }

    public String getTextoTema() {
        return textoTema;
    }

    public void setTextoTema(String textoTema) {
        this.textoTema = textoTema;
    }
}
