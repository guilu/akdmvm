package com.dbhstudios.akdmvm.application.service;


import com.dbhstudios.akdmvm.domain.entity.model.Pregunta;
import com.dbhstudios.akdmvm.domain.entity.model.Respuesta;
import com.dbhstudios.akdmvm.domain.respository.RespuestaJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class RespuestaService {

    private final RespuestaJpaRepository respuestaJpaRepository;

    @Autowired
    public RespuestaService(RespuestaJpaRepository respuestaJpaRepository) {
        this.respuestaJpaRepository = respuestaJpaRepository;
    }
    
    public List<Respuesta> getRespuestas(){
    	return this.respuestaJpaRepository.findAll();
    }

    public List<Respuesta> scramble(List<Respuesta> respuestas){
        Collections.shuffle(respuestas);
        return respuestas;
    }
    
    public List<Respuesta> getRespuestasDePregunta(Optional<Pregunta> pregunta) {
    	return this.respuestaJpaRepository.findByPregunta(pregunta);
    }

    public List<Respuesta> searchInRespuesta(String textoABuscar) {
        return this.respuestaJpaRepository.findByTextoContainingIgnoreCase(textoABuscar);
    }
}
