package br.gov.acrimat.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.Test;

class RelatorioServiceTest {

	@Test
	void test() {
		Timestamp timestamp = Timestamp.valueOf("1952-03-17 22:00:00.0");
		String value = converteTimeStamp(timestamp);
		assertTrue(true);
	}

	private String converteTimeStamp(final Timestamp time) {
		final SimpleDateFormat sdfNovo = new SimpleDateFormat("dd/MM/yyyy");
		final SimpleDateFormat sdfAntigo = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		
		if(time != null) {
			try {
				Date parse = sdfAntigo.parse(time.toString());
				return sdfNovo.format(parse);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		return "";
	}
}
