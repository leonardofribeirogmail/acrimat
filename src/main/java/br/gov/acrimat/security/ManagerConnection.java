package br.gov.acrimat.security;

import java.io.Serializable;

import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.SynchronizationType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import lombok.Getter;
import lombok.extern.jbosslog.JBossLog;

@Named
@JBossLog
@RequestScoped
public class ManagerConnection implements Serializable {
	
	private static final long serialVersionUID = 7594432844289421568L;
	
	@Getter
	@PersistenceContext(unitName = "br.gov.acrimat", 
		synchronization = SynchronizationType.SYNCHRONIZED, 
		type = PersistenceContextType.EXTENDED)
	private transient EntityManager manager;
	
	@Resource @Getter
	private transient UserTransaction transaction;
	
	public void close() {
		if(manager.isJoinedToTransaction()) {
			manager.close();
		}
	}
	
	public void rollback() {
		if(transaction != null) {
			try {
				transaction.rollback();
			} catch (IllegalStateException | SecurityException | SystemException e) {
				log.error(e.getLocalizedMessage(), e);
			}
		}
	}
	
	public CriteriaBuilder getCriteriaBuilder() {
		return manager.getCriteriaBuilder();
	}

	public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteria) {
		return manager.createQuery(criteria);
	}
}
