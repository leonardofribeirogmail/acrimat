package br.gov.acrimat.bean;

import java.util.Arrays;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import br.gov.acrimat.dto.EmailDTO;
import br.gov.acrimat.enums.RecipientTypeEnum;
import br.gov.acrimat.modelutils.impl.ModelBean;
import br.gov.acrimat.service.EmailService;
import br.gov.acrimat.util.Mensagem;
import br.gov.acrimat.util.impl.MessageProvider;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jbosslog.JBossLog;

@Named
@JBossLog
@ViewScoped
public class EmailBean extends ModelBean {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private Mensagem mensagem;
	
	
	@Getter @Setter
	private Integer recipientNumber;

	@Inject
	private MessageProvider messageProvider;
	
	@Inject
	private EmailService emailService;
	
	@Getter @Setter
	private EmailDTO emailDTO = new EmailDTO();
	
	@Getter
	private List<RecipientTypeEnum> recipients = Arrays.asList(RecipientTypeEnum.values());

	
	public String getDescriptionFromResource(final RecipientTypeEnum recipientTypeEnum) {
		return messageProvider.getMensagem(recipientTypeEnum.getDescription());
	}
	
	public void send() {
		
		emailDTO.setRecipientTypeEnum(RecipientTypeEnum.getRecipientByValue(recipientNumber));
		
		try {
			emailService.send(emailDTO);
			mensagem.info("form", "info.email.enviado");
			emailDTO = new EmailDTO();
			recipientNumber = RecipientTypeEnum.TO.getValue();
		} catch(Exception e) {
			log.error(e.getLocalizedMessage(), e);
			mensagem.error("form", e.getLocalizedMessage());
		}
	}
	
}
