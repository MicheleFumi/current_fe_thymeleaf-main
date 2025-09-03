package com.betacom.fe.controller;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.ModelAndView;

import com.betacom.fe.dto.CertificatoDTO;
import com.betacom.fe.dto.SocioDTO;
import com.betacom.fe.requests.CertificatoReq;
import com.betacom.fe.response.ResponseBase;
import com.betacom.fe.response.ResponseObject;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
public class CertificatoController {

	
	
	private WebClient clientWeb;

	public CertificatoController(WebClient clientWeb) {
		this.clientWeb = clientWeb;
	}

	
	@GetMapping("listCertificato")
	public ModelAndView listCertificato(@RequestParam Integer id) {
		log.debug("listCertificatoID: " + id);
		
		ModelAndView mav= new ModelAndView("listCertificato");
		
		SocioDTO cert = clientWeb.get()
				.uri(uriBuilder -> uriBuilder
						.path("socio/getSocio")
						.queryParam("id", id)
						.build())
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<ResponseObject<SocioDTO>>() {})
				.block()
				.getDati();
		
		mav.addObject("certificato", cert.getCertificato());
		mav.addObject("socioID", id);
		log.debug("listCertificato: " + cert);
		return mav;
		
	}
	
	@GetMapping("updateCertificato")
	public ModelAndView updateCertificato(@RequestParam(required = true) Integer id) {
		CertificatoReq c = clientWeb.get()
		        .uri(uriBuilder -> uriBuilder
		            .path("certificato/getCertificato")
		            .queryParam("id", id)
		            .build())
		        .retrieve()
		        .bodyToMono(new ParameterizedTypeReference<ResponseObject<CertificatoReq>>() {})
		        .block()
		        .getDati();

		    ModelAndView mav = new ModelAndView("createCertificato");
		    mav.addObject("certificato", c);
		    mav.addObject("socioID", c.getSocioId());
		    return mav;
	}
	
	@PostMapping("saveCertificato")
	public Object saveCertificato(@ModelAttribute("certificato") CertificatoReq req) {
		log.debug("saveCertificato: id=" + req.getId() + ", socioId=" + req.getSocioId());

		ResponseBase resp = null;
		
		String operation = (req.getId() == null) ? "create" : "update";
		String uri = "certificato/" + operation;
		
		if ("create".equalsIgnoreCase(operation)) {
		resp = clientWeb.post()
				.uri(uri)
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(req)
				.retrieve()
				.bodyToMono(ResponseBase.class)
				.block();	
		}else {
			resp = clientWeb.put()
					.uri(uri)
					.contentType(MediaType.APPLICATION_JSON)
					.bodyValue(req)
					.retrieve()
					.bodyToMono(ResponseBase.class)
					.block();	
		}
		
		log.debug(operation + " :" + resp.getRc());
		if (!resp.getRc()){
			ModelAndView mav = new ModelAndView("createCertificato");
			req.setErrorMsg(resp.getMsg());
			mav.addObject("certificato", req);
			return mav;
		}
		log.debug("saveCertificato:" + req);
		return "redirect:/listSocio";
	}
	
	
	@GetMapping("createCertificato")
	public ModelAndView createCertificato(@RequestParam(required = true) Integer socioID ) {
		log.debug("createCertificato id: " + socioID);
		ModelAndView mav = new ModelAndView("createCertificato");
		CertificatoReq r = new CertificatoReq();
		r.setSocioId(socioID);
		mav.addObject("socioID", socioID);
		mav.addObject("certificato", r);
		log.debug("createCertificato:" + r);
		return mav;
		
		
	}
		
}
