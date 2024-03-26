package br.gov.acrimat.exception;

public class UsuarioException extends DaoException {

	private static final long serialVersionUID = 1L;
	
	public UsuarioException() {
		super();
	}
	
	public UsuarioException(final Throwable e) {
		super(e);
	}

	public UsuarioException(final String mensagem, final Throwable e) {
		super(mensagem, e);
	}
}
