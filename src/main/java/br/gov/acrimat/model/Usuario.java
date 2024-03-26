package br.gov.acrimat.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.gov.acrimat.modelutils.ModelEntidade;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
@EqualsAndHashCode(callSuper = false)
public class Usuario extends ModelEntidade {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name")
	private String nome;

	@Column(name = "email")
	private String email;
	
	@Column(name = "password")
	private String senha;

	@Column(name = "remember_token")
	private String token;

	@Column(name = "created_at")
	private Date dataCriacao;

	@Column(name = "updated_at")
	private Date dataAtualizacao;
}
