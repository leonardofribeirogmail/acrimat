package br.gov.acrimat.enums;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;

public enum OpcaoRelatorioEnum {

	NOME(0, "Nome", "nome", Boolean.TRUE, Boolean.TRUE),
	RG(1, "RG", "rg", Boolean.TRUE, Boolean.TRUE),
	CPF(2, "CPF/CNPJ", "cpfCnpj", Boolean.TRUE, Boolean.TRUE),
	DTNASCIMENTO(3, "Data de nascimento", "dataNascimento", Boolean.TRUE, Boolean.TRUE),
	SEXO(4, "Sexo", "sexo", Boolean.TRUE, Boolean.TRUE),
	TELEFONE(5, "Telefone", "telefone", Boolean.FALSE, Boolean.TRUE),
	DTASSOCIACAO(6, "Data de filiação", "dataFiliacao", Boolean.TRUE, Boolean.TRUE),
	EMAIL(7, "E-mail", "email", Boolean.TRUE, Boolean.TRUE),
	MOTIVO(8, "Motivo não associado", "motivonaoassociado", Boolean.TRUE, Boolean.TRUE),
	ATIVIDADE(10, "Município de Atividade", "municipiosAtividade", Boolean.FALSE, Boolean.TRUE),
	REGIAOATIVIDADE(11, "Região de Atividade", "regiao", Boolean.TRUE, Boolean.TRUE),
	ID(12, "ID", "id", Boolean.TRUE, Boolean.FALSE),
	ENDERECO(13, "Endereço", "endereco", Boolean.TRUE, Boolean.TRUE),
	COMPLEMENTO(14, "Endereço Complemento", "complemento", Boolean.TRUE, Boolean.TRUE),
	BAIRRO(15, "Bairro", "bairro", Boolean.TRUE, Boolean.TRUE),
	CEP(16, "CEP", "cep", Boolean.TRUE, Boolean.TRUE),
	MUNICIPIO(17, "Município", "municipio.nome", Boolean.TRUE, Boolean.TRUE),
	ESTADO(18, "Estado", "municipio.estado", Boolean.TRUE, Boolean.TRUE);
	
	@Getter
	private Integer codigo;
	
	@Getter
	private String nome;
	
	@Getter
	private String valorDb;
	
	@Getter
	private Boolean acessoBanco;
	
	@Getter
	private Boolean telaSelecao; 
	
	private OpcaoRelatorioEnum(final Integer codigo, 
			final String nome, 
			final String valorDb,
			final Boolean acessoBanco,
			final Boolean telaSelecao) {
		this.codigo = codigo;
		this.nome = nome;
		this.valorDb = valorDb;
		this.acessoBanco = acessoBanco;
		this.telaSelecao = telaSelecao;
	}
	
	public static OpcaoRelatorioEnum getEnumByNome(final String nome) {
		for(OpcaoRelatorioEnum opcao : OpcaoRelatorioEnum.values()) {
			if(opcao.getNome().equalsIgnoreCase(nome)
				|| opcao.name().equalsIgnoreCase(nome)) {
				return opcao;
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		final ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(this);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return StringUtils.EMPTY;
	}
}