package com.dbhstudios.akdmvm.infraestructure.controller.admin;

import com.dbhstudios.akdmvm.application.service.AgrupacionService;
import com.dbhstudios.akdmvm.application.service.ExamenService;
import com.dbhstudios.akdmvm.application.service.TemaService;
import com.dbhstudios.akdmvm.application.service.TestService;
import com.dbhstudios.akdmvm.domain.dto.NuevaPreguntaDTO;
import com.dbhstudios.akdmvm.domain.dto.NuevoTemaDTO;
import com.dbhstudios.akdmvm.domain.entity.model.Agrupacion;
import com.dbhstudios.akdmvm.domain.entity.model.Pregunta;
import com.dbhstudios.akdmvm.domain.entity.model.Tema;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AdminController {

    private ExamenService examenService;
    private TestService testService;
    private TemaService temaService;
    private AgrupacionService agrupacionService;

    public AdminController(ExamenService examenService, TestService testService, TemaService temaService, AgrupacionService agrupacionService) {
        this.examenService = examenService;
        this.testService = testService;
        this.temaService = temaService;
        this.agrupacionService = agrupacionService;
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
