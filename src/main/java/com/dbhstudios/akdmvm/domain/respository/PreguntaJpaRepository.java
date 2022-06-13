package com.dbhstudios.akdmvm.domain.respository;

import com.dbhstudios.akdmvm.domain.entity.model.Examen;
import com.dbhstudios.akdmvm.domain.entity.model.Pregunta;
import com.dbhstudios.akdmvm.domain.entity.model.Respuesta;
import com.dbhstudios.akdmvm.domain.entity.model.Tema;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PreguntaJpaRepository extends PagingAndSortingRepository<Pregunta, Long> {

    @Override
    List<Pregunta> findAll();
    List<Pregunta> findByTema(Optional<Tema> tema);
    List<Pregunta> findByExamen(Optional<Examen> examen);

    List<Pregunta> findByTextoContainingIgnoreCase(String textoABuscar);

    Pregunta findPreguntaByRespuestasContains(Optional<Respuesta> respuesta);


}
