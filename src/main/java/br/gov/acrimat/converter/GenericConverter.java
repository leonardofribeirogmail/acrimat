package br.gov.acrimat.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.primefaces.shaded.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.gov.acrimat.modelutils.ModelEntidade;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@FacesConverter("genericConverter")
public class GenericConverter implements Converter {
	
	private static final String FORBIDDEN= "Selecione";

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if(value != null && !value.isEmpty() && !value.contains(FORBIDDEN)) {
			try {				
				final JSONObject json = new JSONObject(value);
				final String clazzName = json.getString("clazz");
				final Class<?> generico = Class.forName(clazzName);
				final Object object = new ObjectMapper().readValue(value, generico);
				return object;
			} catch(Exception e) {
				log.error(e);
			}
		}
		return null;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if(value instanceof ModelEntidade) {
			return value.toString();
		}
		return null;
	}

}
