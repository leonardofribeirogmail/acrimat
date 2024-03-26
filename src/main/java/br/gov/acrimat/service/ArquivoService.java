package br.gov.acrimat.service;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.persistence.criteria.Predicate;

import br.gov.acrimat.dto.AssociadoDTO;
import br.gov.acrimat.exception.ArquivoException;
import br.gov.acrimat.model.Arquivo;
import br.gov.acrimat.modelutils.impl.ModelDao;
import lombok.extern.jbosslog.JBossLog;

@Named
@JBossLog
@RequestScoped
public class ArquivoService extends ModelDao<Arquivo> {

	private static final long serialVersionUID = 1L;
	
	@PostConstruct
	public void init() {
		setClazz(Arquivo.class);
	}
	
	public List<Arquivo> buscarArquivos(AssociadoDTO associado) {
		final Predicate predicate = getBuilder().equal(getRoot().get("associado").get("id"), associado.getId());
		return listar(predicate);
	}
	
	public void excluirArquivos(final AssociadoDTO associado) {
		try {
			final List<Arquivo> arquivos = buscarArquivos(associado);
			if(arquivos.size() > 0) {
				arquivos.forEach(arquivo -> {
					if(!excluir(arquivo.getId())) {
						throw new ArquivoException(null);
					}
				});
			}
		} catch (Exception e) {
			log.error(e);
			throw new ArquivoException("error.erroaoexcluirarquivo");
		}
	}
}
