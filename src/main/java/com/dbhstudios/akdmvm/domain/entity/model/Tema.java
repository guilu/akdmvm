package com.dbhstudios.akdmvm.domain.entity.model;

import com.dbhstudios.akdmvm.domain.entity.BaseEntity;
import com.dbhstudios.akdmvm.domain.entity.DomainModelNames;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = DomainModelNames.TB03_TEMA)
public class Tema extends BaseEntity {

    private static final Logger LOG = LoggerFactory.getLogger(Tema.class);

    private static final long serialVersionUID = 2L;

    @ManyToOne
    @JoinColumn(name = "AGRUPACION_ID", referencedColumnName = "ID")
    @JsonIgnore
    private Agrupacion agrupacion;

    @Size(max = 1024)
    private String texto;

    @OneToMany(mappedBy = "tema", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Pregunta> preguntas;

    public Tema(){}

    public Tema(String texto) {
        this.texto = texto;
    }

    public Agrupacion getAgrupacion() {
        return agrupacion;
    }

    public void setAgrupacion(Agrupacion agrupacion) {
        this.agrupacion = agrupacion;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public List<Pregunta> getPreguntas() {
        return preguntas;
    }

    public void setPreguntas(List<Pregunta> preguntas) {
        this.preguntas = preguntas;
    }

    public int getNumPreguntas() {
        return (this.preguntas != null) ? this.preguntas.size() : 0;
    }

    /**
     * @param numPreguntas
     * @return
     */
    public List<Pregunta> getPreguntasRandom(int numPreguntas) {
        //si tiene mas preguntas que numPreguntas
        //desordeno y saco numPreguntas
        if (this.preguntas.size() > numPreguntas) {
            LOG.info("Del tema {} hay {} preguntas y voy a intentar sacar {} más ", this.texto, this.getPreguntas().size(), numPreguntas);
            Collections.shuffle(this.getPreguntas());
            return this.getPreguntas().subList(0, numPreguntas);
        } else {
            Collections.shuffle(this.getPreguntas());
            return this.getPreguntas();
        }
    }

    /**
     * @return
     */
    public boolean hasPreguntas() {
        return (this.getPreguntas().size() > 0);
    }
}
