package br.gov.acrimat.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import br.gov.acrimat.enums.OpcaoRelatorioEnum;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@FacesConverter("opcaoConverter")
public class OpcaoRelatorioConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if(value != null && !value.isEmpty()) {
			try {				
				return OpcaoRelatorioEnum.getEnumByNome(value);
			} catch(Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		return null;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if(value != null && value instanceof OpcaoRelatorioEnum) {
			return ((OpcaoRelatorioEnum) value).getNome();
		}
		return null;
	}

}
