package com.dbhstudios.akdmvm.infraestructure.controller;


import com.dbhstudios.akdmvm.domain.entity.model.Agrupacion;
import com.dbhstudios.akdmvm.domain.entity.model.Pregunta;
import com.dbhstudios.akdmvm.domain.entity.model.Respuesta;
import com.dbhstudios.akdmvm.domain.entity.model.Tema;
import com.dbhstudios.akdmvm.application.service.AgrupacionService;
import com.dbhstudios.akdmvm.application.service.PreguntaService;
import com.dbhstudios.akdmvm.application.service.RespuestaService;
import com.dbhstudios.akdmvm.application.service.TemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "api/v1",produces = MediaType.APPLICATION_JSON_VALUE)
public class ApiRestController {

	private final AgrupacionService agrupacionService;
	private final TemaService temaService;
	private final PreguntaService preguntaService;
	private final RespuestaService respuestaService;
	
	
	@Autowired
	public ApiRestController(AgrupacionService agrupacionService, TemaService temaservice, PreguntaService preguntaService, RespuestaService respuestaService) {
		this.agrupacionService = agrupacionService;
		this.temaService = temaservice;
		this.preguntaService = preguntaService;
		this.respuestaService = respuestaService;
	}

	@RequestMapping("/agrupaciones")
	public List<Agrupacion> getAgrupaciones(){
		return this.agrupacionService.getAgrupaciones();
	}
	
	@RequestMapping("/temas")
	public List<Tema> getTemas(@RequestParam(value = "empty", required=false) boolean empty){
		if (empty) {
			return this.temaService.getTemasConPreguntas();
		} else {
			return this.temaService.getTemas();
		}
	}
	
	@RequestMapping("/preguntas")
	public List<Pregunta> getPreguntas(){
		return this.preguntaService.getPreguntas();
	}
	
	
	@RequestMapping("/respuestas")
	public List<Respuesta> getRespuestas(){
		return this.respuestaService.getRespuestas();
	}
	
	

	@RequestMapping("/tema/{id}/preguntas")
	public List<Pregunta> getPreguntasByTema(@PathVariable("id") Long id, @RequestParam(required = false, defaultValue = "false") Boolean scramble){
		Optional<Tema> tema = this.temaService.getTemaById(id);
		if(scramble){
			return this.preguntaService.scramble(this.preguntaService.getAllByTema(tema));
		} else {
			return this.preguntaService.getAllByTema(tema);
		}

	}
	
	
	@RequestMapping("/pregunta/{id}/respuestas")
	public List<Respuesta> getRespuestasDePregunta(@PathVariable("id")Long id) {
		return this.respuestaService.getRespuestasDePregunta(this.preguntaService.getById(id));
	}

	@RequestMapping("/pregunta/buscar")
	public List<Pregunta> getPreguntasBySearchTopic(@RequestParam("text") String text) {
		return this.preguntaService.searchInPregunta(text);
	}

	@RequestMapping("/respuesta/{id}/pregunta")
	public Pregunta getPreguntaDeRespuesta(@PathVariable("id")Long id) {
		return this.preguntaService.getPreguntaContainingRespuesta(id);
	}

}
