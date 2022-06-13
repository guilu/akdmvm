package com.dbhstudios.akdmvm.infraestructure.controller;

import com.dbhstudios.akdmvm.application.service.TestService;
import com.dbhstudios.akdmvm.domain.dto.TestStatsDTO;
import com.dbhstudios.akdmvm.domain.entity.model.Test;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/backstage")
public class BackstageController {


    private TestService testService;


    public BackstageController(TestService testService){
        this.testService = testService;
    }

    @RequestMapping(value= {"/",""})
    public String backstage(Model model, Principal principal){

        //recuperar información para mostrar en el index
        // * numero de tests finalizados por el usuario
        // * media de aciertos
        // * media de fallos
        //
        TestStatsDTO testStats = testService.getStatsFromUsersname(principal.getName());
        model.addAttribute("stats", testStats);


        return "backstage/index";
    }
}
