package br.gov.acrimat.model;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.StringUtils;

import br.gov.acrimat.modelutils.ModelEntidade;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "arquivos", uniqueConstraints = {@UniqueConstraint(columnNames = {"url", "nome", "codigoassociado"})})
@EqualsAndHashCode(callSuper = false)
public class Arquivo extends ModelEntidade {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Getter @Setter
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Getter @Setter
	@Column(name = "url")
	private String url;
	
	@Setter
	@Column(name = "nome")
	private String nome;
	
	@Getter @Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="codigoassociado", updatable = false, nullable = false)
	private Associado associado;
	
	public String getNome() {
		if(StringUtils.isBlank(nome)) {
			return "";
		}
		return URLDecoder.decode(nome, StandardCharsets.UTF_8);
	}
}
