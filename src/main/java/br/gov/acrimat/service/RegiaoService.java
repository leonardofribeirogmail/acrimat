package br.gov.acrimat.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import br.gov.acrimat.dto.RegiaoDTO;
import br.gov.acrimat.model.Associado;
import br.gov.acrimat.model.Municipio;
import br.gov.acrimat.model.MunicipioAtividade;
import br.gov.acrimat.model.Regiao;
import br.gov.acrimat.modelutils.impl.ModelDao;
import br.gov.acrimat.security.ManagerConnection;

@Named
@RequestScoped
public class RegiaoService extends ModelDao<Regiao> {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private ManagerConnection connection;
	
	@PostConstruct
	public void init() {
		setClazz(Regiao.class);
	}
	
	public List<Integer> selecionarRegioesAssociado(final Associado associado) {
		if(associado.getMunicipiosAtividade() != null && associado.getMunicipiosAtividade().size() > 0) {			
			final CriteriaBuilder builder = connection.getCriteriaBuilder();
			final CriteriaQuery<Integer> criteria = builder.createQuery(Integer.class);
			criteria.distinct(true);
			
			Root<Associado> rootAssociado = criteria.from(Associado.class);
			Join<Associado, MunicipioAtividade> joinMunicipioAtividade = rootAssociado.join("municipiosAtividade", JoinType.LEFT);
			Join<MunicipioAtividade, Municipio> joinMunicipio = joinMunicipioAtividade.join("municipio", JoinType.LEFT);
			Join<Municipio, Regiao> joinMunicipioRegiao = joinMunicipio.join("regiao", JoinType.LEFT);
			
			Path<Integer> id = joinMunicipioRegiao.get("id");

			criteria.select(id);
			criteria.where(builder.equal(rootAssociado.get("id"), associado.getId()));
			return connection.createQuery(criteria).getResultList();
		}
		
		return new ArrayList<>();
	}
	
	public List<RegiaoDTO> selectAllRegioes(){
		List<RegiaoDTO> listaRegiaoDTO = new ArrayList<>();
		List<Regiao> listar = listar(null);
		if(!listar.isEmpty()) {
			listar.forEach(r -> {
				listaRegiaoDTO.add(new RegiaoDTO(r.getId(), r.getNome()));
			});
		}
		return listaRegiaoDTO;
	}
	
	public List<RegiaoDTO> selecionarRegioesMunicipioAtv() {
		
		final Root<Regiao> regiaoRoot = getCriteriaQuery().from(Regiao.class);

        getCriteriaQuery().distinct(true)
            .multiselect(regiaoRoot.get("id"), regiaoRoot.get("nome"))
            .orderBy(getBuilder().asc(regiaoRoot.get("nome")));

        final Subquery<Long> subquery = getCriteriaQuery().subquery(Long.class);
        final Root<Municipio> subqueryMunicipioRoot = subquery.from(Municipio.class);
        
        subquery.select(getBuilder().count(subqueryMunicipioRoot))
            .where(getBuilder().equal(subqueryMunicipioRoot.get("regiao"), regiaoRoot));

        getCriteriaQuery().having(getBuilder().gt(subquery, 1L));

        final List<Regiao> resultList = getEntityManager().createQuery(getCriteriaQuery()).getResultList();
        
        return Optional.ofNullable(resultList)
        	    .map(regioes -> regioes.stream()
        	        .map(regiao -> RegiaoDTO.builder()
        	            .codigoRegiao(regiao.getId())
        	            .nome(regiao.getNome())
        	            .build())
        	        .collect(Collectors.toList()))
        	    .orElse(Collections.emptyList());
	}
}
