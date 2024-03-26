package br.gov.acrimat.security;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import br.gov.acrimat.exception.AuthenticationException;
import br.gov.acrimat.exception.BeanException;
import br.gov.acrimat.model.Usuario;
import br.gov.acrimat.service.SecurityService;
import lombok.Getter;

@Named
@SessionScoped
public class Authentication implements Serializable {

	private static final long serialVersionUID = 989020023569348595L;
	
	@Getter
	private Usuario usuario;

	@Inject
	private SecurityService securityService;
	
	@Inject
	private HttpServletRequest request;
	
	public void login(final String email, final String senha) throws BeanException {
		try {
			usuario = securityService.validarUsuario(email, senha);
			if(usuario == null) {
				throw new AuthenticationException();
			}
		} catch(AuthenticationException e) {
			throw new AuthenticationException("error.usuarioinvalido");
		} catch(Exception e) {
			throw new BeanException(e.getLocalizedMessage(), e);
		}
	}
	
	public void logout() {
		this.usuario = null;
		request.getSession(false).invalidate();
	}
}
