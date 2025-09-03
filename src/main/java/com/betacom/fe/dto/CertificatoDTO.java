package com.betacom.fe.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Builder
@Getter
@Setter
public class CertificatoDTO {

	private Integer id;
	private Boolean tipo;  //false normale true agonistico
	private LocalDate dataCertificato;
	public String getDataCertificatoFormatted() {
        return dataCertificato != null
            ? dataCertificato.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            : "";
    }
}
