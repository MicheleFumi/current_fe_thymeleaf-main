package com.betacom.fe.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.ModelAndView;

import com.betacom.fe.dto.AbbonamentoDTO;
import com.betacom.fe.dto.AttivitaDTO;
import com.betacom.fe.requests.AttivitaReq;
import com.betacom.fe.requests.SocioReq;
import com.betacom.fe.response.ResponseBase;
import com.betacom.fe.response.ResponseList;
import com.betacom.fe.response.ResponseObject;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
public class AttivitaController {

	
	private WebClient clientWeb;

	public AttivitaController(WebClient clientWeb) {
		this.clientWeb = clientWeb;
	}
	
	
	
	@GetMapping(value= {"listAttivita"})
	public ModelAndView listAttivita() {
		
		ModelAndView mav = new ModelAndView("listAttivita");
		List<AttivitaDTO> lAtt = clientWeb.get()
				.uri("attivita/list")
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<ResponseList<AttivitaDTO>>() {})
				.block()
				.getDati();
		
		
		
		mav.addObject("listAttivita", lAtt);
	    mav.addObject("attivita", new AttivitaReq());
		return mav;
	}
	
	@GetMapping("createAttivita")
	
	public ModelAndView createAttivita() {
		ModelAndView mav = new ModelAndView("createAttivita");
		AttivitaReq a = new AttivitaReq();
		mav.addObject("attivita", a);
		
		return mav;
	}
	
	
	
	@GetMapping(value= {"updateAttivita"})
	private ModelAndView updateAttivita (@RequestParam(required = true) Integer attivitaID) {
		
		AttivitaDTO resp = clientWeb.get()
				.uri(uriBuilder -> uriBuilder
						.path("attivita/getAttivita")
						.queryParam("id", attivitaID)
						.build())
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<ResponseObject<AttivitaDTO>>() {})
				.block()
				.getDati();
		
		AttivitaReq req = AttivitaReq.builder()
				.id(resp.getId())
				.descrizione(resp.getDescrizione())
				.prezzo(resp.getPrezzo())
				.build();
		 
		 ModelAndView mav = new ModelAndView("createAttivita");
			mav.addObject("attivita", req);
	
		return mav;
	}
	
	
	@PostMapping(value= {"saveAttivita"})
	public Object saveAttivita(@ModelAttribute("attivita")  AttivitaReq req) {
		
		String operation = (req.getId() == null) ? "create" : "update";
		
		ResponseBase resp = null;
		String uri = "attivita/" + operation;		
		
		if ("create".equalsIgnoreCase(operation)) {
			resp = clientWeb.post()
					.uri(uri)
					.contentType(MediaType.APPLICATION_JSON)
					.bodyValue(req)
					.retrieve()
					.bodyToMono(ResponseBase.class)
					.block();			
		} else {
			
			resp = clientWeb.put()
					.uri(uri)
					.contentType(MediaType.APPLICATION_JSON)
					.bodyValue(req)
					.retrieve()
					.bodyToMono(ResponseBase.class)
					.block();

		}
		
		log.debug(operation + " :" + resp.getRc() + " "  + resp.getMsg());
		if (!resp.getRc()){
			ModelAndView mav = new ModelAndView("CreateAttivita");
			mav.addObject("attivita", req);
			return mav;
		}
		
		return "redirect:/listAttivita";
	}
	
	
	
	@PostMapping("deleteAttivita")
	private String deleteAttivita (@RequestParam(required = true) Integer attivitaID) {
		AttivitaReq req = new AttivitaReq();
		ResponseBase resp = new ResponseBase();
		ResponseList<AbbonamentoDTO> response = clientWeb.get()
				.uri(uriBuilder -> uriBuilder
					    .path("abbonamento/getAbbonamentoByAttivita")
					    .queryParam("id", attivitaID)
					    .build())
		        .retrieve()
		        .bodyToMono(new ParameterizedTypeReference<ResponseList<AbbonamentoDTO>>() {})
		        .block();
		
		List<AbbonamentoDTO> abbonamentiAttivi = response.getDati();
		
		 if (abbonamentiAttivi == null || abbonamentiAttivi.isEmpty()) {
		
		req.setId(attivitaID);
		
		resp = clientWeb.post()
				.uri("attivita/delete")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(req)
				.retrieve()
				.bodyToMono(ResponseBase.class)
				.block();		
		log.debug("rc: " +resp.getRc());
		 } else {
		        resp.setMsg("Non si possono cancellare attività con abbonamenti attivi");
			       
		        req.setErrorMsg(resp.getMsg());
		    }
		return "redirect:/listAttivita";
	}
	
	
	@PostMapping("createAttivitaAbbonamento")
	public String createAttivitaAbbonamento(@RequestParam(required = true) Integer abbonamentoID,
			@RequestParam(required = true) Integer attivitaID, @RequestParam(required = true) Integer socioID) {
		log.debug("abbonamentoID=" + abbonamentoID + ", attivitaID=" + attivitaID + ", socioID=" + socioID);
		AttivitaReq req= new AttivitaReq();
		ResponseBase resp = new ResponseBase();
		req.setAbbonamentiId(abbonamentoID);
		req.setId(attivitaID);
		
		resp = clientWeb.post()
				.uri("attivita/createAttivitaAbbonamento")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(req)
				.retrieve()
				.bodyToMono(ResponseBase.class)
				.block();		
		log.debug("Risposta backend: " + resp.getMsg());

		return "redirect:/listAbbonamenti?id=" + socioID;
	}
	
	@PostMapping("removeAttivitaAbbonamento")
	public String removeAttivitaAbbonamento(@RequestParam(required = true) Integer abbonamentoID,
			@RequestParam(required = true) Integer attivitaID, @RequestParam(required = true) Integer socioID) {
		
		AttivitaReq req= new AttivitaReq();
		ResponseBase resp = new ResponseBase();
		req.setAbbonamentiId(abbonamentoID);
		req.setId(attivitaID);
		
		resp = clientWeb.post()
				.uri("attivita/removeAttivitaAbbonamento")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(req)
				.retrieve()
				.bodyToMono(ResponseBase.class)
				.block();		
		log.debug("Risposta backend: " + resp.getMsg());

		return "redirect:/listAbbonamenti?id=" + socioID;
	}
}
