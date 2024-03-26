package br.gov.acrimat.exception;

public class ArquivoException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public ArquivoException(String mensagem) {
		super(mensagem);
	}

	public ArquivoException(String mensagem, Throwable e) {
		super(mensagem, e);
	}
}
