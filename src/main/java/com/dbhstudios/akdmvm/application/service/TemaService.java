package com.dbhstudios.akdmvm.application.service;

import com.dbhstudios.akdmvm.domain.dto.NuevaPreguntaDTO;
import com.dbhstudios.akdmvm.domain.dto.NuevoTemaDTO;
import com.dbhstudios.akdmvm.domain.entity.model.Agrupacion;
import com.dbhstudios.akdmvm.domain.entity.model.Pregunta;
import com.dbhstudios.akdmvm.domain.entity.model.Respuesta;
import com.dbhstudios.akdmvm.domain.entity.model.Tema;
import com.dbhstudios.akdmvm.domain.respository.TemaJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class TemaService {

    private static final Logger LOG = LoggerFactory.getLogger(TemaService.class);

    private final TemaJpaRepository temaJpaRepository;
    private final PreguntaService preguntaService;

    private final AgrupacionService agrupacionService;


    @Autowired
    public TemaService(TemaJpaRepository temaJpaRepository, PreguntaService preguntaService, AgrupacionService agrupacionService) {
        this.temaJpaRepository = temaJpaRepository;
        this.preguntaService = preguntaService;
        this.agrupacionService = agrupacionService;
    }

    public List<Tema> getTemas() {
        return this.temaJpaRepository.findAll();
    }

    public Optional<Tema> getTemaById(Long id) {
        return this.temaJpaRepository.findById(id);
    }
    
    public List<Tema> getTemasConPreguntas(){
    	return this.temaJpaRepository.findTemasConPreguntas();
    }

    /**
     * random tambien de las respuestas de las preguntas....
     *
     * @param temas
     * @return
     */
    public List<Tema> scramble(List<Tema> temas, int preguntasPorTema) {
        Collections.shuffle(temas);
        for (Tema tema : temas) {
            Collections.shuffle(this.preguntaService.scramble(tema.getPreguntas()));
        }
        return temas;
    }

    /**
     * Selecciona un tema al azahar :P
     *
     * @return
     */
    public List<Pregunta> getTemasYPreguntasRandom(int numPreguntasTotales) {

		List<Pregunta> flattenPreguntas = new ArrayList<>();

        List<Tema> temas = this.temaJpaRepository.findTemasConPreguntas();
        LOG.info("Hay {} temas con preguntas", temas.size());
        int temasSize = temas.size();

        if(temasSize>0){
			int preguntasXTemas = numPreguntasTotales / temasSize;
			int resto = numPreguntasTotales % temasSize;
			LOG.info("De cada tema hay que sacar {} preguntas si puedo", preguntasXTemas);
            LOG.info("El resto son {}", resto);

			//En realidad voy a sacar de cad tema dsponible preguntasXTemas así que:
			for (int temaCurrent = 0; temaCurrent < temasSize; temaCurrent++) {
				//los temas tienen que salir en orden así que hay que sacar el numero de preguntas de cada tema
                // y hasta los restos :P una pregunta más
                if (resto>0) {
                    flattenPreguntas.addAll(temas.get(temaCurrent).getPreguntasRandom(preguntasXTemas + 1));
                } else {
                    flattenPreguntas.addAll(temas.get(temaCurrent).getPreguntasRandom(preguntasXTemas));
                }
                resto--;
			}
		}
        return flattenPreguntas;
    }

    public List<Tema> searchInTema(String cadena){
        return this.temaJpaRepository.findByTextoContainingIgnoreCase(cadena);
    }


    public Pregunta saveNewPreguntaDTOenTema(NuevaPreguntaDTO preguntaDTO){
        //de la pregunta DTO se obtiene el id  del tema al que se le pone la pregunta
        // y sus 4 respuestas
        Optional<Tema> tema = temaJpaRepository.findById((long) preguntaDTO.getIdTema());

        Pregunta pregunta = new Pregunta(preguntaDTO.getTextoPregunta());
        List<Respuesta> respuestas = new ArrayList<>();
        Respuesta respuesta1 = new Respuesta(preguntaDTO.getTexto1Respuesta(), preguntaDTO.getRespuestaCorrecta() == 1, pregunta);
        respuestas.add(respuesta1);
        Respuesta respuesta2 = new Respuesta(preguntaDTO.getTexto2Respuesta(), preguntaDTO.getRespuestaCorrecta() == 2, pregunta);
        respuestas.add(respuesta2);
        Respuesta respuesta3 = new Respuesta(preguntaDTO.getTexto3Respuesta(), preguntaDTO.getRespuestaCorrecta() == 3, pregunta);
        respuestas.add(respuesta3);
        Respuesta respuesta4 = new Respuesta(preguntaDTO.getTexto4Respuesta(), preguntaDTO.getRespuestaCorrecta() == 4, pregunta);
        respuestas.add(respuesta4);

        pregunta.setTema(tema.get());
        pregunta.setRespuestas(respuestas);


        return preguntaService.savePregunta(pregunta);

    }


    public List<Tema> getTemasAlphabetically() {
        return temaJpaRepository.findAllByOrderByTextoAsc();
    }

    public Tema saveNewTemaDTO(NuevoTemaDTO temaDTO){

        Tema tema = new Tema(temaDTO.getTextoTema());
        Agrupacion agrupacion = null;
        if (temaDTO.getIdAgrupacion() > 0L){
            agrupacion = agrupacionService.getById(temaDTO.getIdAgrupacion());
        }
        tema.setAgrupacion(agrupacion);

        return temaJpaRepository.save(tema);
    }

}
