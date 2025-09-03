package com.betacom.fe.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class InMemorySecurityConfig {

	private CustomUserDetailsServices CustomUserDetailsServices;

	public InMemorySecurityConfig(com.betacom.fe.configuration.CustomUserDetailsServices customUserDetailsServices) {
		super();
		CustomUserDetailsServices = customUserDetailsServices;
	}
	
	@Bean
	InMemoryUserDetailsManager InMemoryUserDetailsManager() {
		return CustomUserDetailsServices.lodUsers();
	}
}
