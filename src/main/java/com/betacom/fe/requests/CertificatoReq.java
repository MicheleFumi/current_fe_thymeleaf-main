package com.betacom.fe.requests;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CertificatoReq {
	private Integer id;
	private Boolean tipo;  //false normale true agonistico
	private LocalDate dataCertificato;
	private Integer socioId;
	private String errorMsg;

}
