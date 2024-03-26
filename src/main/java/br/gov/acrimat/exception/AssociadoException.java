package br.gov.acrimat.exception;

public class AssociadoException extends ModelServiceException {

	private static final long serialVersionUID = 1L;
	
	public AssociadoException() {
		// Sem texto
	}
	
	public AssociadoException(final String mensagem) {
		super(mensagem);
	}

	public AssociadoException(final String mensagem, final Throwable e) {
		super(mensagem, e);
	}
}
