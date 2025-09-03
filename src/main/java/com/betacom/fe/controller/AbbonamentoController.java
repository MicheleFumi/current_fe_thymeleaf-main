package com.betacom.fe.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.ModelAndView;

import com.betacom.fe.dto.AbbonamentoDTO;
import com.betacom.fe.dto.AttivitaDTO;
import com.betacom.fe.dto.SocioDTO;
import com.betacom.fe.requests.AbbonamentoReq;
import com.betacom.fe.response.ResponseBase;
import com.betacom.fe.response.ResponseList;
import com.betacom.fe.response.ResponseObject;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
public class AbbonamentoController {
	private WebClient clientWeb;

	public AbbonamentoController(WebClient clientWeb) {
		this.clientWeb = clientWeb;
	}
	
	@GetMapping("listAbbonamenti")
	public ModelAndView listAbbonamenti(@RequestParam Integer id) {
		log.debug("listAbbonamenti:" + id);
		ModelAndView mav = new ModelAndView("listAbbonamenti");
		
		
		SocioDTO soc = clientWeb.get()
				.uri(uriBuilder -> uriBuilder
						.path("socio/getSocio")
						.queryParam("id", id)
						.build())
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<ResponseObject<SocioDTO>>() {})
				.block()
				.getDati();
		log.debug("socio abbo:" + soc.getAbbonamento().size() );
		
		List<AttivitaDTO> attivita = clientWeb.get()
				.uri("attivita/list")
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<ResponseList<AttivitaDTO>>() {})
				.block()
				.getDati();
		
		
		
		
		
		
		soc.getAbbonamento().forEach(a -> log.debug(a.toString()));
		
		mav.addObject("listAbb",soc.getAbbonamento());
		mav.addObject("socioID", id);
		mav.addObject("attivita", attivita);
		return mav;
	}
	
	
	@GetMapping("createAbbonamento")
	public String createAbbonamento(@RequestParam(required = true) Integer socioID) {
		
		log.debug("createAbbonamento id: " + socioID);
		ResponseBase resp = new ResponseBase();
		AbbonamentoReq req = new AbbonamentoReq();
		
		req.setSocioId(socioID);
		req.setDataIscrizione(LocalDate.now());
		
		resp = clientWeb.post()
				.uri("abbonamento/create")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(req)
				.retrieve()
				.bodyToMono(ResponseBase.class)
				.block();		
		log.debug("rc: " +resp.getRc());
		return "redirect:/listAbbonamenti?id=" + socioID;
		
	}
	
	@PostMapping("removeAbbonamento")
	public String removeAbbonamento(@RequestParam(required = true) Integer socioID, @RequestParam(required = true) Integer abbonamentoID ) {
		
		log.debug("removeAbbonamento id: " + abbonamentoID);
		ResponseBase resp = new ResponseBase();
		AbbonamentoReq req = new AbbonamentoReq();
		
		req.setSocioId(socioID);
		req.setId(abbonamentoID);
		
		resp = clientWeb.post()
				.uri("abbonamento/delete")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(req)
				.retrieve()
				.bodyToMono(ResponseBase.class)
				.block();		
		log.debug("rc: " +resp.getRc());
		return "redirect:/listAbbonamenti?id=" + socioID;
		
	}
	
}
