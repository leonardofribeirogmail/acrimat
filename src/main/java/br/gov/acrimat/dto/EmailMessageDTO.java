package br.gov.acrimat.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmailMessageDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	private String message;
	private List<EmailImageDTO> images;
}
