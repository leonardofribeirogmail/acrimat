package br.gov.acrimat.exception;

public class ModelServiceException extends DaoException {

	private static final long serialVersionUID = -4551351891180579310L;
	
	public ModelServiceException() {
		super();
	}

	public ModelServiceException(final String mensagem) {
		super(mensagem);
	}
	
	public ModelServiceException(final Throwable e) {
		super(e.getLocalizedMessage(), e);
	}
	
	public ModelServiceException(final String mensagem, final Throwable e) {
		super(mensagem, e);
	}
}
