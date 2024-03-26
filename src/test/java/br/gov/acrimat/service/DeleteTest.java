package br.gov.acrimat.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import br.gov.acrimat.model.Associado;
import br.gov.acrimat.model.Usuario;

public class DeleteTest {
	
	private EntityManager manager;
	private List<Object[]> associados;
	private TypedQuery<Object[]> query;
	
	@BeforeEach
	public void before() {
		//manager = Persistence.createEntityManagerFactory("br.gov.acrimat.manually").createEntityManager();
	}

	@Test
	@Disabled
	public void buscar() {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Object[]> criteria = builder.createQuery(Object[].class);
		Root<Associado> root = criteria.from(Associado.class);
		
		Expression<Long> quantidade = builder.count(root); 
		Path<String> cpfCnpj = root.get("cpfCnpj");
		Path<Long> id = root.get("id");
		
		criteria.multiselect(Arrays.asList(id, cpfCnpj, quantidade));
		criteria.having(builder.greaterThan(quantidade, 1L));
		criteria.groupBy(root.get("cpfCnpj"));
		
		associados = manager.createQuery(criteria).getResultList();
		
		assertTrue(associados.size() > 0);
	}
	
	@Test
	@Transactional
	@Disabled
	public void inserir() {
		manager.getTransaction().begin();
		
		Usuario usu = new Usuario();
		usu.setNome("Executiva");
		usu.setEmail("executiva@acrimat.org.br");
		usu.setSenha("$2a$10$r5PfMeKDFB0e0KxrTDj2GOfdLh9Pv4GDNk0OOADhWIuZHiD/Qt6zq");
		usu.setDataAtualizacao(new Date());
		usu.setDataCriacao(new Date());
		usu.setToken("");
		
		Usuario merge = manager.merge(usu);

		manager.getTransaction().commit();
	}
	
	@Test
	@Disabled
	public void deletarUsuario() {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Usuario> criteria = builder.createQuery(Usuario.class);
		Root<Usuario> root = criteria.from(Usuario.class);
		
		criteria.where(builder.equal(root.get("id"), 29L));
		
		Usuario usuario = manager.createQuery(criteria).getSingleResult();
	}
	
	@Test @Disabled
	void selecionarRegioes() {
		
		StringBuilder stb = new StringBuilder();
		stb.append("SELECT DISTINCT r.id, m.nome from Regiao as r ");
		stb.append("INNER JOIN Municipio as m ");
		stb.append("ON m.codigoRegiao = r.id ");
		stb.append("INNER JOIN MunicipioAtividade as ma ");
		stb.append("ON ma.municipio.id = m.id");
		
		query = manager.createQuery(stb.toString(), Object[].class);
		int size = query.getResultList().size();
		assertTrue(size > 0);
	}
}
