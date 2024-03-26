package br.gov.acrimat.exception;

public class EmailNotSendException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public EmailNotSendException(final String message) {
		super(message);
	}
	
	public EmailNotSendException(final String message, final Throwable e) {
		super(message, e);
	}
}
