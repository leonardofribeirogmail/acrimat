package br.gov.acrimat.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import lombok.extern.jbosslog.JBossLog;

@Named
@JBossLog
@RequestScoped
public class ExceptionHandlerService implements Serializable {

	private static final long serialVersionUID = 1L;

	public boolean analisarExcecao(Throwable exception, String textoProcurado) {
		
		boolean contem = false;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(out);
		
		try {
			if(exception != null) {
				exception.printStackTrace(writer);
				contem = out.toString().contains(textoProcurado);
				out.close();
			}
		} catch (IOException e) {
			log.error(e.getLocalizedMessage(), e);
		} finally {
			writer.close();
		}
		
		return contem;
	}
}
