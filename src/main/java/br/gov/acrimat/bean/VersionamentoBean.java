package br.gov.acrimat.bean;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import br.gov.acrimat.modelutils.impl.ModelBean;
import lombok.Getter;
import lombok.extern.jbosslog.JBossLog;

@Named
@JBossLog
@RequestScoped
public class VersionamentoBean extends ModelBean {

	private static final long serialVersionUID = 1L;
	
	@Getter
	private String versao;
	private static final String KEY = "../../../";

	@PostConstruct
	public void init() {
		
		final SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd.HHmm");
		final String enderecoServidor = ArquivoBean.getContext();
		
		try {
			final String sistemaOperacional = String.valueOf(System.getProperty("os.name")).toLowerCase();
			String enderecoContexto = enderecoServidor + getFileEndereco();
			if(sistemaOperacional.contains("windows")) {
				enderecoContexto = enderecoContexto.startsWith("/") 
						? enderecoContexto.substring(1, enderecoContexto.length()) : enderecoContexto;
			}
			final Path path = Paths.get(enderecoContexto);
			final BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
			versao = formatter.format(new Date(attrs.lastAccessTime().toMillis()));
		} catch(Exception e) {
			log.error(e.getLocalizedMessage(), e);
		}
	}
	
	private String getFileEndereco() {
		final String webInf = "/WEB-INF/";
		final String vfs = "vfs:/";
		return this.getClass().getResource(KEY).toString().split(webInf)[0].split(vfs)[1];
	}
}
