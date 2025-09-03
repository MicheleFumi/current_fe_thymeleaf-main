package com.betacom.fe.configuration;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Service;

import com.betacom.fe.requests.UtenteReq;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class UtenteServices {

	
	private PasswordEncoder getPasswordEncoder;
	private InMemoryUserDetailsManager InMemoryUserDetailsManager;
	
	public UtenteServices(PasswordEncoder getPasswordEncoder, InMemoryUserDetailsManager inMemoryUserDetailsManager) {
		
		this.getPasswordEncoder = getPasswordEncoder;
		InMemoryUserDetailsManager = inMemoryUserDetailsManager;
	}
	
	public void updateUtente(UtenteReq req) {
		if(InMemoryUserDetailsManager.userExists(req.getUserName())) {
			InMemoryUserDetailsManager.deleteUser(req.getUserName()); //utente deleted
			log.debug("delete " + req.getUserName());
		}
			InMemoryUserDetailsManager.createUser(
					User
					.withUsername(req.getUserName())
					.password(getPasswordEncoder.encode(req.getPwd().toString()))
					.roles(req.getRole())
					.build()
					);
		log.debug("create " + req.getUserName());
	}
	
	
	public void removeUtente(String username) {
		if(InMemoryUserDetailsManager.userExists(username)) {
			log.debug("trovato " + username);
			
			InMemoryUserDetailsManager.deleteUser(username); //utente deleted
		}
			
		log.debug("create " + username);
	}
	
}
