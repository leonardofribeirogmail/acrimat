package br.gov.acrimat.converter;

import javax.persistence.AttributeConverter;

public class BooleanConverter implements AttributeConverter<Boolean, String> {

	@Override
	public String convertToDatabaseColumn(Boolean attribute) {
		if(attribute == null) {
			return "";
		}
		return Boolean.TRUE.equals(attribute) ? "S" : "N";
	}

	@Override
	public Boolean convertToEntityAttribute(String dbData) {
		if(dbData == null || dbData.isEmpty()) {
			return Boolean.FALSE;
		}
		return dbData.equals("S") ? Boolean.TRUE : Boolean.FALSE;
	}
}
