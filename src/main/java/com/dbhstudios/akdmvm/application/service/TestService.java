package com.dbhstudios.akdmvm.application.service;

import com.dbhstudios.akdmvm.auth.domain.model.User;
import com.dbhstudios.akdmvm.auth.service.UserService;
import com.dbhstudios.akdmvm.domain.dto.ContadoresTest;
import com.dbhstudios.akdmvm.domain.dto.TemasSeleccionados;
import com.dbhstudios.akdmvm.domain.dto.TestStatsDTO;
import com.dbhstudios.akdmvm.domain.entity.model.Agrupacion;
import com.dbhstudios.akdmvm.domain.entity.model.Pregunta;
import com.dbhstudios.akdmvm.domain.entity.model.Tema;
import com.dbhstudios.akdmvm.domain.entity.model.Test;
import com.dbhstudios.akdmvm.domain.respository.TestRespository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class TestService {

    private final TemaService temaService;

    private final AgrupacionService agrupacionService;


    private final TestRespository testRespository;

    private final UserService userService;

    @Autowired
    public TestService(TemaService temaService, AgrupacionService agrupacionService, TestRespository testRespository, UserService userService) {
        this.temaService = temaService;
        this.agrupacionService = agrupacionService;
        this.testRespository = testRespository;
        this.userService = userService;
    }

    public List<Pregunta> generaPreguntas(TemasSeleccionados temasSeleccionados) {
        List<Pregunta> flattenPreguntas = new ArrayList<>();
        List<Tema> temas = this.temaService.scramble(temasSeleccionados.getTemas(), temasSeleccionados.getNumPreguntasPorTema());
        for (Tema tema : temas) {
            flattenPreguntas.addAll(tema.getPreguntas());
        }
        if (flattenPreguntas.size() < temasSeleccionados.getNumPreguntasPorTema()) {
            return flattenPreguntas;
        } else {
            return flattenPreguntas.subList(0, temasSeleccionados.getNumPreguntasPorTema());
        }
    }

    public ContadoresTest pasaPregunta(ContadoresTest contadoresTest) {
        contadoresTest.setCurrent(contadoresTest.getCurrent() + 1);
        contadoresTest.setAciertos(contadoresTest.getAciertos());
        contadoresTest.setErrores(contadoresTest.getErrores());
        return contadoresTest;
    }

    public List<Pregunta> generaSimulacro(int numPreguntasTotales) {
        // 50 preguntas.... recorre todos los temas cogiendo una pregunta al azar
        return temaService.getTemasYPreguntasRandom(numPreguntasTotales);
    }


    public List<Tema> getTemas() {
        return temaService.getTemas();
    }

    public List<Tema> getTemasAlphabetically() {
        return temaService.getTemasAlphabetically();
    }

    public List<Agrupacion> getAgrupacionesAlphabetically() {
        return agrupacionService.getAgrupacionesAlphabetically();
    }

    public void grabarTest(Test test, String username) {
        User user = this.userService.findUserByEmail(username);
        test.setUser(user);
        this.testRespository.save(test);
    }

    public void updateTest(Test test) {

        Optional<Test> testToUpdate = testRespository.findById(test.getId());
        if(testToUpdate.isPresent()){
            testToUpdate.get().setFallos(test.getFallos());
            testToUpdate.get().setAciertos(test.getAciertos());
            testToUpdate.get().setFinalizado(test.isFinalizado());
            testRespository.save(testToUpdate.get());
        }

    }


    public TestStatsDTO getStatsFromUsersname(String usermail){

        User user = userService.findUserByEmail(usermail);

        List<Test> tests = testRespository.findByFinalizadoTrueAndUser(user);

        log.info("Tests finalizados del usuario {} son {}",usermail,tests.size());

        if (tests.size() > 0 ) {

            TestStatsDTO stats = new TestStatsDTO();
            stats.setTestFinalizados(tests.size());
            stats.setMediaAciertos(testRespository.findAvgAciertosByUser(user));
            stats.setMediaFallos(testRespository.findAvgFallosByUser(user));
            return stats;

        } else {
            // No tienes estadísticas
            return new TestStatsDTO();
        }

    }
}
