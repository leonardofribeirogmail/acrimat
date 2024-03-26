package br.gov.acrimat.enums;

import lombok.Getter;

public enum Status {

	ATIVO("Ativo", Boolean.TRUE.toString()),
	INATIVO("Inativo", Boolean.FALSE.toString()),
	AMBOS("Ambos", "ambos");
	
	@Getter
	private String nome;
	
	@Getter
	private String value;
	
	Status(final String nome, final String value) {
		this.nome = nome;
		this.value = value;
	}
	
	public static Status valueOf(final Object value) {
		for(Status status : Status.values()) {
			if(status.value.equals(String.valueOf(value))) {
				return status;
			}
		}
		
		return Status.AMBOS;
	}
}
