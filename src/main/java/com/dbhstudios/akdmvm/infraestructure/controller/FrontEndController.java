package com.dbhstudios.akdmvm.infraestructure.controller;

import com.dbhstudios.akdmvm.application.service.*;
import com.dbhstudios.akdmvm.domain.dto.*;
import com.dbhstudios.akdmvm.domain.entity.model.Test;
import com.dbhstudios.akdmvm.domain.entity.model.*;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
@Controller
public class FrontEndController {

    private final AgrupacionService agrupacionService;
    private final TestService testService;
    private final SearchService searchService;
    private final ExamenService examenService;
    private final PreguntaService preguntaService;

    private final TemaService temaService;

    @Autowired
    public FrontEndController(AgrupacionService agrupacionService, TestService testService, SearchService searchService, ExamenService examenService, PreguntaService preguntaService, TemaService temaService) {
        this.agrupacionService = agrupacionService;
        this.testService = testService;
        this.searchService = searchService;
        this.examenService = examenService;
        this.preguntaService = preguntaService;
        this.temaService = temaService;
    }

    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("agrupaciones", this.agrupacionService.getAgrupaciones());
        model.addAttribute("temasSeleccionados", new TemasSeleccionados());
        model.addAttribute("examenes", this.examenService.getExamenes());
        model.addAttribute("menu", "index");
        return "index";
    }

    @RequestMapping("/temas-seleccion")
    public String temasSeleccion(Model model) {
        model.addAttribute("agrupaciones", this.agrupacionService.getAgrupaciones());
        model.addAttribute("temasSeleccionados", new TemasSeleccionados());
        model.addAttribute("examenes", this.examenService.getExamenes());
        model.addAttribute("menu", "temas");
        return "temas-seleccion";
    }

    @PostMapping("/buscar")
    public String buscar(@RequestParam String textoABuscar, Model model, HttpSession session) {
        log.info("voy a ver si encuentro '{}' en algun campo de la bbdd", textoABuscar);

        ResultadoBuscar resultadoBuscar = new ResultadoBuscar();
        List<Agrupacion> agrupaciones = this.searchService.searchInAgrupacion(textoABuscar);
        List<Tema> temas = this.searchService.searchInTema(textoABuscar);
        List<Pregunta> preguntas = this.searchService.searchInPregunta(textoABuscar);
        List<Respuesta> respuestas = this.searchService.searchInRespuesta(textoABuscar);

        if (respuestas.size() > 0) {
            resultadoBuscar.setNumRespuestas(respuestas.size());
            resultadoBuscar.setRespuestas(respuestas);
            model.addAttribute("active", "respuestas");
            model.addAttribute("respuestas", respuestas);
        }

        if (preguntas.size() > 0) {
            resultadoBuscar.setNumPreguntas(preguntas.size());
            resultadoBuscar.setPreguntas(preguntas);

            //preparar las variables para el test
            ContadoresTest contadoresTest = new ContadoresTest();
            contadoresTest.setNumPreguntasTotal(preguntas.size());
            contadoresTest.setNumPreguntasPorTemas(0);

            //Hibernate.initialize carga todas las respuestas de las preguntas que están en lazy y ahora mismo solo
            //están las preguntas
            for(Pregunta preg: preguntas) {
                Hibernate.initialize(preg.getRespuestas());
            }


            model.addAttribute("active", "preguntas");
            model.addAttribute("contadoresTest", contadoresTest);
            model.addAttribute("preguntas", preguntas);

            session.setAttribute("contadoresTest", contadoresTest);
            session.setAttribute("preguntasFalladas", new ArrayList<>());
            session.setAttribute("preguntas", preguntas);
        }

        if (temas.size() > 0) {
            resultadoBuscar.setNumTemas(temas.size());
            resultadoBuscar.setTemas(temas);
            model.addAttribute("active", "temas");
            model.addAttribute("temas", temas);
        }

        if (agrupaciones.size() > 0) {
            resultadoBuscar.setNumAgrupaciones(agrupaciones.size());
            resultadoBuscar.setAgrupaciones(agrupaciones);
            model.addAttribute("active", "agrupaciones");
            model.addAttribute("agrupaciones", agrupaciones);
        }

        model.addAttribute("resultadoBuscar", resultadoBuscar);
        return "resultado-buscar";

    }


}
