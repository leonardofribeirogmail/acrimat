package br.gov.acrimat.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.persistence.AttributeConverter;

import br.gov.acrimat.enums.TipoTelefone;

@FacesConverter("tipoTelefoneConverter")
public class TipoTelefoneConverter implements Converter, AttributeConverter<TipoTelefone, Integer> {

	@Override
	public Integer convertToDatabaseColumn(TipoTelefone tipoTelefone) {
		return tipoTelefone != null ? tipoTelefone.getValor() : null;
	}

	@Override
	public TipoTelefone convertToEntityAttribute(Integer codigo) {
		return convertStringToTipoTelefone(codigo);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		return convertStringToTipoTelefone(value);
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if(value instanceof TipoTelefone) {
			TipoTelefone tipoTelefone = (TipoTelefone) value;
			return convertTipoTelefoneToString(tipoTelefone);
		}
		return null;
	}

	private TipoTelefone convertStringToTipoTelefone(String telefone) {
		if(telefone != null && !telefone.isEmpty()) {
			for (TipoTelefone tipoTelefone : TipoTelefone.values()) {
				if (tipoTelefone.getNome().equalsIgnoreCase(telefone)) {
					return tipoTelefone;
				}
			}
		}
		return null;
	}
	
	private String convertTipoTelefoneToString(TipoTelefone tipoTelefone) {
		return tipoTelefone != null ? tipoTelefone.getNome() : null;
	}
	
	private TipoTelefone convertStringToTipoTelefone(Integer codigo) {
		for (TipoTelefone tipoTelefone : TipoTelefone.values()) {
			if (tipoTelefone.getValor().equals(codigo)) {
				return tipoTelefone;
			}
		}
		return null;
	}
}