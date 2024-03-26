package br.gov.acrimat.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.gov.acrimat.model.Associado;
import br.gov.acrimat.model.Municipio;
import br.gov.acrimat.model.MunicipioAtividade;
import br.gov.acrimat.model.OpcaoRelatorio;
import br.gov.acrimat.model.Regiao;
import br.gov.acrimat.model.Relatorio;
import br.gov.acrimat.model.Usuario;
import br.gov.acrimat.security.ManagerConnection;
import lombok.extern.jbosslog.JBossLog;

@Named
@JBossLog
@RequestScoped
public class ColumnService implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private ManagerConnection connection;

	public List<String> getNomeMunicipioById(Integer id) {
		CriteriaBuilder builder = connection.getCriteriaBuilder();
		CriteriaQuery<String> criteria = builder.createQuery(String.class);
		Root<Municipio> root = criteria.from(Municipio.class);
		Predicate predicate = builder.equal(root.get("id"), id);
		
		criteria.select(root.get("nome"));
		criteria.where(predicate);
		
		try {
			return connection.createQuery(criteria).getResultList();
		} catch(Exception e) {
			log.error(e.getLocalizedMessage(), e);
		}
		
		return new ArrayList<String>();
	}
	
	public List<Tuple> buscarAssociados(final String order, final String status) {
		final CriteriaBuilder builder = connection.getCriteriaBuilder();
		final CriteriaQuery<Tuple> criteria = builder.createQuery(Tuple.class);
		
		Root<Associado> rootAssociado = criteria.from(Associado.class);
		Join<Associado, MunicipioAtividade> joinMunicipioAtividade = rootAssociado.join("municipiosAtividade", JoinType.LEFT);
		Join<MunicipioAtividade, Municipio> joinMunicipio = joinMunicipioAtividade.join("municipio", JoinType.LEFT);
		Join<Municipio, Regiao> joinMunicipioRegiao = joinMunicipio.join("regiao", JoinType.LEFT);
		
		Path<Long> id = rootAssociado.get("id");
		Path<String> nome = rootAssociado.get("nome");
		Path<String> cpfCnpj = rootAssociado.get("cpfCnpj");
		Path<String> rg = rootAssociado.get("rg");
		Path<Date> dataFiliacao = rootAssociado.get("dataFiliacao");
		Path<List<String>> municipios = joinMunicipio.get("nome");
		Path<Integer> municipioRegiao = joinMunicipioRegiao.get("id");
		
		criteria.multiselect(Arrays.asList(id, nome, cpfCnpj, rg, dataFiliacao, municipios, municipioRegiao));
		
		Predicate predicate = builder.isNotNull(rootAssociado.get("cpfCnpj"));
		
		if (status != null) {
			predicate = builder.and(predicate, 
				builder.equal(rootAssociado.get("ativo"), Boolean.valueOf(status)));
		}
		
		criteria.where(predicate).orderBy(builder.asc(rootAssociado.get(order)));
		
		try {
			return connection.createQuery(criteria).getResultList();
		} catch(Exception e) {
			log.error(e.getLocalizedMessage(), e);
		}
		
		return new ArrayList<>();
	}
	
	public List<Tuple> getRelatorios(final Usuario usuario) {
		final CriteriaBuilder builder = connection.getCriteriaBuilder();
		final CriteriaQuery<Tuple> criteria = builder.createQuery(Tuple.class);
		
		Root<Relatorio> root = criteria.from(Relatorio.class);
		
		final Join<Relatorio, OpcaoRelatorio> joinRelatarioOpcaoRelatorio = root.join("opcoes", JoinType.LEFT);
		
		criteria.multiselect(
			root.get("id"),
			joinRelatarioOpcaoRelatorio.get("id"), 
			joinRelatarioOpcaoRelatorio.get("opcao")
		);
		
		final Predicate predicate = builder.equal(root.get("usuario"), usuario);
		criteria.where(predicate);
		
		try {
			return connection.createQuery(criteria).getResultList();
		} catch(Exception e) {
			log.error(e.getLocalizedMessage(), e);
		}
		
		return new ArrayList<Tuple>();
	}
}
