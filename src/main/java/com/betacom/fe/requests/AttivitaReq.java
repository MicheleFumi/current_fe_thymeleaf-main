package com.betacom.fe.requests;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttivitaReq {
	private Integer id;
	private String descrizione;
	private BigDecimal prezzo;
	private Integer abbonamentiId;
	private String errorMsg;
}
