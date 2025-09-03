package com.betacom.fe.requests;

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
@Setter
public class UtenteReq {
	private Integer id;
	private String userName;
	private String pwd;
	private String email;
	private String role;
}
