package br.gov.acrimat.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class AssociadoDTO extends AssociadoBaseDTO {

	private static final long serialVersionUID = 1L;
	
	@Getter @Setter
	private List<String> municipiosAtividade;
	
	@Getter @Setter
	private List<Integer> codigoRegioes;
	
	public AssociadoDTO(Long id, String nome, String cpfCnpj, String rg, String data,
			List<String> municipiosAtividade, List<Integer> codigoRegioes) {
		this.id = id;
		this.nome = nome;
		this.cpfCnpj = cpfCnpj;
		this.rg = rg;
		this.dataFiliacao = data;
		this.municipiosAtividade = municipiosAtividade;
		this.codigoRegioes = codigoRegioes;
	}
}
