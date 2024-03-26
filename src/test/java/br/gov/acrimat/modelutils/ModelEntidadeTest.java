package br.gov.acrimat.modelutils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.primefaces.shaded.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.gov.acrimat.model.Municipio;

class ModelEntidadeTest {

	/*
	 * 
	@Test
	void test() throws ClassNotFoundException, JsonMappingException, JsonProcessingException {
		Municipio municipio = new Municipio(1, 2, "municipio", "estado", 3);
		String value = municipio.toString();
		JSONObject json = new JSONObject(value);
		String clazzName = json.getString("clazz");
		Class<?> generico = Class.forName(clazzName);
		Municipio novoMunicipio = (Municipio) new ObjectMapper().readValue(value, generico);
		assertEquals(municipio, novoMunicipio);
	}
	 */

}
