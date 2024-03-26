package br.gov.acrimat.service;

import org.apache.commons.codec.binary.Base64;

public class TesteMain {

	private static final String LOCATION = "/Users/leonardofernandes/Desktop/testeEmail.txt";
	
	public static void main(String[] args) throws Exception {
		final String value = "bGVvbmFyZG9AaG90bWFpbC5jb207bWluaGFsb25nYXNlbmhhY3JpcHRvZ3JhZmFkYQ==";
		final byte[] decodeBase64 = Base64.decodeBase64(value);
		final String decodedString = new String(decodeBase64);
		System.out.println(decodedString);
	}
}
