package br.gov.acrimat.bean;

import java.io.IOException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import br.gov.acrimat.exception.AuthenticationException;
import br.gov.acrimat.exception.BeanException;
import br.gov.acrimat.modelutils.impl.ModelBean;
import br.gov.acrimat.security.Authentication;
import br.gov.acrimat.util.Mensagem;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
@Named("usuarioBean")
public class UsuarioBean extends ModelBean {

	private static final long serialVersionUID = 1L;

	@Setter
	@Getter
	private String email;
	
	@Setter 
	@Getter
	private String senha;
	
	@Inject
	private Mensagem mensagem;
	
	@Inject
	private Authentication authentication;

	public void validar() throws BeanException, IOException {
		
		PrimeFaces.current().executeScript("monitor.start()");
		
		try {
			authentication.login(email, senha);
			enviarAreaLogada();			
		} catch(AuthenticationException e) {
			mensagem.error(e.getLocalizedMessage());
			log.error(e.getLocalizedMessage(), e);
			PrimeFaces.current().executeScript("monitor.stop()");
		} catch(BeanException e) {
			PrimeFaces.current().executeScript("monitor.stop()");
			throw new BeanException(e.getLocalizedMessage(), e);
		}
	}
}
