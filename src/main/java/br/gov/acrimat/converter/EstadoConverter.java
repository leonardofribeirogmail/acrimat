package br.gov.acrimat.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.modelmapper.ModelMapper;

import br.gov.acrimat.model.Estado;

@FacesConverter("estadoConverter")
public class EstadoConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if(value != null && !value.isEmpty()) {
			try {				
				return new ModelMapper().map(value, Estado.class);
			} catch(Exception e) {
				return null;
			}
		}
		return null;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if(value != null && value instanceof Estado) {
			return ((Estado) value).toString();
		}
		return null;
	}
	
}
