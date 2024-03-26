package br.gov.acrimat.dto;

import java.io.Serializable;
import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailMapDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer position;
	private ArrayList<String> emails;
}
