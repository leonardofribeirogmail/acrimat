package br.gov.acrimat.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import br.gov.acrimat.modelutils.ModelEntidade;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Leo/Will
 * Armazena os filtros salvos pelo usuario no relatorio
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "relatorio")
@EqualsAndHashCode(callSuper = false)
public class Relatorio extends ModelEntidade {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "codigorelatorio")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "codigorelatorio")
	private List<OpcaoRelatorio> opcoes;
	
	@JoinColumn(name = "codigousuario")
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	private Usuario usuario;
}
