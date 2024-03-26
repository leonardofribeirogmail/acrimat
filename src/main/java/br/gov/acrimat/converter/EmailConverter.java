package br.gov.acrimat.converter;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.collect.Lists;

import br.gov.acrimat.dto.EmailImageDTO;
import br.gov.acrimat.dto.EmailMessageDTO;
import br.gov.acrimat.exception.EmailNotSendException;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@FacesConverter(value = "emailConverter")
public class EmailConverter implements Converter {
	
	private static final String SRC = "src";
	private static final Double MAX_SIZE = Double.valueOf(7900);

	@Override
	public Object getAsObject(final FacesContext context, 
			final UIComponent component, final String value) {
		
		if(StringUtils.isNotBlank(value)) {
			return getEmailImageDTO(value);
		}
		
		return null;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if(value instanceof EmailMessageDTO) {
			return ((EmailMessageDTO) value).getMessage();
		}
		
		return null;
	}
	
	private final void throwException(final Throwable e) {
		log.error(e);
		throw new ConverterException(
				new FacesMessage(FacesMessage.SEVERITY_ERROR, null, e.getLocalizedMessage()), e);
	}
	
	private EmailMessageDTO getEmailImageDTO(final String message) {
		
		final EmailMessageDTO emailMessageDTO = new EmailMessageDTO();
		
		try {
			final Document document = Jsoup.parse(message);
			final Elements images = document.getElementsByTag("img");
			
			if(Objects.nonNull(images)) {
				Double totalLength = Double.valueOf(0);
				final List<EmailImageDTO> emailImages = Lists.newArrayList();
				final Iterator<Element> iterator = images.iterator();
				while(iterator.hasNext()) {
					final Element image = iterator.next();
					final String src = image.attr(SRC);
					
					totalLength = Double.sum(totalLength, imageSize(src));
					
					if(totalLength > MAX_SIZE) {
						throw new EmailNotSendException("O tamanho total do email não deve exceder 8Mb");
					}
					
					final EmailImageDTO emailImageDTO = new EmailImageDTO(src);
					
					image.attr(SRC, emailImageDTO.getContentWithCid());
					emailImages.add(emailImageDTO);
				}
				
				emailMessageDTO.setImages(Lists.newArrayList(emailImages));
			}
			
			emailMessageDTO.setMessage(document.html());
		} catch(Exception e) {
			throwException(e);
		}
		
		return emailMessageDTO;
	}
	
	private double imageSize(final String src) {
		final String base64String = src;

		final String stringLength = base64String.split(",")[1];

		final Double sizeInBytes = 4 * Math.ceil((Double.valueOf(stringLength.length()) / Double.valueOf(3)))*0.5624896334383812;
		return sizeInBytes/1000;
	}
}
