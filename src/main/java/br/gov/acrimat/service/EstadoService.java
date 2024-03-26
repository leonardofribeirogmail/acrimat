package br.gov.acrimat.service;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.persistence.criteria.Predicate;

import br.gov.acrimat.model.Estado;
import br.gov.acrimat.modelutils.impl.ModelDao;

@Named
@RequestScoped
public class EstadoService extends ModelDao<Estado> {

	private static final long serialVersionUID = 1L;

	@PostConstruct
	public void init() {
		setClazz(Estado.class);
	}

	public List<Estado> getEstados() {
		return listar(null, getBuilder().asc(getRoot().get("nome")));
	}

	public Estado getEstadoByNome(String nome) {
		Predicate predicate = getBuilder().equal(getRoot().get("nome"), nome);
		return consultar(predicate);
	}

	public Estado getEstadoById(Integer id) {
		Predicate predicate = getBuilder().equal(getRoot().get("id"), id);
		return consultar(predicate);
	}
}
