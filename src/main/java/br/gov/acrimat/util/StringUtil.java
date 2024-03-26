package br.gov.acrimat.util;

import org.apache.commons.lang3.StringUtils;

public class StringUtil {

	private StringUtil() {
		// Sem necessidade
	}
	
	public static final String capitalize(String value) {
		final StringBuilder builder = new StringBuilder();
		
		if(value != null) {			
			String[] values = value.toLowerCase().split(" ");
			for(String v : values) {
				builder.append(StringUtils.capitalize(v)).append(" ");
			}
		}
		
		return builder.toString().trim();
	}
}
