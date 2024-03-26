package br.gov.acrimat.modelutils;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
public class ModelEntidade implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Getter
	protected Class<?> clazz;
	
	@Override
	public String toString() {
		final ObjectMapper mapper = new ObjectMapper();
		try {
			this.clazz = this.getClass();
			String writeValueAsString = mapper.writeValueAsString(this);
			return writeValueAsString;
		} catch (JsonProcessingException e) {
			log.error(e.getMessage(), e);
		}
		return "";
	}
}
