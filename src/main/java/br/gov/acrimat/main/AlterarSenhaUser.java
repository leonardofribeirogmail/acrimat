package br.gov.acrimat.main;

import java.security.MessageDigest;

public class AlterarSenhaUser {

	public static void main(String[] args) throws Exception{
		final String senha = "teste";
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] digested = digest.digest(senha.getBytes("UTF-8"));
		StringBuilder hexString = new StringBuilder();
		for (byte b : digested) {
			hexString.append(String.format("%02X", 0xFF & b));
		}
		String senhahex = hexString.toString();
		System.err.println(senhahex);

	}

} 
