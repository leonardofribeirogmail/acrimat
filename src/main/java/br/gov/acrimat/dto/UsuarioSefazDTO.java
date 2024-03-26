package br.gov.acrimat.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsuarioSefazDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String inscricaoEstadual;
	private String nome;
	private String status;
}
