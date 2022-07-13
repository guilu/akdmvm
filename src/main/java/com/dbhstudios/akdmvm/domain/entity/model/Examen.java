package com.dbhstudios.akdmvm.domain.entity.model;

import com.dbhstudios.akdmvm.domain.entity.BaseEntity;
import com.dbhstudios.akdmvm.domain.entity.DomainModelNames;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = DomainModelNames.TB02_EXAMEN)
public class Examen extends BaseEntity {
	
	private static final long serialVersionUID = 1L;

	@Size(max = 1024)
    @Column
    private String texto;

    @OneToMany(mappedBy = "examen", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Pregunta> preguntas;

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
}
