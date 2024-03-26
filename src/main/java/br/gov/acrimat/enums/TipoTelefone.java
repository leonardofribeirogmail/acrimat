package br.gov.acrimat.enums;

public enum TipoTelefone {

	FIXO(0, "Fixo", "(99)9999-9999"),
	CELULAR(1, "Celular", "(99)99999-9999");
	
	private Integer valor; 
	private String nome;
	private String mascara;
	
	private TipoTelefone(Integer valor, String nome, String mascara) {
		this.valor = valor;
		this.nome = nome;
		this.mascara = mascara;
	}

	public Integer getValor() {
		return valor;
	}

	public String getNome() {
		return nome;
	}
	
	public String getMascara() {
		return mascara;
	}
	
	@Override
	public String toString() {
		return nome;
	}
}
