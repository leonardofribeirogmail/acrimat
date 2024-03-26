package br.gov.acrimat.bean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.gov.acrimat.dto.OpcaoRelatorioDTO;
import br.gov.acrimat.dto.RelatorioDTO;
import br.gov.acrimat.enums.OpcaoRelatorioEnum;
import br.gov.acrimat.service.RelatorioService;

@ExtendWith(MockitoExtension.class)
class RelatorioBeanTest {
	
	@Mock
	private RelatorioService service;
	
	@InjectMocks
	private RelatorioBean relatorioBean;

	@Test
	void testVerificarOpcoesSelecionadas() {
		RelatorioDTO relatorio = opcoesSalvas();
		doReturn(relatorio).when(service).consultarOpcoesSalvas();
		
		relatorioBean.verificarOpcoesSelecionadas();
		
		assertEquals(relatorioBean.getRelatorio(), relatorio);
	}

	private RelatorioDTO opcoesSalvas() {
		final List<OpcaoRelatorioDTO> opcoes = Arrays.asList(
			new OpcaoRelatorioDTO(1L, OpcaoRelatorioEnum.ATIVIDADE),
			new OpcaoRelatorioDTO(2L, OpcaoRelatorioEnum.NOME)
		);
		RelatorioDTO relatorio = new RelatorioDTO(opcoes);
		relatorio.setId(1L);
		return relatorio;
	}
}
