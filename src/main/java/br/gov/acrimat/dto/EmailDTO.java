package br.gov.acrimat.dto;

import java.io.Serializable;
import java.util.Properties;

import br.gov.acrimat.enums.RecipientTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String jobId;
	private String image;
	private String subject;
	private String userName;
	private String password;
	private EmailMapDTO emailMap;
	private Properties properties;
	private EmailMessageDTO emailMessageDTO;
	private RecipientTypeEnum recipientTypeEnum;
	
	public EmailDTO() {
		this.emailMessageDTO = new EmailMessageDTO();
	}
}
