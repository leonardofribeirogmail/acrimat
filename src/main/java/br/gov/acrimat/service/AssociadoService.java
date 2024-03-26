package br.gov.acrimat.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.Tuple;
import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;

import org.hibernate.Hibernate;

import br.gov.acrimat.dto.AssociadoDTO;
import br.gov.acrimat.exception.AssociadoException;
import br.gov.acrimat.exception.AssociadoTelefoneException;
import br.gov.acrimat.exception.ChaveUnicaException;
import br.gov.acrimat.exception.ModelServiceException;
import br.gov.acrimat.model.Associado;
import br.gov.acrimat.modelutils.impl.ModelDao;
import br.gov.acrimat.util.StringUtil;
import lombok.extern.jbosslog.JBossLog;

@Named
@JBossLog
@RequestScoped
public class AssociadoService extends ModelDao<Associado> {

	private static final long serialVersionUID = 1L;

	@Inject
	private ColumnService service;
	
	@Inject
	private ArquivoService arquivoService;

	@PostConstruct
	public void init() {
		setClazz(Associado.class);
	}

	public List<AssociadoDTO> getAssociados(final String status) {
		return buscarAssociados("nome", status);
	}

	public List<AssociadoDTO> getAssociadosByOrder(final String order, final String status) {
		return buscarAssociados(order, status);
	}

	private List<AssociadoDTO> buscarAssociados(final String order, final String status) {
		List<Tuple> tuples = service.buscarAssociados(order, status);
		List<AssociadoDTO> associados = new ArrayList<>();

		tuples.forEach(row -> {
			preencherAssociado(associados, row);
		});

		return associados;
	}

	public Associado salvar(final Associado associado) {
		try {
			associado.setNome(StringUtil.capitalize(associado.getNome()));
			consultarAssociadoJaExistente(associado);
			return merge(associado);
		} catch (AssociadoException e) {
			throw new AssociadoException(e.getLocalizedMessage());
		} catch (ChaveUnicaException e) {
			log.error(e.getLocalizedMessage(), e);
			throw new AssociadoTelefoneException(getProvider().getMensagem("error.telefone.repetido"), e);
		} catch (ModelServiceException e) {
			log.error(e.getLocalizedMessage(), e);
			throw new AssociadoException(getProvider().getMensagem("error.salvar"), e);
		}
	}

	@Transactional
	public Associado buscarAssociadoCompleto(Long id) {
		final Predicate predicate = getBuilder().equal(getRoot().get("id"), id);
		try {
			getCriteriaQuery().select(getRoot());
			Associado associado = consultar(predicate);

			Hibernate.initialize(associado.getTelefones());
			Hibernate.initialize(associado.getArquivos());
			Hibernate.initialize(associado.getMunicipiosAtividade());

			return associado;
		} catch (ModelServiceException e) {
			log.error(e.getLocalizedMessage(), e);
			throw new AssociadoException(getProvider().getMensagem("error.falhabuscarlista"), e);
		}
	}

	public void consultarAssociadoJaExistente(Associado associado) {
		Predicate predicate = getBuilder().equal(getRoot().get("cpfCnpj"), associado.getCpfCnpj());
		try {
			List<Associado> associados = listar(predicate);
			if (associados.size() > 1 && associado.getId() == null) {
				throw new AssociadoException();
			} else if (associados.size() == 1) {
				Associado associadoAntigo = associados.get(0);
				if (!associadoAntigo.getId().equals(associado.getId())) {
					throw new AssociadoException();
				}
			}
		} catch (AssociadoException e) {
			throw new AssociadoException("error.associadoduplicado");
		}
	}

	public void excluirAssociado(AssociadoDTO associado) {
		try {
			arquivoService.excluirArquivos(associado);
			if (!excluir(associado.getId())) {
				throw new AssociadoException();
			}
		} catch (Exception e) {
			log.error(e);
			throw new AssociadoException("error.excluir.associado");
		}
	}

	private String getData(Date data) {
		final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		if (data != null) {
			return formatter.format(data);
		}
		return "";
	}

	private void preencherAssociado(List<AssociadoDTO> associados, Tuple row) {
		AssociadoDTO associado = new AssociadoDTO((Long) row.get(0), (String) row.get(1), (String) row.get(2),
				(String) row.get(3), getData((Date) row.get(4)), new ArrayList<>(), new ArrayList<>());

		if (associados.contains(associado)) {
			associado = associados.get(associados.indexOf(associado));
		} else {
			associados.add(associado);
		}

		final String municipioAtividade = (String) row.get(5);
		if (municipioAtividade != null && !municipioAtividade.isEmpty()
			&& !associado.getMunicipiosAtividade().contains(municipioAtividade)) {
			associado.getMunicipiosAtividade().add(municipioAtividade);
		}
		
		final Integer codigoRegiao = (Integer) row.get(6);
		if (codigoRegiao != null && codigoRegiao > 0 
			&& !associado.getCodigoRegioes().contains(codigoRegiao)) {
			associado.getCodigoRegioes().add(codigoRegiao);
		}
	}
}
