package com.dbhstudios.akdmvm.domain.respository;

import com.dbhstudios.akdmvm.domain.entity.model.Pregunta;
import com.dbhstudios.akdmvm.domain.entity.model.Respuesta;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RespuestaJpaRepository extends PagingAndSortingRepository<Respuesta, Long> {

	List<Respuesta> findAll();
	List<Respuesta> findByPregunta(Optional<Pregunta> pregunta);
    List<Respuesta> findByTextoContainingIgnoreCase(String textoABuscar);
}
