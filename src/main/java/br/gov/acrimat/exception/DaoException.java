package br.gov.acrimat.exception;

public class DaoException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public DaoException() {
		super();
	}

	public DaoException(final String mensagem) {
		super(mensagem);
	}
	
	public DaoException(final Throwable e) {
		super(e);
	}
	
	public DaoException(final String mensagem, final Throwable e) {
		super(mensagem, e);
	}
}
