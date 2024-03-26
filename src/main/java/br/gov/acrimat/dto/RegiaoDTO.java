package br.gov.acrimat.dto;

import java.util.Objects;

import br.gov.acrimat.modelutils.ModelEntidade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegiaoDTO extends ModelEntidade {

	private static final long serialVersionUID = 1L;
	
	@Getter @Setter
	private Integer codigoRegiao;

	@Getter @Setter
	private String nome;

	@Override
	public int hashCode() {
		return Objects.hash(codigoRegiao, nome);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RegiaoDTO other = (RegiaoDTO) obj;
		return Objects.equals(codigoRegiao, other.codigoRegiao) && Objects.equals(nome, other.nome);
	}
	
	
}
