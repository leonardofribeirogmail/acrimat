package br.gov.acrimat.model;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.gov.acrimat.converter.TipoTelefoneConverter;
import br.gov.acrimat.enums.TipoTelefone;
import br.gov.acrimat.modelutils.ModelEntidade;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "telefone", 
	uniqueConstraints = {@UniqueConstraint(columnNames = {"numero", "codigoassociado", "tipotelefone"})})
@EqualsAndHashCode(callSuper = false)
public class Telefone extends ModelEntidade {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "numero", nullable = false)
	private String numero;
	
	@Column(name = "tipotelefone", nullable = false)
	@Convert(converter = TipoTelefoneConverter.class)
	private TipoTelefone tipoTelefone;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="codigoassociado", updatable = false, nullable = false)
	private Associado associado;
	
	public Telefone getNewInstance() {
		return new Telefone();
	}
}
