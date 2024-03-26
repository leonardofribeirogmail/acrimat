package br.gov.acrimat.modelutils.impl;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import br.gov.acrimat.exception.ChaveUnicaException;
import br.gov.acrimat.exception.DaoException;
import br.gov.acrimat.exception.ModelServiceException;
import br.gov.acrimat.modelutils.InterfaceDao;
import br.gov.acrimat.security.ManagerConnection;
import br.gov.acrimat.service.ExceptionHandlerService;
import br.gov.acrimat.util.impl.MessageProvider;
import lombok.Getter;
import lombok.Setter;

public class ModelDao<T> implements Serializable, InterfaceDao<T> {

	private static final long serialVersionUID = 1L;

	@Inject @Getter
	private MessageProvider provider;
	
	@Inject
	private ManagerConnection connection;
	
	@Inject @Getter
	private ExceptionHandlerService exceptionHandler;
	
	private transient CriteriaBuilder builder;
	
	private transient CriteriaQuery<T> criteria;
	
	private transient Root<T> root;
	
	@Setter
	private Class<T> clazz;
	
	protected CriteriaBuilder getBuilder() {
		if(builder == null) {
			builder = connection.getCriteriaBuilder();
		}
		return builder;
	}
	
	protected CriteriaQuery<T> getCriteriaQuery() {
		if(criteria == null) {
			criteria = getBuilder().createQuery(clazz);
		}
		return criteria;
	}
	
	protected Root<T> getRoot() {
		if(root == null) {
			root = getCriteriaQuery().from(clazz);
		}
		return root;
	}
	
	protected EntityManager getEntityManager() {
		return connection.getManager();
	}

	@Override @Transactional
	public T consultar(Predicate predicate) throws DaoException {
		getCriteriaQuery().distinct(true);
		getCriteriaQuery().where(predicate);
		connection.getManager().getEntityManagerFactory().getCache().evictAll();
		try {
			return connection.createQuery(getCriteriaQuery()).getSingleResult();
		} catch(Exception e) {
			throw new ModelServiceException(e);
		}
	}

	@Override
	public T merge(T objetoGenerico) throws DaoException {
		try {
			connection.getTransaction().begin();
			objetoGenerico = connection.getManager().merge(objetoGenerico);
			connection.getTransaction().commit();
			return objetoGenerico;
		} catch(Exception e) {
			connection.rollback();
			if(getExceptionHandler().analisarExcecao(e, "ConstraintViolationException")) {
				throw new ChaveUnicaException(e);
			} else {				
				throw new ModelServiceException(e);	
			}
		}
	}
	
	@Override
	public List<T> listar(Predicate predicate, Order order) throws DaoException {
		getCriteriaQuery().orderBy(order);
		return buscarLista(predicate);
	}

	@Override
	public List<T> listar(Predicate predicate) throws DaoException {
		return buscarLista(predicate);
	}
	
	@Transactional
	private List<T> buscarLista(Predicate predicate) {
		
		connection.getManager().getEntityManagerFactory().getCache().evictAll();
		
		if(predicate != null) {
			getCriteriaQuery().where(predicate);			
		}
		getCriteriaQuery().distinct(true);
		try {
			return connection.createQuery(getCriteriaQuery()).getResultList();
		} catch(Exception e) {
			throw new ModelServiceException(e);
		}
	}
	
	@Override
	public boolean excluir(Object id){
		final CriteriaBuilder builder =  connection.getCriteriaBuilder();
		try {
			CriteriaDelete<T> criteriaDelete = builder.createCriteriaDelete(clazz);
			Root<T> rootDelete = criteriaDelete.from(clazz);
			Predicate predicate = builder.equal(rootDelete.get("id"), id);
			criteriaDelete.where(predicate); 
			
			connection.getTransaction().begin();
			boolean executeUpdate = connection.getManager().createQuery(criteriaDelete).executeUpdate() > 0;
			connection.getTransaction().commit();
			return executeUpdate;
		} catch (Exception e) {
			connection.rollback();
			throw new ModelServiceException(e);
		}
	}
}
