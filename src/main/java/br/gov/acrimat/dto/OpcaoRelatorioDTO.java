package br.gov.acrimat.dto;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.gov.acrimat.enums.OpcaoRelatorioEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class OpcaoRelatorioDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Getter @Setter
	private Long id;
	
	@Getter @Setter
	private OpcaoRelatorioEnum opcao;
	
	public OpcaoRelatorioDTO(OpcaoRelatorioEnum opcao) {
		this.opcao = opcao;
	}
	
	public static OpcaoRelatorioDTO novaOpcao(OpcaoRelatorioEnum opcao) {
		return new OpcaoRelatorioDTO(opcao);
	}
	
	@Override
	public String toString() {
		final ObjectMapper mapper = new ObjectMapper();
		try {
			String writeValueAsString = mapper.writeValueAsString(this);
			return writeValueAsString;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((opcao == null) ? 0 : opcao.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OpcaoRelatorioDTO other = (OpcaoRelatorioDTO) obj;
		if (opcao != other.opcao)
			return false;
		return true;
	}

}
