package br.gov.acrimat.listener;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import br.gov.acrimat.security.Authentication;
import br.gov.acrimat.util.HttpResponse;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
public class LifeCycleListener implements PhaseListener {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private HttpResponse response;
	
	@Inject
	private HttpServletRequest request;
	
	@Inject
	private Authentication authentication;

	@Override
	public void beforePhase(PhaseEvent event) {
	    Optional.ofNullable(extrairUrlAreaPublica())
        .filter(url -> request.getRequestURL().toString().contains("arealogada"))
        .filter(url -> Objects.isNull(authentication.getUsuario()))
        .ifPresent(url -> response.sendRedirect(url.toString()));
	}


	@Override
	public void afterPhase(PhaseEvent event) {
		//Nao necessita de tratamento
	}
	
	@Override
	public PhaseId getPhaseId() {
		return PhaseId.ANY_PHASE;
	}
	
	private URL extrairUrlAreaPublica() {
		final String scheme = request.getScheme();
		final String serverName = request.getServerName();
		final String contextPath = request.getContextPath();
		int localPort = request.getLocalPort();
		try {
			return new URL(scheme, serverName, localPort, contextPath);
		} catch (MalformedURLException e) {
			log.error(e.getMessage(), e);
		}
		
		return null;
	}
}
