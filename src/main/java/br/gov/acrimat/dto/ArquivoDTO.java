package br.gov.acrimat.dto;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.acrimat.exception.ArquivoException;
import br.gov.acrimat.model.Arquivo;
import lombok.Getter;
import lombok.Setter;

public class ArquivoDTO implements Serializable {

	private static final long serialVersionUID = 8281534162360823062L;

	@Getter @Setter
	private Arquivo content;
	
	@Getter
	private boolean arquivoCorrompido = false;
	
	@Getter
	private StreamedContent stream;
	
	@Setter @Getter
	private String arquivoBase64;
	
	@Setter @Getter
	private String arquivoNome;
	
	public ArquivoDTO(Arquivo arquivo) {
		this.content = arquivo;
		construirStream();
	}
	
	public ArquivoDTO(Arquivo arquivo, boolean arquivoCorrompido) {
		this.content = arquivo;
		this.arquivoCorrompido = arquivoCorrompido;
	}
	
	private void construirStream() {
		if(content != null && content.getUrl() != null) {
			
			InputStream inputStream = null;
			
			try {
				inputStream = new FileInputStream(new File(content.getUrl()));
				ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(IOUtils.toByteArray(inputStream));
				
				stream = DefaultStreamedContent.builder()
					.contentType(FilenameUtils.getExtension(content.getUrl()))
					.name(content.getNome())
					.stream(() -> byteArrayInputStream)
					.build();
				inputStream.close();
			} catch(Exception e) {
				throw new ArquivoException(e.getLocalizedMessage(), e);
			}
		}
	}
}
