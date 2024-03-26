package br.gov.acrimat.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.overlaypanel.OverlayPanel;
import org.talesolutions.cep.CEP;
import org.talesolutions.cep.CEPNaoEncontradoException;
import org.talesolutions.cep.CEPServiceFailureException;

import br.gov.acrimat.dto.AssociadoDTO;
import br.gov.acrimat.enums.TipoTelefone;
import br.gov.acrimat.lazy.LazyModelAssociado;
import br.gov.acrimat.model.Associado;
import br.gov.acrimat.model.Estado;
import br.gov.acrimat.model.Municipio;
import br.gov.acrimat.model.MunicipioAtividade;
import br.gov.acrimat.model.Telefone;
import br.gov.acrimat.modelutils.impl.ModelBean;
import br.gov.acrimat.service.AssociadoService;
import br.gov.acrimat.service.BuscaCepService;
import br.gov.acrimat.service.EstadoService;
import br.gov.acrimat.service.MunicipioService;
import br.gov.acrimat.service.RegiaoService;
import br.gov.acrimat.util.Mensagem;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jbosslog.JBossLog;

@Named
@JBossLog
@SessionScoped
public class AssociadoBean extends ModelBean {

	private static final long serialVersionUID = 1L;

	@Inject
	private Mensagem mensagem;

	@Inject
	private AssociadoService service;
	
	@Inject
	private MunicipioService municipioService;
	
	@Inject
	private EstadoService estadoService;
	
	@Inject
	private BuscaCepService buscaCepService;
	
	@Inject
	private RegiaoService regiaoService;

	@Getter
	@Setter
	private Associado associado;

	@Getter
	@Setter
	private boolean cnpj;

	@Getter
	@Setter
	private String statusAssociado = "true";
	
	@Getter
	@Setter
	private Telefone telefone;
	
	@Getter
	@Setter
	private Municipio municipio;
	
	@Setter 
	@Getter
	private MunicipioAtividade municipioAtividade;
	
	@Getter
	private Estado estadoReferencia;
	
	@Getter
	@Setter
	private OverlayPanel overlayPanel;

	@Getter
	private LazyModelAssociado associados;

	@Getter
	@Setter
	private List<AssociadoDTO> associadosFiltrados;
	
	@Getter
	private List<Municipio> municipios;
	
	@Getter
	private List<Municipio> municipiosReferencia;
	
	@Getter
	private List<Estado> estados;
	
	@Getter @Setter
	private boolean sefaz = false;
	
	@Inject
	private RegiaoBean regiaoBean;
	
	private String growl = "form:growl";
	
	@PostConstruct
	public void init() {
		associado = new Associado();
		limpar();
		
		try {
			estados = estadoService.getEstados();
		} catch(Exception e) {
			log.error(e.getLocalizedMessage(), e);
			mensagem.error(e.getLocalizedMessage());
		}
	}

	public void inicializarAssociado(AssociadoDTO associado) {		
		try {
			limpar();
			this.associado = service.buscarAssociadoCompleto(associado.getId());
			popularDependenciaAssociado();
			
			PrimeFaces.current().executeScript("PF('dialog').show()");
		} catch (Exception e) {
			log.error(e.getLocalizedMessage(), e);
			mensagem.error(e.getLocalizedMessage());
		}
	}
	
	public void listar() {
		associados = null;
		try {
			associados = new LazyModelAssociado(service.getAssociadosByOrder("nome", statusAssociado), regiaoBean);
		} catch (Exception e) {
			log.error(e.getLocalizedMessage(), e);
			mensagem.error(e.getLocalizedMessage());
		} finally {
			resetarDataTable();
			PrimeFaces.current().executeScript("onDataTableScroll()");
		}
	}

	public void salvar() {
		
		final DataTable dataTable = getDataTable();
		final Integer index = associados.contains(associado) 
			? associados.getIndex(associado) : -1;

		try {
			associado = service.salvar(associado);
			popularDependenciaAssociado();
			
			mergeOuPersist(index);
			associado.setEditMode(false);
			removerEmCasoDeStatusDiferente(index);

			mensagem.info("info.associadosalvo");
			PrimeFaces.current().ajax().update(growl);
			PrimeFaces.current().executeScript("PF('dialog').hide()");
			PrimeFaces.current().ajax().update(dataTable.getClientId());
		} catch (Exception e) {
			mensagem.error("panelGridParentAssociado", e.getLocalizedMessage());
		}
	}

	public void novo() {
		associado = new Associado();
		buscarEstadoReferencia();
		limpar();
	}

	public void limpar() {
		telefone = new Telefone();
		municipio = new Municipio();
		
		associado.setEditMode(true);
		associado.setTelefones(new ArrayList<>());
		associado.setMunicipiosAtividade(new ArrayList<>());
		
		//Necessario para limpar referencia do linkMunicipioAtividade
		if(overlayPanel != null) {			
			overlayPanel.setFor("");
		}
	}

	public void adicionarTelefone() {
		try {
			if (telefone.getTipoTelefone() == null) {
				throw new NullPointerException();
			}
			telefone.setAssociado(associado);
			associado.getTelefones().add(telefone);
			telefone = new Telefone();
		} catch (NullPointerException e) {
			mensagem.error("form:tabViewAssociado:selectOneMenuTipoTelefone", "error.campoobrigatorio");
		}
	}
	
	public void removerTelefone(Telefone telefone) {
		associado.getTelefones().remove(telefone);
	}
	
	public void adicionarMunicipioAtividade() {
		if(associado.getMunicipiosAtividade() == null) {
			associado.setMunicipiosAtividade(new ArrayList<>());
		}
		associado.getMunicipiosAtividade().add(new MunicipioAtividade(null, municipio, associado));
		municipio = new Municipio();
	}
	
	public boolean compararMunicipioAtividade(Municipio municipio) {
		if(associado.getMunicipiosAtividade() == null) {
			return false;
		}
		final List<Municipio> municipios = new ArrayList<>();
		for(MunicipioAtividade municipioAtividade : associado.getMunicipiosAtividade()) {
			municipios.add(municipioAtividade.getMunicipio());
		}
		return municipios.contains(municipio);
	}

	public void mensagemQuandoEditarCancelar() {
		if(associado.getId() != null) {			
			Severity severity = associado.isEditMode() ? FacesMessage.SEVERITY_INFO : FacesMessage.SEVERITY_WARN;
			String texto = associado.isEditMode() ? "info.cancelar.editar" : "warn.editar";
			mensagem.generico(severity, growl, texto);
			PrimeFaces.current().ajax().update(growl);
		} else {
			PrimeFaces.current().executeScript("PF('dialog').hide()");
		}
	}
	
	public void buscarEnderecoPorCep() {
		try {
			if(associado.getCep() != null && !associado.getCep().isEmpty()) {
				CEP endereco = buscaCepService.buscarEndereco(associado.getCep().replace("-", ""));
				Estado estado = getEstadoByUf(endereco.getUf());
				Municipio municipio = municipioService.getMunicipioByLocalidadeEstado(estado, endereco.getLocalidade());
				municipios = municipioService.getMunicipiosByEstado(estado);
				
				associado.setEstado(estado.getNome());
				associado.setMunicipio(municipio);
				
				if(!endereco.getLogradouro().isEmpty()) {					
					associado.setEndereco(endereco.getLogradouro());
				}
				if(!endereco.getBairro().isEmpty()) {					
					associado.setBairro(endereco.getBairro());
				}
			}
		} catch(CEPNaoEncontradoException e) {
			mensagem.error("form:tabViewAssociado:panelGridEndereco", "error.cepnaoencontrado");
		} catch(CEPServiceFailureException e) {
			mensagem.error("form:tabViewAssociado:panelGridEndereco", "error.buscacepindisponivel");
		}
	}

	public List<TipoTelefone> getTipoTelefones() {
		return Arrays.asList(TipoTelefone.values());
	}
	
	public void listarMunicipioPorEstado() {
		if(associado.getEstado() != null && !associado.getEstado().isEmpty()) {
			municipios = municipioService.getMunicipiosByEstado(associado.getEstado());
		}
	}
	
	public void excluirAssociado(AssociadoDTO associado) {
		final DataTable dataTable = getDataTable();
		try {
			service.excluirAssociado(associado);
			listar();
			PrimeFaces.current().ajax().update(dataTable.getClientId());
			mensagem.info(growl, "info.associado.excluido");
		} catch (Exception e) {
			mensagem.error(growl, e.getLocalizedMessage());
		}finally {
			PrimeFaces.current().ajax().update(growl);
		}
	}
	
	private void popularDependenciaAssociado() {
		verificarCpfCnpj();
		listarMunicipioPorEstado();
		buscarEstadoReferencia();
		buscarRegioesAssociado();
	}
	
	private void buscarRegioesAssociado() {
		if(associado != null && associado.getMunicipiosAtividade() != null) {
			associado.setCodigoRegioes(regiaoService.selecionarRegioesAssociado(associado));
		}
	}

	private void buscarEstadoReferencia() {
		try {
			estadoReferencia = estadoService.getEstadoById(11);
			municipiosReferencia = municipioService.getMunicipiosByEstado(estadoReferencia);
		} catch (Exception e) {
			mensagem.error("panelGridParentAssociado", e.getLocalizedMessage());
		}
	}

	private void resetarDataTable() {
		DataTable dataTable = getDataTable();
		dataTable.reset();
		dataTable.clearInitialState();
		dataTable.clearLazyCache();
	}
	
	private void verificarCpfCnpj() {
		if (associado == null || associado.getCpfCnpj() == null || associado.getCpfCnpj().isEmpty()) {
			cnpj = false;
		} else {
			cnpj = associado.getCpfCnpj().replaceAll("[^0-9]", "").length() > 11;
		}
	}

	private void mergeOuPersist(final int index) {
		if (index != -1) {
			associados.alterar(index, associado);
		} else {
			associados.adicionar(associado);
		}
	}

	private void removerEmCasoDeStatusDiferente(final Integer index) {
		final boolean diferente = statusAssociado != null 
				&& !associado.getAtivo().equals(Boolean.valueOf(statusAssociado));
		if (diferente && associados.contains(associado)) {
			final Boolean removido = associados.remover(associado);
			if(removido.equals(Boolean.FALSE)) {
				associados.remover(index);
			}
			resetarDataTable();
		}
	}

	private DataTable getDataTable() {
		return (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("form:dataTable");
	}

	private Estado getEstadoByUf(final String Uf) {
		for(Estado estado : estados) {
			if(estado.getUf().equals(Uf)) {
				return estado;
			}
		}
		
		return null;
	}
}
