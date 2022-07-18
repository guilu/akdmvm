package com.dbhstudios.akdmvm.infraestructure.controller;

import com.dbhstudios.akdmvm.application.service.ExamenService;
import com.dbhstudios.akdmvm.application.service.PreguntaService;
import com.dbhstudios.akdmvm.application.service.TestService;
import com.dbhstudios.akdmvm.domain.dto.ContadoresTest;
import com.dbhstudios.akdmvm.domain.dto.TemasSeleccionados;
import com.dbhstudios.akdmvm.domain.dto.TestDTO;
import com.dbhstudios.akdmvm.domain.dto.TestStatsDTO;
import com.dbhstudios.akdmvm.domain.entity.model.Examen;
import com.dbhstudios.akdmvm.domain.entity.model.Pregunta;
import com.dbhstudios.akdmvm.domain.entity.model.Test;
import lombok.extern.log4j.Log4j2;
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
public class TestGenerationController {


    //test,examen,pregunta
    private TestService testService;
    private ExamenService examenService;
    private PreguntaService preguntaService;

    public TestGenerationController(TestService testService, ExamenService examenService, PreguntaService preguntaService) {
        this.testService = testService;
        this.examenService = examenService;
        this.preguntaService = preguntaService;
    }

    @PostMapping("/test")
    public String generarTest(@ModelAttribute TemasSeleccionados temasSeleccionados, Model model,
                              HttpSession session,
                              Principal principal
    ) {
        temasSeleccionados.setTemasNotNull();

        log.info("endpoint /test recibo del form {} tema(s): {}", temasSeleccionados.getTemas().size(), temasSeleccionados);
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

        try {
            //grabamos el test en bbdd
            testService.grabarTest(test, principal.getName());
        } catch (Exception exception) {
            log.error("No he podido grabar el test {}",test);
            log.error("Ha ocurrido la siguiente excepcion {}", exception.getMessage());
            log.error("todo el test: {}", test.toJson());
        }

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

                try {
                    //grabamos el test en bbdd
                    testService.grabarTest(test, principal.getName());
                } catch (Exception exception) {
                    log.error("No he podido grabar el test {}",test);
                    log.error("Ha ocurrido la siguiente excepcion {}", exception.getMessage());
                    log.error("todo el test: {}", test.toJson());
                }

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
                TestStatsDTO testStats = testService.getStatsFromUsername(principal.getName());
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

        try {
            //actualiza info de test.
            log.debug("el test {} {}",test.getId(), test);
            //grabamos el test en bbdd
            testService.grabarTest(test, principal.getName());
        } catch (Exception exception) {
            log.error("No he podido grabar el test {}",test);
            log.error("Ha ocurrido la siguiente excepcion {}", exception.getMessage());
            log.error("todo el test: {}", test.toJson());
        }


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
        TestStatsDTO testStats = testService.getStatsFromUsername(principal.getName());
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

        //grabamos el test
        Test test = new Test(preguntas.size(),preguntas.size());
        test.setPreguntasTest(preguntas);

        session.setAttribute("preguntas", preguntas);
        session.setAttribute("test",test);

        model.addAttribute("preguntas", preguntas);
        model.addAttribute("test", new TestDTO(test));

        model.addAttribute("menu", "examenes");
        model.addAttribute("examenes", this.examenService.getExamenes());

        return "simulacro";
    }


}
