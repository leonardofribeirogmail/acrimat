package br.gov.acrimat.service;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.PreencodedMimeBodyPart;

import org.apache.xmlgraphics.image.loader.ImageException;

import br.gov.acrimat.dto.EmailDTO;
import br.gov.acrimat.dto.EmailImageDTO;
import br.gov.acrimat.exception.EmailNotSendException;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
public class EmailScheduleService implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private transient Session session;
	
	public EmailScheduleService(final Session session) {
		this.session = session;
	}
	
	public void sendMessage(final EmailDTO email) throws IOException, ImageException {
		
		final boolean isJobEmpty = Objects.isNull(email.getEmailMap());
		
		if(isJobEmpty) {
			log.warn("A lista de emails está vazia");
			throw new EmailNotSendException(String.format("Não foi possível enviar um dos emails {%s}", email));
		}
		
		log.infof("Enviando email para [%s] -> %s", email.getEmailMap().getPosition(), 
				Arrays.toString(email.getEmailMap().getEmails().toArray()));
		final Multipart multipart = new MimeMultipart();
		final MimeBodyPart mimeBodyPart = new MimeBodyPart();
		final MimeMessage mimeMessage = new MimeMessage(session);

	    try {
			mimeMessage.setFrom(new InternetAddress(email.getUserName()));
			
			mimeMessage.setSubject(email.getSubject());
			mimeMessage.addRecipients(email.getRecipientTypeEnum().getRecipientType(), 
					InternetAddress.parse(String.join(",", email.getEmailMap().getEmails())));
			
			mimeBodyPart.setContent(email.getEmailMessageDTO().getMessage(), "text/html; charset=utf-8");
			
			multipart.addBodyPart(mimeBodyPart);
			attachImages((MimeMultipart) multipart, email);
			
			mimeMessage.setContent(multipart);
			
			Transport.send(mimeMessage);
			
			log.infof("Lista de email{%s} enviada com sucesso!", email.getJobId());
			log.debugf("Emails enviados para: %s", Arrays.toString(email.getEmailMap().getEmails().toArray()));
		} catch (MessagingException e) {
			log.infof("Exceção ao executar o job %s", email.getJobId());
			throw new EmailNotSendException(e.getLocalizedMessage(), e);
		}
	}
	
	private static void attachImages(final MimeMultipart mimeMultipart, final EmailDTO emailDTO) 
			throws MessagingException {
		boolean hasImages = Objects.nonNull(emailDTO.getEmailMessageDTO().getImages());
		hasImages = hasImages && !emailDTO.getEmailMessageDTO().getImages().isEmpty();
		
		if(!hasImages) {
			return;
		}
		
		final Iterator<EmailImageDTO> iterator = emailDTO.getEmailMessageDTO().getImages().iterator();
		while(iterator.hasNext()) {
			
			final PreencodedMimeBodyPart bodyPart = new PreencodedMimeBodyPart("base64");
			final EmailImageDTO image = iterator.next();
			
			final String imageType = image.getNewImage().split(";")[0].replace("data:", "");
			final String imageString = image.getNewImage().split(",")[1];
			final String sufix = imageType.split("/")[1];
			
			bodyPart.setHeader("Content-ID", image.getUuid().toString());
			bodyPart.setDisposition(Part.INLINE);
			bodyPart.setContent(imageString, imageType);
			bodyPart.setFileName(image.getUuid().toString().concat(".").concat(sufix));
			mimeMultipart.addBodyPart(bodyPart);
		}
	}

}
