package com.dbhstudios.akdmvm.application.service;

import com.dbhstudios.akdmvm.domain.entity.model.Agrupacion;
import com.dbhstudios.akdmvm.domain.entity.model.Pregunta;
import com.dbhstudios.akdmvm.domain.entity.model.Respuesta;
import com.dbhstudios.akdmvm.domain.entity.model.Tema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {

    private final AgrupacionService agrupacionService;
    private final TemaService temaService;
    private final PreguntaService preguntaService;
    private final RespuestaService respuestaService;

    @Autowired
    public SearchService(AgrupacionService agrupacionService, TemaService temaService, PreguntaService preguntaService, RespuestaService respuestaService) {
        this.agrupacionService = agrupacionService;
        this.temaService = temaService;
        this.preguntaService = preguntaService;
        this.respuestaService = respuestaService;
    }

    public List<Agrupacion> searchInAgrupacion(String textoABuscar) {
        return agrupacionService.searchInAgrupacion(textoABuscar);
    }

    public List<Tema> searchInTema(String textoABuscar) {
        return temaService.searchInTema(textoABuscar);
    }

    public List<Pregunta> searchInPregunta(String textoABuscar) {
        return preguntaService.searchInPregunta(textoABuscar);
    }

    public List<Respuesta> searchInRespuesta(String textoABuscar) {
        return respuestaService.searchInRespuesta(textoABuscar);
    }
}
