package br.gov.acrimat.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.apache.commons.lang3.StringUtils;

@FacesConverter("onlyNumbersConverter")
public class OnlyNumbersConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if(!StringUtils.isAllBlank(value)) {
			return value.replaceAll("[^0-9]", "");
		}
		
		return "";
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if(value instanceof String) {
			return String.valueOf(value).replaceAll("[^0-9]", "");
		}
		
		return "";
	}

}
