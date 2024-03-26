package br.gov.acrimat.modelutils.impl;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.gov.acrimat.modelutils.ModelInterface;
import br.gov.acrimat.util.impl.MessageProvider;
import lombok.Getter;

public class ModelBean implements ModelInterface {

	private static final long serialVersionUID = 8247561476075030L;

	@Inject @Getter
	private MessageProvider provider;
	
	@Inject @Getter
	private HttpServletRequest request;
	
	@Inject @Getter
	private HttpServletResponse response;

}
