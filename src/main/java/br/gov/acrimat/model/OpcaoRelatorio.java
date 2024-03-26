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

import br.gov.acrimat.converter.OpcaoRelEnumConverter;
import br.gov.acrimat.enums.OpcaoRelatorioEnum;
import br.gov.acrimat.modelutils.ModelEntidade;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "opcaorelatorio")
@EqualsAndHashCode(callSuper = false)
public class OpcaoRelatorio extends ModelEntidade {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "codigoopcaorelatorio")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Convert(converter = OpcaoRelEnumConverter.class)
	@Column(length = 30)
	private OpcaoRelatorioEnum opcao;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "codigorelatorio", nullable = true, updatable = false)
	private Relatorio relatorio;

	public OpcaoRelatorio(OpcaoRelatorioEnum opcao) {
		this.opcao = opcao;
	}
	
}
