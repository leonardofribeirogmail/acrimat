package br.gov.acrimat.lazy;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

import org.apache.commons.lang3.math.NumberUtils;
import org.primefaces.model.SortOrder;

import br.gov.acrimat.dto.AssociadoDTO;

public class LazyModelAssociadoSort implements Serializable, Comparator<AssociadoDTO> {

	private static final long serialVersionUID = 1L;
	
	private SortOrder order;
	private String sortField;
	
	
	public LazyModelAssociadoSort(String sortField, SortOrder order) {
		this.order = order;
		this.sortField = sortField;
	}
	
	@Override
	public int compare(AssociadoDTO AssociadoDTO1, AssociadoDTO AssociadoDTO2) {
		
		int value = 0;
		
		try {
			if("nome".equals(sortField)) {
				value = AssociadoDTO1.getNome().compareTo(AssociadoDTO2.getNome());
			} else if("cpfCnpj".equals(sortField)) {
				value = AssociadoDTO1.getCpfCnpj().compareTo(AssociadoDTO2.getCpfCnpj());
			} else if("dataFiliacao".equals(sortField)) {
				value = AssociadoDTO1.getDataFiliacao().compareTo(AssociadoDTO2.getDataFiliacao());
			} else if("municipiosAtividade".equals(sortField)) {
				value = getAssociadoMunicipioAtividadeCompared(AssociadoDTO1, AssociadoDTO2);
			}
			
			value = SortOrder.ASCENDING.equals(order) ? value : -1 * value;
		} catch(Exception e) {
			value = 0;
		}
		
		return value;
	}
	
	private Integer getAssociadoMunicipioAtividadeCompared(final AssociadoDTO associadoDTO1, final AssociadoDTO associadoDTO2) {
		
		boolean associado1Null = Objects.isNull(associadoDTO1.getMunicipiosAtividade());
		boolean associado2Null = Objects.isNull(associadoDTO2.getMunicipiosAtividade());
		
		associado1Null = associado1Null || associadoDTO1.getMunicipiosAtividade().isEmpty();
		associado2Null = associado2Null || associadoDTO2.getMunicipiosAtividade().isEmpty();
		
		if(associado1Null && associado2Null) {
			return NumberUtils.INTEGER_ZERO;
		} else if(associado1Null) {
			return NumberUtils.INTEGER_ONE;
		} else if(associado2Null) {
			return -NumberUtils.INTEGER_ONE;
		}
		
		final String ma1 = associadoDTO1.getMunicipiosAtividade().get(NumberUtils.INTEGER_ZERO);
		final String ma2 = associadoDTO2.getMunicipiosAtividade().get(NumberUtils.INTEGER_ZERO);
		
		return ma1.compareTo(ma2);
	}
	
}
