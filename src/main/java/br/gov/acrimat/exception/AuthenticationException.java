package br.gov.acrimat.exception;

public class AuthenticationException extends RuntimeException {

	private static final long serialVersionUID = 1652871943753039197L;
	
	public AuthenticationException() {}
	
	public AuthenticationException(final String mensagem) {
		super(mensagem);
	}

	public AuthenticationException(final String mensagem, final Throwable e) {
		super(mensagem, e);
	}
}
