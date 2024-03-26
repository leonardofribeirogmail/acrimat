package br.gov.acrimat.modelutils;

import java.io.IOException;
import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ModelInterface extends Serializable {
	
	HttpServletRequest getRequest();
	HttpServletResponse getResponse();

	default void enviarAreaLogada() throws IOException {
		final String contextPath = getRequest().getContextPath();
		final String arealogada = contextPath.concat("/paginas/arealogada/home.xhtml");
		getResponse().sendRedirect(arealogada);
	}
}
