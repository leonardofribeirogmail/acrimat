package br.gov.acrimat.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.gov.acrimat.enums.Status;
import br.gov.acrimat.model.Municipio;
import br.gov.acrimat.modelutils.ModelEntidade;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RelatorioDTO extends ModelEntidade {

	private static final long serialVersionUID = 1L;
	
	@Getter @Setter
	private Long id;
	
	@Getter @Setter
	private Municipio municipioAtividade;
	
	@Getter @Setter
	private Municipio municipioEleitoral;
	
	@Getter @Setter
	private RegiaoDTO regiao;
	
	@Getter @Setter
	private Date dtInicialAssociado;
	
	@Getter @Setter
	private Date dtFinalAssociado;
	
	@Getter @Setter
	private Status statusAssociado;
	
	@Getter @Setter
	private List<OpcaoRelatorioDTO> opcoes;
	
	public RelatorioDTO(final List<OpcaoRelatorioDTO> opcoes) {
		this.opcoes = opcoes;
	}

	public List<String> extrairOpcoesSelecionadas() {
		final List<String> opcoesSelecionadas = new ArrayList<>();
		
		opcoes.stream()
		.filter(opcaoRelatorio -> opcaoRelatorio.getOpcao().getAcessoBanco())
		.forEach(opcaoRelatorio -> opcoesSelecionadas.add(opcaoRelatorio.getOpcao().getValorDb()));
		
		return opcoesSelecionadas;
	}

}
