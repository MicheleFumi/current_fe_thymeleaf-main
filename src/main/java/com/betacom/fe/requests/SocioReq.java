package com.betacom.fe.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
public class SocioReq {
	private Integer id;
	private String cognome;
	private String nome;
	private String codiceFiscale;
	private String email;
	private String attivita;
	private String abbonamento;
	private String errrorMsg;
}
