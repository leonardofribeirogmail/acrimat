package br.gov.acrimat.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import br.com.caelum.stella.format.CNPJFormatter;
import br.com.caelum.stella.format.CPFFormatter;

@FacesConverter("cpfCnpjConverter")
public class CpfCnpjConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if(value != null && !value.isEmpty()) {
			return fazerConversao(value);
		}
		
		return null;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if(value != null && !value.toString().isEmpty()) {
			return fazerConversao(value.toString());
		}
		
		return null;
	}
	
	private String replace(String value) {
		return value.replaceAll("[^0-9]", "");
	}
	
	private String converterCpf(String value) {
		return new CPFFormatter().format(value);
	}
	
	private String converterCnpj(String value) {
		return new CNPJFormatter().format(value);
	}
	
	private String fazerConversao(String value) {
		String valueReplaced = replace(value);
		int quantidade = valueReplaced.length();
		boolean valido = quantidade == 11 || quantidade == 14;
		if(valido) {
			return quantidade == 11 ? converterCpf(valueReplaced) : converterCnpj(valueReplaced);
		}
		
		return null;
	}
}
