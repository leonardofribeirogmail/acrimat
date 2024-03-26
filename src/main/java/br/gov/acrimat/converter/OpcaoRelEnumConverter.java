package br.gov.acrimat.converter;

import javax.persistence.AttributeConverter;

import br.gov.acrimat.enums.OpcaoRelatorioEnum;

public class OpcaoRelEnumConverter implements AttributeConverter<OpcaoRelatorioEnum, String> {

	@Override
	public String convertToDatabaseColumn(OpcaoRelatorioEnum attribute) {
		return attribute != null ? attribute.getNome() : null;
	}

	@Override
	public OpcaoRelatorioEnum convertToEntityAttribute(String dbData) {
		return dbData != null ? OpcaoRelatorioEnum.getEnumByNome(dbData) : null;
	}

}
