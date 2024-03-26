package br.gov.acrimat.util.impl;

import java.util.ResourceBundle;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import br.gov.acrimat.annotation.CustomProducer;
import br.gov.acrimat.util.PropertyProvider;

@Named
@RequestScoped
public class SmtpProvider implements PropertyProvider {

	private static final String PROPERTY_NAME = "smtp";
	
	private ResourceBundle bundle;

	@Produces @CustomProducer
	public ResourceBundle getBundle() {
		bundle = getBundle(bundle, PROPERTY_NAME);
		return bundle;
	}
	
	public String getMensagem(final String key) {
		return getMensagem(getBundle(), key);
	}
}
