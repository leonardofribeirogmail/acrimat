package br.gov.acrimat.service;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import br.gov.acrimat.exception.UsuarioException;
import br.gov.acrimat.model.Usuario;

@Named
@RequestScoped
public class SecurityService implements Serializable {

	private static final long serialVersionUID = 4766996828503819476L;
	
	@Inject
	private UsuarioService usuarioService;
	
	public Usuario validarUsuario(final String email, final String senha) {
		try {
			return usuarioService.getUsuarioByEmailSenha(email, digestSenha(senha));			
		} catch (UsuarioException e) {
			return null;
		}
	}
	
	private String digestSenha(final String senha){
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			byte[] digested = digest.digest(senha.getBytes("UTF-8"));
			StringBuilder hexString = new StringBuilder();
			for (byte b : digested) {
				hexString.append(String.format("%02X", 0xFF & b));
			}
			
			return hexString.toString();
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e ) {
			throw new UsuarioException(e);
		}
		
	}
}
