package br.gov.acrimat.exception;

public class EmailNotFound extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public EmailNotFound(final Throwable e) {
		super(e);
	}
}
