package br.gov.acrimat.bean;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.primefaces.PrimeFaces;

import br.gov.acrimat.dto.ArquivoDTO;
import br.gov.acrimat.exception.ArquivoException;
import br.gov.acrimat.exception.ChaveUnicaException;
import br.gov.acrimat.model.Arquivo;
import br.gov.acrimat.model.Associado;
import br.gov.acrimat.service.AssociadoService;
import br.gov.acrimat.util.Mensagem;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ViewScoped
@Named("arquivoBean")
public class ArquivoBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private Mensagem mensagem;
	
	@Inject
	private AssociadoService service;
	
	@Inject
	private HttpServletRequest request;
	
	@Getter
	private List<ArquivoDTO> arquivos;
	
	@Getter @Setter
	private Associado associado;
	
	@Getter @Setter
	private ArquivoDTO arquivo;
	
	@Getter @Setter
	private String arquivoBase64;
	
	@Getter @Setter
	private String arquivoNome;
	
	public void popularAssociado(Long id) {
		arquivos = new ArrayList<>();
		try {
			associado = service.buscarAssociadoCompleto(id);
			if(Objects.nonNull(associado.getArquivos()) && !associado.getArquivos().isEmpty()) {
				for(Arquivo arquivo : associado.getArquivos()) {
					try {					
						arquivos.add(new ArquivoDTO(arquivo));
					} catch(Exception e) {
						log.error(e.getLocalizedMessage(), e);
						arquivos.add(new ArquivoDTO(arquivo, true));
					}
				}
			}
		} catch(Exception e) {
			log.error(e.getLocalizedMessage(), e);
			mensagem.error("error.errointerno");
		}
	}
	
	public static final String getContext() {
		String contexto = ArquivoBean.class.getResource(".").toString();
		boolean contextoServidor = contexto.contains("opt") || contexto.contains("vfs");
		return contextoServidor ? "/" : "";
	}
	
	public void excluirArquivo(ArquivoDTO content) {
		
		Arquivo arquivo = content.getContent();
		
		try {
			boolean excluirArquivo = excluirArquivo(arquivo.getUrl());
			
			if(!excluirArquivo && !content.isArquivoCorrompido()) {
				throw new ArquivoException("error.erroaoexcluirarquivo");
			}
			
			associado.getArquivos().remove(arquivo);
			associado = service.salvar(associado);
			
			mensagem.info("info.arquivoexcluido");
		} catch(Exception e) {
			log.error(e.getLocalizedMessage(), e);
			mensagem.error("error.erroaoexcluirarquivo");
		} finally {
			popularAssociado(associado.getId());
		}
	}
	
	public void salvarArquivo() {
		
		String endereco = null;
	
		try {
			Path path = resgatarPastaDoServidor();
			endereco = (path.toFile()+"\\"+arquivoNome).replace("\\", "/");
			
			salvarArquivoDiretorio(endereco);
			mensagem.info("info.arquivosalvo");
		} catch(ChaveUnicaException e) {
			excluirArquivo(endereco);
			log.error(e.getLocalizedMessage(), e);
			mensagem.error("erro.arquivoduplicado");
		} catch(Exception e) {
			excluirArquivo(endereco);
			log.error(e.getLocalizedMessage(), e);
			mensagem.error("error.errosalvararquivo");
		} finally {
			popularAssociado(associado.getId());
			PrimeFaces.current().executeScript("monitor.stop()");
		}
	}

	private void salvarArquivoDiretorio(String endereco) throws IOException {
		
		log.info("Arquivo " +arquivoNome+ " sendo salvo em "+endereco);
		
		try (FileOutputStream fileOutputStream = new FileOutputStream(new File(endereco))) {
			fileOutputStream.write(Base64.decodeBase64(arquivoBase64));
			salvarAssociado(endereco);
		} catch(Exception e) {
			throw new IOException(e);
		}
	}

	private void salvarAssociado(String endereco) {
		if(associado.getArquivos() == null) {
			associado.setArquivos(new ArrayList<>());
		}
		if(arquivos == null) {
			arquivos = new ArrayList<>();
		}
		
		final Arquivo arquivo = new Arquivo();
		arquivo.setAssociado(associado);
		arquivo.setNome(arquivoNome);
		arquivo.setUrl(endereco);
		
		associado.getArquivos().add(arquivo);
		
		associado = service.merge(associado);
	}

	private Path resgatarPastaDoServidor() throws IOException {
		
		Path path = construirPath();
		
		if(!Files.exists(path)) {
			path = Files.createDirectory(path);
			log.info("Pasta criada para manipulação de arquivos: " + path.toString());
		} else {
			log.info("A pasta "+path.toString()+" já existe");
		}
		
		return path;
	}

	private String resgatarContextoServidor() {
		final String enderecoServidor = ArquivoBean.getContext();
		return enderecoServidor + this.getClass().getResource(".").toString().replace("vfs:/", "").split("/standalone")[0];
	}
	
	/**
	 * Extrai o contexto mostrando ser hml ou producao
	 * @return {@link String} contexto da aplicacao
	 */
	private String resgatarContextoAplicacao() {
		final String contextoDeAvaliacao = "acrimathml";
		final String contexto = request.getContextPath();
		return request.getContextPath().contains(contexto) ? (contextoDeAvaliacao) : "\\acrimat";
	}
	
	
	/**
	 * Monta a pasta correta onde os arquivos ficarao, diferenciando hml de producao
	 * @return {@link String} Retorna a pasta onde ficara armazenado os arquivos
	 * @throws IOException 
	 */
	private Path construirPath() throws IOException {
		final String contexto = resgatarContextoServidor();
		final String contextoAplicacao = resgatarContextoAplicacao().replace("\\", "/");
		String contextoArquivo = contexto+"//arquivos//";
		
		final String sistemaOperacional = String.valueOf(System.getProperty("os.name")).toLowerCase();
		if(sistemaOperacional.contains("windows")) {
			contextoArquivo = contextoArquivo.startsWith("/") 
					? contextoArquivo.substring(1, contextoArquivo.length()) : contextoArquivo;
		}
		
		final Path pastaContextoArquivo = Paths.get(contextoArquivo);
		
		if(!Files.exists(pastaContextoArquivo)) {
			Files.createDirectory(pastaContextoArquivo);
		}
		
		final String contextoMontadoAplicacao = contextoArquivo + contextoAplicacao;
		final Path pastaDoContextoAplicacao = Paths.get(contextoMontadoAplicacao);
		
		if(!Files.exists(pastaDoContextoAplicacao)) {
			Files.createDirectory(pastaDoContextoAplicacao);
		}
		
		final String pasta = contextoMontadoAplicacao + "\\" + associado.getCpfCnpj();
		return Paths.get(pasta.replace("\\", "/"));
	}
	
	private boolean excluirArquivo(String endereco) {
		if(endereco != null && !endereco.isEmpty()) {
			File file = new File(endereco);
			try {				
				if(file.exists()) {
					FileUtils.forceDelete(file);
					return !file.exists();
				}
			} catch(IOException e) {
				log.error(e.getLocalizedMessage(), e);
				mensagem.error("error.erroaoexcluirarquivo");
			}
		}
		
		return false;
	}
}
