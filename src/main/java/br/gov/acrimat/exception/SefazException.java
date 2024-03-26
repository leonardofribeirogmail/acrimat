package br.gov.acrimat.exception;

public class SefazException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public SefazException() {
		super();
	}

	public SefazException(final String mensagem) {
		super(mensagem);
	}
	
	public SefazException(final String mensagem, Throwable e) {
		super(mensagem, e);
	}
}
