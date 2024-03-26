package br.gov.acrimat.service;

import java.io.Serializable;

import javax.enterprise.context.Dependent;
import javax.inject.Named;

import org.talesolutions.cep.CEP;
import org.talesolutions.cep.CEPService;
import org.talesolutions.cep.CEPServiceFactory;

@Named
@Dependent
public class BuscaCepService implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private CEPService service = CEPServiceFactory.getCEPService();

	public CEP buscarEndereco(final String cep) {
		return service.obtemPorNumeroCEP(cep);
	}
}
