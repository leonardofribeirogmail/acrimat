package br.gov.acrimat.converter;

import javax.persistence.AttributeConverter;

import org.primefaces.shaded.json.JSONObject;

public class JsonObjectConverter implements AttributeConverter<JSONObject, String> {

	@Override
	public String convertToDatabaseColumn(JSONObject attribute) {
		if(attribute != null) {
			return attribute.toString();
		}
		return null;
	}

	@Override
	public JSONObject convertToEntityAttribute(String dbData) {
		if(dbData != null && !dbData.isEmpty()) {
			return new JSONObject(dbData);
		}
		return null;
	}

}
