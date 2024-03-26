package br.gov.acrimat.util;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import br.gov.acrimat.util.impl.MessageProvider;

@Named
@RequestScoped
public class Mensagem implements Serializable {

	private static final long serialVersionUID = 2950972855246501534L;
	
	private transient FacesContext context = FacesContext.getCurrentInstance();
	
	@Inject
	private MessageProvider provider;
		
	private FacesMessage getFacesMessage(FacesMessage.Severity severity, String summary) {
		return new FacesMessage(severity, summary, null);
	}
	
	public void info(final String texto) {
		context.addMessage(null, getFacesMessage(FacesMessage.SEVERITY_INFO, provider.getMensagem(texto)));
	}
	
	public void info(final String id, final String texto) {
		context.addMessage(id, getFacesMessage(FacesMessage.SEVERITY_INFO, provider.getMensagem(texto)));
	}
	
	public void error(final String texto) {
		context.addMessage(null, getFacesMessage(FacesMessage.SEVERITY_ERROR, provider.getMensagem(texto)));
	}
	
	public void error(final String id, final String texto) {
		context.addMessage(id, getFacesMessage(FacesMessage.SEVERITY_ERROR, provider.getMensagem(texto)));
	}
	
	public void generico(FacesMessage.Severity severity, final String id, final String texto) {
		context.addMessage(id, getFacesMessage(severity, provider.getMensagem(texto)));
	}
}
