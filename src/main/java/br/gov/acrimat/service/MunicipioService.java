package br.gov.acrimat.service;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.criteria.Predicate;

import br.gov.acrimat.model.Estado;
import br.gov.acrimat.model.Municipio;
import br.gov.acrimat.modelutils.impl.ModelDao;

@Named
@RequestScoped
public class MunicipioService extends ModelDao<Municipio> {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private EstadoService service;

	@PostConstruct
	public void init() {
		setClazz(Municipio.class);
	}
	
	public List<Municipio> getMunicipiosByEstado(Estado estado) {
		return getMunicipiosByEstado(estado.getNome());
	}
	
	public List<Municipio> getMunicipiosByEstado(String nomeEstado) {
		Estado estado = service.getEstadoByNome(nomeEstado);
		Predicate predicate = getBuilder().equal(getRoot().get("estado"), estado.getNome());
		return listar(predicate, getBuilder().asc(getRoot().get("nome")));
	}
	
	public List<Municipio> getMunicipiosByCodigo(Integer codigoMunicipio) {
		Predicate predicate = getBuilder().equal(getRoot().get("codigoMunicipio"), codigoMunicipio);
		return listar(predicate, getBuilder().asc(getRoot().get("nome")));
	}
	
	public List<Municipio> getMunicipiosById(Integer id) {
		Predicate predicate = getBuilder().equal(getRoot().get("id"), id);
		return listar(predicate, getBuilder().asc(getRoot().get("nome")));
	}
	
	public Municipio getMunicipioByLocalidadeEstado(Estado estado, String nome) {
		Predicate predicateEstado = getBuilder().equal(getRoot().get("estado"), estado.getNome());
		Predicate predicateNome = getBuilder().equal(getRoot().get("nome"), nome);
		return consultar(getBuilder().and(predicateEstado, predicateNome));
	}
}