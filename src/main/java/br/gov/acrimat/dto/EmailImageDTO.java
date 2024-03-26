package br.gov.acrimat.dto;

import java.io.Serializable;
import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmailImageDTO implements Serializable {

	private static final String CID = "cid:";
	private static final long serialVersionUID = 1L;

	private UUID uuid = UUID.randomUUID();
	private String newImage;
	
	public EmailImageDTO(final String src) {
		this.newImage = src;
	}
	
	public String getContentWithCid() {
		return CID.concat(uuid.toString());
	}
}
