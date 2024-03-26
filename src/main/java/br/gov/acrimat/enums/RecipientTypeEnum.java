package br.gov.acrimat.enums;

import lombok.Getter;

public enum RecipientTypeEnum {

	TO(0, "PARA", javax.mail.Message.RecipientType.TO, "info.tipoenvio.to"),
	CC(1, "CC", javax.mail.Message.RecipientType.CC, "info.tipoenvio.cc"),
	BCC(2, "CCO", javax.mail.Message.RecipientType.BCC, "info.tipoenvio.cco");
	
	@Getter
	private Integer value;
	
	@Getter
	private String name;
	
	@Getter
	private javax.mail.Message.RecipientType recipientType;
	
	@Getter
	private String description;
	
	private RecipientTypeEnum(final Integer value, 
			final String name, 
			final javax.mail.Message.RecipientType recipientType,
			final String description) {
		this.name = name;
		this.value = value;
		this.description = description;
		this.recipientType = recipientType;
	}
	
	public static RecipientTypeEnum getRecipientByValue(final Integer value) {
		for(RecipientTypeEnum recipientTypeEnum : RecipientTypeEnum.values()) {
			if(recipientTypeEnum.getValue().equals(value)) {
				return recipientTypeEnum;
			}
		}
		
		throw new NullPointerException("Recipient não encontrado");
	}
}
