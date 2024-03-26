package br.gov.acrimat.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.ListIndexBase;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import br.gov.acrimat.converter.BooleanConverter;
import br.gov.acrimat.dto.AssociadoDTO;
import br.gov.acrimat.listener.AssociadoListener;
import br.gov.acrimat.modelutils.ModelEntidade;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "associados", indexes = {@Index(columnList = "codigoassociado, associado")})
@EntityListeners(AssociadoListener.class)
@EqualsAndHashCode(callSuper = false)
public class Associado extends ModelEntidade {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "codigoassociado")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "socio", length = 1, nullable = true)
	private String socio;

	@Temporal(TemporalType.DATE)
	@Column(name = "datafiliacao")
	private Date dataFiliacao;

	@Column(name = "dataatualizacao")
	private Date dataAtualizacao;

	@Convert(converter = BooleanConverter.class)
	@Column(name = "falecido", nullable = true)
	private Boolean falecido = false;

	@Column(name = "associado")
	private String nome;

	@Column(name = "cnpjcpf", unique = true)
	private String cpfCnpj;

	@Column(name = "rg_oexp")
	private String rg;

	@Column(name = "estadocivil")
	private String estadoCivil;

	@Column(name = "sexo")
	private String sexo;

	@Column(name = "enderecocorrespondencia")
	private String endereco;

	@Column(name = "complemento")
	private String complemento;

	@Column(name = "bairro")
	private String bairro;

	@Column(name = "cep")
	private String cep;

	@Column(name = "fone1")
	private String telefone1;

	@Column(name = "fone2")
	private String telefone2;

	@Column(name = "celular")
	private String celular;

	@Column(name = "email")
	private String email;

	@Column(name = "origemdocadastro")
	private String origemDoCadastro;

	@ManyToOne @NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "codigomunicipio")
	private Municipio municipio;
	
	@ManyToOne @NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "codmunicipioeleitoral")
	private Municipio municipioEleitoral;

	@Column(name = "proprieade_ie_princip")
	private String propriedadeIePrincipal;

	@Column(name = "datanascimento")
	private Date dataNascimento;

	@Column(name = "estado")
	private String estado;

	@Convert(converter = BooleanConverter.class)
	@Column(name = "ativo", nullable = true)
	private Boolean ativo = true;

	@Column(name = "condicaoassociado")
	private String condicaoassociado;

	@Column(name = "localfiliacao")
	private String localFiliacao;

	@Column(name = "celnew")
	private String celnew;

	@Column(name = "codigoprofissao")
	private Integer codigoprofissao;

	@Column(name = "numeroassociado")
	private Integer numeroassociado;

	@ListIndexBase(value = 0)
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "codigoassociado")
	private List<Telefone> telefones = new ArrayList<>();
	
	@ListIndexBase(value = 1)
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "codigoassociado")
	private List<Arquivo> arquivos = new ArrayList<>();
	
	@ListIndexBase(value = 2)
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "codigoassociado")
	private List<MunicipioAtividade> municipiosAtividade = new ArrayList<>();

	/**
	 * Ha uma mensagem no banco de dados dizendo que esse atributo deveria ser
	 * 'eassociado'
	 */
	@Convert(converter = BooleanConverter.class)
	@Column(name = "naoassociado")
	private Boolean eassociado = true;

	@Column(name = "motivonaoassociado", nullable = false)
	private String motivonaoassociado;

	@Transient
	private boolean editMode = false;
	
	@Transient
	private List<Integer> codigoRegioes;

	public AssociadoDTO converterParaDTO() {
		return new AssociadoDTO(id, nome, cpfCnpj, rg, 
			getData(dataFiliacao), extrairMunicipiosAtividade(), codigoRegioes);
	}

	private List<String> extrairMunicipiosAtividade() {
		final List<String> municipiosAtividade = new ArrayList<>();
		
		if(municipiosAtividade != null) {
			for(MunicipioAtividade municipioAtividade : this.municipiosAtividade) {
				municipiosAtividade.add(municipioAtividade.getMunicipio().getNome());
			}
		}
		return municipiosAtividade;
	}
	
	private String getData(Date data) {
		final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		
		if(data != null) {
			return formatter.format(data);
		}
		
		return "";
	}
}
