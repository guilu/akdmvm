package com.dbhstudios.akdmvm.infraestructure.controller;

import com.dbhstudios.akdmvm.application.service.ExamenService;
import com.dbhstudios.akdmvm.application.service.TestService;
import com.dbhstudios.akdmvm.domain.dto.TestStatsDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/backstage")
public class BackstageController {


    private TestService testService;
    private ExamenService examenService;


    public BackstageController(TestService testService, ExamenService examenService){
        this.testService = testService;
        this.examenService = examenService;
    }

    @RequestMapping(value= {"/",""})
    public String backstage(Model model, Principal principal){

        //recuperar información para mostrar en el index
        // * numero de tests finalizados por el usuario
        // * media de aciertos
        // * media de fallos
        //
        TestStatsDTO testStats = testService.getStatsFromUsername(principal.getName());
        model.addAttribute("stats", testStats);
        model.addAttribute("examenes", this.examenService.getExamenes());

        return "backstage/index";
    }
}
