package com.betacom.fe.controller;


import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.ModelAndView;

import com.betacom.fe.configuration.UtenteServices;
import com.betacom.fe.dto.UtenteDTO;

import com.betacom.fe.requests.UtenteReq;
import com.betacom.fe.response.ResponseBase;
import com.betacom.fe.response.ResponseList;
import com.betacom.fe.response.ResponseObject;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
public class UtenteController {

	private WebClient clientWeb;
	private UtenteServices utS;
	public UtenteController(WebClient clientWeb, UtenteServices utS) {
		this.clientWeb = clientWeb;
		this.utS = utS;
	}
	
	@GetMapping("/listUtente")
	public ModelAndView listUtente() {
		
		ModelAndView mav = new ModelAndView("listUtente");
		
		
		ResponseList<UtenteDTO> ut = clientWeb.get()
				.uri("utente/list")
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<ResponseList<UtenteDTO>>() {})
				.block();
		
		
		mav.addObject("listUt",ut.getDati());
		mav.addObject("utente",new UtenteReq());
		
		return mav;
	}
	
	@GetMapping("createUtente")
	public ModelAndView createUtente() {
		ModelAndView mav = new ModelAndView("createUtente");
		UtenteReq r = new UtenteReq();
		mav.addObject("errorMSG", null);
		mav.addObject("utente", r);
		
		return mav;
	}
	
	
	
	@GetMapping("updateUtente")
	public ModelAndView updateUtente(@RequestParam(required = true) Integer id) {
		
		UtenteDTO resp = clientWeb.get()
				.uri(uriBuilder -> uriBuilder
						.path("utente/findById")
						.queryParam("id", id)
						.build())
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<ResponseObject<UtenteDTO>>() {})
				.block()
				.getDati();
		
		UtenteReq u = UtenteReq.builder()
				.id(resp.getId())
				.userName(resp.getUserName())
				.pwd(resp.getPwd())
				.email(resp.getEmail())
				.role(resp.getRole())
				.build();
		
		log.debug(resp.getUserName());
		
		ModelAndView mav = new ModelAndView("createUtente");
		mav.addObject("utente", u);
		
		return mav;
	}
	
	@PostMapping("saveUtente")
	public Object saveUtente(@ModelAttribute("utente") UtenteReq req ) {
		log.debug("creazione utente: "+req);
		String operation = (req.getId() == null) ? "create" : "update";
		
		ResponseBase resp = null;
		String uri = "utente/" + operation;	
		HttpMethod typeM = "create".equalsIgnoreCase(operation) ? HttpMethod.POST : HttpMethod.PUT;
		
		resp = clientWeb.method(typeM)
				.uri(uri)
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(req)
				.retrieve()
				.bodyToMono(ResponseBase.class)
				.block();			
	
		if (!resp.getRc()){
			ModelAndView mav = new ModelAndView("createUtente");
			mav.addObject("utente", req);
			mav.addObject("errorMSG", resp.getMsg());
			return mav;
		}
		
		utS.updateUtente(req);
		return "redirect:/listUtente";
	}
	
	
	@PostMapping("removeUtente")
	public Object removeUtente(@RequestParam(required = true) Integer id) {
		
		UtenteReq req = new UtenteReq();
		ResponseBase resp = new ResponseBase();
		
		if(id!= null) {
		req.setId(id);

	       resp = clientWeb.post()
	            .uri("utente/delete")
	            .contentType(MediaType.APPLICATION_JSON)
	            .bodyValue(req)
	            .retrieve()
	            .bodyToMono(ResponseBase.class)
	            .block();

		}
		if (!resp.getRc()) {
			ModelAndView mav = new ModelAndView("createUtente");
			mav.addObject("req", req);
			mav.addObject("errorMSG", resp.getMsg());
			return mav;
		} 
		utS.removeUtente(resp.getMsg());
		return "redirect:/listUtente";
	}
	
	@GetMapping("register")
	public ModelAndView register() {
		ModelAndView mav = new ModelAndView("register");
		UtenteReq r = new UtenteReq();
		mav.addObject("errorMSG", null);
		mav.addObject("utente", r);
		
		return mav;
	}
	
	@PostMapping("register/saveUtenteNonLoggato")
	public Object saveUtenteNonLoggato(@ModelAttribute("utente") UtenteReq req ) {
		log.debug("creazione utente: "+req);
		
		ResponseBase resp = null;
		
		req.setRole("USER");
		
		resp = clientWeb.post()
				.uri("utente/create")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(req)
				.retrieve()
				.bodyToMono(ResponseBase.class)
				.block();			
	
		if (!resp.getRc()){
			ModelAndView mav = new ModelAndView("createUtente");
			mav.addObject("utente", req);
			mav.addObject("errorMSG", resp.getMsg());
			return mav;
		}
		
		utS.updateUtente(req);
		return "redirect:/login";
	}
	
}
