package br.gov.acrimat.dto;

import java.util.Objects;
import java.util.Set;

import br.gov.acrimat.model.MunicipioAtividade;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class AssociadoRelDTO extends AssociadoBaseDTO {

	private static final long serialVersionUID = 1L;

	@Getter @Setter
	private String sexo;
	
	@Getter @Setter
	private String dataNascimento;
	
	@Getter @Setter
	private String email;
	
	@Getter @Setter
	private String motivonaoassociado;
	
	@Getter @Setter
	private String municipioEleitoral;
	
	@Getter @Setter
	private String regiao;
	
	@Getter @Setter
	private String endereco;
	
	@Getter @Setter
	private String complemento;
	
	@Getter @Setter
	private String bairro;
	
	@Getter @Setter
	private String cep;
	
	@Getter @Setter
	private String municipio;
	
	@Getter @Setter
	private String estado;
	
	@Getter @Setter
	private Set<String> telefones;
	
	@Getter @Setter
	private Set<MunicipioAtividade> municipiosAtividade;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(dataNascimento, email, motivonaoassociado, municipioEleitoral, regiao, sexo);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		AssociadoRelDTO other = (AssociadoRelDTO) obj;
		return Objects.equals(dataNascimento, other.dataNascimento) && Objects.equals(email, other.email)
				&& Objects.equals(motivonaoassociado, other.motivonaoassociado)
				&& Objects.equals(municipioEleitoral, other.municipioEleitoral)
				&& Objects.equals(regiao, other.regiao) && Objects.equals(sexo, other.sexo);
	}
}
