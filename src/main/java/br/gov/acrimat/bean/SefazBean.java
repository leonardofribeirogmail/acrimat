package br.gov.acrimat.bean;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.primefaces.component.outputpanel.OutputPanel;

import com.github.kevinsawicki.http.HttpRequest;

import br.gov.acrimat.dto.UsuarioSefazDTO;
import br.gov.acrimat.exception.SefazException;
import br.gov.acrimat.util.Mensagem;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ViewScoped
@Named("sefazBean")
public class SefazBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private Mensagem mensagem;

	@Getter
	private InputStream imagem;

	private String jSessionId;

	@Resource(lookup = "java:global/sefazUrlImagem")
	private String urlSefazImagem;

	@Getter @Setter
	@Resource(lookup = "java:global/sefazUrl")
	private String urlSefaz;

	@Getter
	@Setter
	private String cpfCnpj;

	@Getter
	@Setter
	private String imagemDigitada;

	@Getter
	@Setter
	private OutputPanel outPutPanelMaster;
	
	@Getter
	private List<UsuarioSefazDTO> usuariosSefaz;
	
	public void recarregarPagina() {

		HttpURLConnection connection = null;

		try {
			connection = (HttpURLConnection) new URL(urlSefazImagem).openConnection();

			final Map<String, List<String>> headerFields = connection.getHeaderFields();
			final List<String> list = headerFields.get("Set-Cookie");
			jSessionId = list.get(1).split(";")[0];

			imagem = IOUtils.toBufferedInputStream(connection.getInputStream());
			usuariosSefaz = new ArrayList<UsuarioSefazDTO>();
		} catch (IOException e) {
			log.error(e);
			mensagem.error("form:panelGridSefaz", e.getLocalizedMessage());
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	public void consultar() {
		final String vu = String.valueOf(new Date().getTime());
		Integer tipoDocumento = cpfCnpj.length() > 14 ? 2 : 3;
		String novaUrl = urlSefaz.concat("?pagn=Pesquisar&numrDoct=" + cpfCnpj + "&captchaDigitado=" + imagemDigitada
				+ "&codigoTipoDocumento=" + tipoDocumento + "&ajaxRequest=1&vU=" + vu);
		final HttpRequest request = HttpRequest.post(novaUrl);
		request.header("Cookie", jSessionId);
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			out.write(IOUtils.toByteArray(request.getConnection().getInputStream()));
			retornoChamada(out.toString("ISO-8859-1"));
		} catch (IOException e) {
			log.error(e);
		} finally {
			request.disconnect();
			imagemDigitada = null;
		}
	}
	
	private void retornoChamada(String param) {
		final Document document = Jsoup.parse(param.replace("${classeLinha}", "linha-usuario"));
		final Elements elementos = document.select(".linha-usuario");
		try {
			elementos.forEach((usuario) -> {
				final String inscricaoEstadual = usuario.select("td font a").text();
				if(inscricaoEstadual.length() > 0) {
					Elements fonts = usuario.select("td font");
					final String nome = fonts.get(1).text();
					final String status = fonts.get(2).text();  
					usuariosSefaz.add(new UsuarioSefazDTO(inscricaoEstadual, nome, status));
				}
			});
			
			if(usuariosSefaz.size() == 0) {
				throw new SefazException();
			}
		} catch (Exception e) {
			mensagem.error("form:imagemDigitada", "error.sefaz.requisicao");
			log.error(e);
		}
	}
}
