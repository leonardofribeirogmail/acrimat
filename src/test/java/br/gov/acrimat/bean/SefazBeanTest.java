package br.gov.acrimat.bean;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class SefazBeanTest {

	@Test @Disabled
	void test() {
		File file = new File("C:\\Users\\leona\\Desktop\\teste.html");
		try(FileInputStream fileInputStream = new FileInputStream(file)) {
			Document document = Jsoup.parse(new String(IOUtils.toByteArray(fileInputStream)).replace("${classeLinha}", "linha-usuario"));
			Elements elementos = document.select(".linha-usuario");
			elementos.forEach((usuario) -> {
				final String inscricaoEstadual = usuario.select("td font a").text();
				System.out.println(inscricaoEstadual);
				if(inscricaoEstadual.length() > 0) {
					Elements fonts = document.select("tr td font");
					final String nome = fonts.get(1).text();
					final String status = fonts.get(2).text();  
					
				}
			});
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
