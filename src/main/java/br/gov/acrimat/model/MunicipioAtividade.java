package br.gov.acrimat.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.gov.acrimat.modelutils.ModelEntidade;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "municipio_atividade")
public class MunicipioAtividade extends ModelEntidade {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id") @Getter @Setter
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne @Getter @Setter
	@JoinColumn(name = "codigomunicipio")
	private Municipio municipio;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY) @Getter @Setter
	@JoinColumn(name="codigoassociado", updatable = false, nullable = false)
	private Associado associado;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MunicipioAtividade other = (MunicipioAtividade) obj;
		if (municipio == null) {
			if (other.municipio != null)
				return false;
		} else if (!municipio.equals(other.municipio))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((municipio == null) ? 0 : municipio.hashCode());
		return result;
	}
}
