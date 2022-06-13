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

    @PostMapping("/test")
    public String generarTest(@ModelAttribute TemasSeleccionados temasSeleccionados, Model model,
                              HttpSession session,
                              Principal principal
    ) {
        temasSeleccionados.setTemasNotNull();

        log.info("recibo del form {} tema(s): {}", temasSeleccionados.getTemas().size(), temasSeleccionados);
        log.info("total de preguntas {}", temasSeleccionados.getTotalPreguntas());
        log.info("temasSeleccionados {}",temasSeleccionados);


        List<Pregunta> preguntas = testService.generaPreguntas(temasSeleccionados);
        ContadoresTest contadoresTest = new ContadoresTest();
        contadoresTest.setNumPreguntasPorTemas(temasSeleccionados.getNumPreguntasPorTema());
        contadoresTest.setNumPreguntasTotal(preguntas.size());

        if (preguntas.size() == 0)
            preguntas = null; //me lo cargo para que no sea un array vacio sobre el que thymeleaf no puede iterar

        //llenamos el objeto Test para guardar el resultado final.
        Test test = new Test(temasSeleccionados.getNumPreguntasPorTema(),preguntas.size());
        test.setPreguntasTest(preguntas);

        model.addAttribute("contadoresTest", contadoresTest);
        model.addAttribute("preguntas", preguntas);
        model.addAttribute("menu", "temas-seleccion");

        //grabamos el test en bbdd
        testService.grabarTest(test,principal.getName());

        session.setAttribute("preguntasFalladas", new ArrayList<>());
        session.setAttribute("contadoresTest", contadoresTest);
        session.setAttribute("preguntas", preguntas);
        session.setAttribute("test",test);

        return "test";
    }

    @PostMapping("/pasaPregunta")
    public String pasaPregunta(@ModelAttribute ContadoresTest contadoresTest, Model model, HttpSession session, Principal principal) {

        @SuppressWarnings("unchecked")
        List<Pregunta> preguntasFalladas = (List<Pregunta>) session.getAttribute("preguntasFalladas");
        @SuppressWarnings("unchecked")
        List<Pregunta> preguntas = (List<Pregunta>) session.getAttribute("preguntas");
        Test test = (Test) session.getAttribute("test");

        log.info("Llamada a PasaPregunta Contadores {} {}",contadoresTest.getCurrent(), preguntas.size());

        //puede que no sea la primera pregunta si no es un test sino que viene de una busqueda:
        int preguntaAnterior = (contadoresTest.getCurrent() == 0) ? 0 : contadoresTest.getCurrent() - 1;

        //log.debug("fallada: {}", contadoresTest.isFallada());

        if (contadoresTest.isFallada()) {
            //vamos llenando el array de preguntas falladas para repetir al final si el usuario quiere.
            preguntasFalladas.add(preguntas.get(preguntaAnterior));
            test.setFallos(test.getFallos()+1);
        } else {
            test.setAciertos(test.getAciertos()+1);
        }

        //actualiza info de test.
        log.debug("el test {} {}",test.getId(), test);
        testService.updateTest(test);

        //si se ha terminado el test
        if (contadoresTest.getCurrent() == preguntas.size()) {

            //test finalizado
            test.setFinalizado(true);
            testService.updateTest(test);

            log.debug("FIN! Finalizado True!. Preguntas falladas:{}, Preguntas correctas:{}", preguntasFalladas.size(), preguntas.size()-preguntasFalladas.size());
            log.debug("repetir las falladas? {}", contadoresTest.isRepetirFalladas());

            //repetir las fallidas es un test nuevo solo con los fallos.
            if (contadoresTest.isRepetirFalladas()) {

                //Para repetir solo las falladas.
                // * desordenar las preguntas falladas
                // * reseteo de contadores
                // * las preguntas son solo las preguntas falladas.

                preguntasFalladas = this.preguntaService.scramble(preguntasFalladas);
                contadoresTest.reset();
                contadoresTest.setNumPreguntasTotal(preguntasFalladas.size());

                //llenamos el objeto Test para guardar el resultado final.
                test = new Test(contadoresTest.getNumPreguntasPorTemas(),preguntasFalladas.size());
                test.setPreguntasTest(preguntasFalladas);
                //grabamos el test en bbdd
                testService.grabarTest(test,principal.getName());

                model.addAttribute("contadoresTest", contadoresTest);
                model.addAttribute("preguntas", preguntasFalladas);

                session.setAttribute("preguntasFalladas", new ArrayList<>());
                session.setAttribute("preguntas", preguntasFalladas);
                session.setAttribute("test",test);

                return "test";

            } else {
                //nos vamos al inicio.
                //recuperar información para mostrar en el index
                // * numero de tests finalizados por el usuario
                // * media de aciertos
                // * media de fallos
                //
                TestStatsDTO testStats = testService.getStatsFromUsersname(principal.getName());
                log.info("estadisticas {}", testStats);
                model.addAttribute("stats", testStats);

                return "backstage/index";
            }


        } else {
            //va acumulando...

            log.info("La Pregunta {} de {}: {} ", contadoresTest.getCurrent(), preguntas.size(), preguntas.get(contadoresTest.getCurrent()).getTexto());
            session.setAttribute("preguntasFalladas", preguntasFalladas);
            model.addAttribute("contadoresTest", contadoresTest);
            model.addAttribute("preguntas", preguntas);
        }

        model.addAttribute("menu", "test");
        return "test";
    }


    @RequestMapping("/simulacro/{numPreguntasTotales}")
    public String simulacro(@PathVariable int numPreguntasTotales, Model model, HttpSession session, Principal principal) {

        //Es lo mismo que un test, pero los temasSeleccionados son random y las preguntas son totales:
        //50 75 o 100
        //Se cogen preguntas de cada tema en round robin. Para que las preguntas del mismo tema salgan juntas

        List<Pregunta> preguntas = testService.generaSimulacro(numPreguntasTotales);
        log.info("Preguntas totales para el simulacro: {}", numPreguntasTotales);
        log.info("Preguntas sacadas: {}", preguntas.size());

        //grabamos el test
        Test test = new Test(numPreguntasTotales,preguntas.size());
        test.setPreguntasTest(preguntas);

        session.setAttribute("preguntas", preguntas);
        session.setAttribute("test",test);

        model.addAttribute("preguntas", preguntas);
        model.addAttribute("test", new TestDTO(test));

        model.addAttribute("menu", "simulacro");
        model.addAttribute("examenes", this.examenService.getExamenes());

        return "simulacro";
    }

    @PostMapping("simulacro/iniciar")
    @ResponseBody
    public String iniciarSimulacro(Model model, HttpSession session, Principal principal){
        Test test = (Test) session.getAttribute("test");
        test.setFinalizado(false);
        test.setAciertos(0);
        test.setFallos(0);

        //actualiza info de test.
        log.debug("el test {} {}",test.getId(), test);
        testService.grabarTest(test,principal.getName());

        return "guardado "+test.getId();
    }

    @PostMapping("simulacro/resolver")
    public String resolverSimulacro(@ModelAttribute TestDTO testDTO, Model model, HttpSession session, Principal principal){

        Test test = (Test) session.getAttribute("test");
        test.setFallos(testDTO.getFallos());
        test.setAciertos(testDTO.getAciertos());
        test.setFinalizado(true);

        //actualiza info de test.
        log.debug("se acabo el simulacro {} {}",test.getId(), test);
        testService.updateTest(test);

        //nos vamos al inicio.
        //recuperar información para mostrar en el index
        // * numero de tests finalizados por el usuario
        // * media de aciertos
        // * media de fallos
        //
        TestStatsDTO testStats = testService.getStatsFromUsersname(principal.getName());
        log.info("estadisticas {}", testStats);
        model.addAttribute("stats", testStats);
        return "backstage/index";
    }

    @RequestMapping("/examen/{id}")
    public String examen(@PathVariable int id, Model model, HttpSession session) {

        //Es lo mismo que un simulacro pero las preguntas salen del examen
        //Se cogen preguntas del examen y las respuestas se barajan.
        Optional<Examen> examen = this.examenService.getExamen((long) id);

        List<Pregunta> preguntas = this.preguntaService.getPreguntasDeExamen(examen);

        if (preguntas.size() == 0){
            model.addAttribute("message","NO ENCUENTRO PREGUNTAS PARA ESTE EXAMEN. ");
            model.addAttribute("examenes", this.examenService.getExamenes());
            return "message";
        }

        //En los examenes las preguntas no se desordenan pero si las respuestas
        preguntas = this.preguntaService.scrambleRespuestas(preguntas);
        log.info("Preguntas totales para el examen: {}", examen.isPresent() ? examen.get().getTexto() : "vacio");
        log.info("Preguntas sacadas: {}", preguntas.size());

        session.setAttribute("preguntas", preguntas);
        model.addAttribute("preguntas", preguntas);
        model.addAttribute("menu", "examenes");
        model.addAttribute("examenes", this.examenService.getExamenes());
        return "simulacro";
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


    @GetMapping("/new-pregunta")
    public String nuevaPreguntaForm(Model model){
        model.addAttribute("menu", "new-pregunta");
        model.addAttribute("examenes", this.examenService.getExamenes());

        model.addAttribute("temas", testService.getTemasAlphabetically());
        model.addAttribute("nuevaPregunta", new NuevaPreguntaDTO());
        return "new-pregunta";

    }

    @PostMapping("/new-pregunta")
    public String nuevaPreguntaSubmit(@ModelAttribute NuevaPreguntaDTO nuevaPregunta, Model model){
        model.addAttribute("menu", "new-pregunta");
        model.addAttribute("examenes", this.examenService.getExamenes());

        model.addAttribute("nuevaPregunta", new NuevaPreguntaDTO());

        Pregunta pregunta = temaService.saveNewPreguntaDTOenTema(nuevaPregunta);
        model.addAttribute("pregunta",pregunta);
        return "new-pregunta";
    }



    @GetMapping("/new-tema")
    public String nuevoTemaForm(Model model){
        model.addAttribute("menu", "new-tema");
        model.addAttribute("examenes", this.examenService.getExamenes());

        model.addAttribute("agrupaciones", testService.getAgrupacionesAlphabetically());
        model.addAttribute("nuevoTema", new NuevoTemaDTO());
        return "new-tema";
    }


    @PostMapping("/new-tema")
    public String nuevoTemaSubmit(@ModelAttribute NuevoTemaDTO nuevoTema, Model model) {
        model.addAttribute("menu", "new-tema");
        model.addAttribute("examenes", this.examenService.getExamenes());

        model.addAttribute("nuevoTema", new NuevoTemaDTO());

        Tema temaSaved = temaService.saveNewTemaDTO(nuevoTema);

        model.addAttribute("tema", temaSaved);
        return "new-tema";

    }


    @GetMapping("/new-agrupacion")
    public String nuevaAgrupacionForm(Model model){
        model.addAttribute("menu", "new-agrupacion");
        model.addAttribute("examenes", this.examenService.getExamenes());

        model.addAttribute("nuevaAgrupacion", new Agrupacion());

        return "new-agrupacion";
    }


    @PostMapping("/new-agrupacion")
    public String nuevaAgrupacionSubmit(@ModelAttribute Agrupacion nuevaAgrupacion, Model model) {
        model.addAttribute("menu", "new-agrupacion");
        model.addAttribute("examenes", this.examenService.getExamenes());

        model.addAttribute("nuevaAgrupacion", new Agrupacion());

        Agrupacion agrupacionSaved = agrupacionService.saveAgrupacion(nuevaAgrupacion);

        model.addAttribute("agrupacion", agrupacionSaved);
        return "new-agrupacion";

    }

    @GetMapping("/delete-agrupacion")
    public String deleteAgrupacionForm(Model model){
        model.addAttribute("menu", "new-tema");
        model.addAttribute("examenes", this.examenService.getExamenes());

        model.addAttribute("agrupaciones", testService.getAgrupacionesAlphabetically());
        model.addAttribute("agrupacion", new Agrupacion());
        return "delete-agrupacion";
    }

    @PostMapping("/delete-agrupacion")
    public String deleteAgrupacionSubmit(@ModelAttribute Agrupacion agrupacion, Model model){
        model.addAttribute("menu", "new-tema");
        model.addAttribute("examenes", this.examenService.getExamenes());

        agrupacionService.deleteAgrupacion(agrupacion);
        model.addAttribute("agrupaciones", testService.getAgrupacionesAlphabetically());

        return "delete-agrupacion";
    }






}
