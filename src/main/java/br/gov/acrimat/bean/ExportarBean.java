package br.gov.acrimat.bean;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Named;

import br.gov.acrimat.model.Associado;
import lombok.Getter;
import lombok.Setter;

@Named
@Dependent
public class ExportarBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Getter @Setter
	private List<Associado> selecionados;
}
