package com.dbhstudios.akdmvm.domain.entity.model;

import com.dbhstudios.akdmvm.domain.entity.BaseEntity;
import com.dbhstudios.akdmvm.domain.entity.DomainModelNames;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = DomainModelNames.TB01_AGRUPACION)
public class Agrupacion extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Size(max = 1024)
	private String texto;

	@OneToMany(mappedBy = "agrupacion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Tema> temas;

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public List<Tema> getTemas() {
		return temas;
	}

	public void setTemas(List<Tema> temas) {
		this.temas = temas;
	}
	
	
}
