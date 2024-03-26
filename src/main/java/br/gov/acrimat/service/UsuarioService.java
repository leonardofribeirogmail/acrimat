package br.gov.acrimat.service;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.persistence.criteria.Predicate;

import br.gov.acrimat.exception.DaoException;
import br.gov.acrimat.exception.UsuarioException;
import br.gov.acrimat.model.Usuario;
import br.gov.acrimat.modelutils.impl.ModelDao;
import lombok.extern.jbosslog.JBossLog;

@Named
@JBossLog
@RequestScoped
public class UsuarioService extends ModelDao<Usuario> {

	@PostConstruct
	public void init() {
		setClazz(Usuario.class);
	}

	private static final long serialVersionUID = -3747434707747699979L;
	
	public Usuario getUsuarioByEmailSenha(final String email, final String senha) {
		final Predicate predicate = getBuilder()
			.and(getBuilder().equal(getRoot().get("email"), email), 
					getBuilder().equal(getRoot().get("senha"), senha));
		try {
			return consultar(predicate);
		} catch (Exception e) {
			throw new UsuarioException(getProvider().getMensagem("error.usuarioinvalido"), e);
		}
	}
	
	@Override
	public Usuario merge(final Usuario usuario) throws DaoException {
		try {
			return super.merge(usuario);
		} catch(Exception e) {
			log.error(e.getLocalizedMessage(), e);
			throw new UsuarioException(getProvider().getMensagem("error.salvarusuario"), e);
		}
	}
}
