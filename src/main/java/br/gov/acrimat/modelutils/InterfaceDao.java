package br.gov.acrimat.modelutils;

import java.util.List;

import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;

import br.gov.acrimat.exception.DaoException;

public interface InterfaceDao<T> {

	public T consultar(final Predicate predicate) throws DaoException;
	public T merge(final T objetoGenerico) throws DaoException;
	public List<T> listar(final Predicate predicate) throws DaoException;
	public List<T> listar(final Predicate predicate, final Order order) throws DaoException;
	public boolean excluir(final Object id) throws DaoException;
}
