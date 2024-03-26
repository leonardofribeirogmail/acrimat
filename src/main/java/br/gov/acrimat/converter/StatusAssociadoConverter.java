package br.gov.acrimat.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.apache.commons.lang3.StringUtils;

import br.gov.acrimat.enums.Status;

@FacesConverter(value = "statusAssociadoConverter")
public class StatusAssociadoConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if(StringUtils.isNoneBlank(value)) {
			return Status.valueOf((Object) value);
		}
		
		return Status.AMBOS;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if(value instanceof Status) {
			return String.valueOf(((Status) value).getValue());
		}
		
		return StringUtils.EMPTY;
	}

}
