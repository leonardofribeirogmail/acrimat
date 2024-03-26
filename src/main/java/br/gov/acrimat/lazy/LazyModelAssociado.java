package br.gov.acrimat.lazy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import br.gov.acrimat.bean.RegiaoBean;
import br.gov.acrimat.dto.AssociadoDTO;
import br.gov.acrimat.dto.RegiaoDTO;
import br.gov.acrimat.model.Associado;
import lombok.Getter;

public class LazyModelAssociado extends LazyDataModel<AssociadoDTO> {

	private static final long serialVersionUID = 1L;

	@Getter
	private List<AssociadoDTO> dataSource;

	private RegiaoBean regiaoBean;

	public LazyModelAssociado(List<AssociadoDTO> associados, RegiaoBean regiaoBean) {
		dataSource = associados;
		this.regiaoBean = regiaoBean;
	}

	@Override
	public AssociadoDTO getRowData(String rowKey) {
		for (AssociadoDTO associado : dataSource) {
			if (associado.getId().equals(Long.valueOf(rowKey))) {
				return associado;
			}
		}

		return null;
	}

	@Override
	public String getRowKey(AssociadoDTO associado) {
		return String.valueOf(associado.getId());
	}

	@Override
	public int getRowCount() {
		return super.getRowCount();
	}

	@Override
	public List<AssociadoDTO> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, FilterMeta> filterBy) {

		final List<AssociadoDTO> associados = filtrar(filterBy);
		
		Collections.sort(associados, new LazyModelAssociadoSort(sortField, sortOrder));

		int dataSize = associados.size();
		setRowCount(dataSize);

		if (dataSize > pageSize) {
			try {
				return associados.subList(first, first + pageSize);
			} catch (IndexOutOfBoundsException e) {
				return associados.subList(first, first + (dataSize % pageSize));
			}
		}

		return associados;
	}

	private List<AssociadoDTO> filtrar(Map<String, FilterMeta> filterBy) {
		final List<AssociadoDTO> associados = new ArrayList<>();

		for (AssociadoDTO associado : dataSource) {
			boolean match = true;

			if (filterBy != null) {
				for (Iterator<String> it = filterBy.keySet().iterator(); it.hasNext();) {

					String filterProperty = it.next();
					if (filterBy.get(filterProperty).getFilterField().equals("globalFilter")) {
						String filterValue = String.valueOf(filterBy.get(filterProperty).getFilterValue()).trim();

						try {
							match = conferirMatch(false, associado.getNome(), filterValue);
							match = conferirMatch(match, associado.getCpfCnpj(), filterValue);
							match = conferirMatch(match, associado.getDataFiliacao(), filterValue);
							match = conferirMatch(match, associado.getRg(), filterValue);
							match = conferirMatch(match, associado.getMunicipiosAtividade(), filterValue);
							match = conferirMatch(match, associado.getCodigoRegioes());
						} catch (Exception e) {
							match = false;
						}
					}
				}
			}

			if (match) {
				associados.add(associado);
			}
		}
		return associados;
	}

	public void adicionar(Associado associado) {
		dataSource.add(0, associado.converterParaDTO());
	}

	public void alterar(Integer index, Associado associado) {
		dataSource.set(index, associado.converterParaDTO());
	}

	public boolean contains(Associado associado) {
		AssociadoDTO temp = associado.converterParaDTO();
		return dataSource.contains(temp) || conferirId(temp);
	}

	public Integer getIndex(Associado associado) {
		AssociadoDTO temp = associado.converterParaDTO();
		final Integer index = dataSource.indexOf(temp);
		return index >= 0 ? index : getAssociadoIndex(temp);
	}

	public Boolean remover(Associado associado) {
		AssociadoDTO temp = associado.converterParaDTO();
		return dataSource.remove(temp);
	}
	
	public Boolean remover(final int index) {
		final AssociadoDTO associadoDTO = dataSource.remove(index);
		return Objects.nonNull(associadoDTO);
	}

	private boolean conferirMatch(boolean match, String value, String filterValue) {
		if (filterValue.equals("null") || match) {
			return true;
		} else if (value.isEmpty() && !filterValue.equals("null")) {
			return false;
		}

		return Pattern.compile(Pattern.quote(filterValue.toLowerCase()), Pattern.CASE_INSENSITIVE)
				.matcher(value.toLowerCase()).find();
	}

	private boolean conferirMatch(boolean match, List<String> values, String filterValue) {
		if (filterValue.equals("null") || match) {
			return true;
		} else if (values.isEmpty() && !filterValue.equals("null")) {
			return false;
		}
		for (String value : values) {
			match = Pattern.compile(Pattern.quote(filterValue.toLowerCase()), Pattern.CASE_INSENSITIVE)
					.matcher(value.toLowerCase()).find();
			if (match) {
				break;
			}
		}

		return match;
	}

	private boolean conferirMatch(boolean match, List<Integer> codigoRegioes) {
		
		final RegiaoDTO regiaoDTOSelecionada = regiaoBean.getRegiaoDTO();
		
		if (regiaoDTOSelecionada != null && codigoRegioes != null) {
			return match && codigoRegioes.contains(regiaoDTOSelecionada.getCodigoRegiao());
		} else if(regiaoDTOSelecionada != null) {
			return false;
		}
		
		return match;
	}
	
	private boolean conferirId(final AssociadoDTO associadoDTO) {
		return dataSource.stream()
				.filter(assoc -> assoc.getId().equals(associadoDTO.getId())).count() > 0;
		
	}
	
	private Integer getAssociadoIndex(final AssociadoDTO associadoDTO) {
		final Optional<AssociadoDTO> findFirst = dataSource.stream()
				.filter(assoc -> assoc.getId().equals(associadoDTO.getId())).findFirst();
		return findFirst.isPresent() ? dataSource.indexOf(findFirst.get()) : -1;
	}
}
