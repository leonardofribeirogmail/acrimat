package br.gov.acrimat.exception;

public class BeanException extends Exception {

	private static final long serialVersionUID = 1L;

	public BeanException(final String mensagem) {
		super(mensagem);
	}
	
	public BeanException(final String mensagem, Throwable e) {
		super(mensagem, e);
	}
}
