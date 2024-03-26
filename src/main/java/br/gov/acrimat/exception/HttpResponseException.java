package br.gov.acrimat.exception;

public class HttpResponseException extends RuntimeException {

	private static final long serialVersionUID = -4039647335158214453L;

	public HttpResponseException(final Throwable e) {
		super(e);
	}
}
