package br.gov.acrimat.bean;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.PrimeFaces;
import org.primefaces.component.panelgrid.PanelGrid;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.acrimat.dto.AssociadoRelDTO;
import br.gov.acrimat.dto.OpcaoRelatorioDTO;
import br.gov.acrimat.dto.RegiaoDTO;
import br.gov.acrimat.dto.RelatorioDTO;
import br.gov.acrimat.enums.OpcaoRelatorioEnum;
import br.gov.acrimat.enums.Status;
import br.gov.acrimat.model.Estado;
import br.gov.acrimat.model.Municipio;
import br.gov.acrimat.service.EstadoService;
import br.gov.acrimat.service.MunicipioService;
import br.gov.acrimat.service.RelatorioService;
import br.gov.acrimat.util.Mensagem;
import br.gov.acrimat.util.impl.MessageProvider;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jbosslog.JBossLog;

import static br.gov.acrimat.enums.OpcaoRelatorioEnum.*;

@JBossLog
@SessionScoped
@Named("relatorioBean")
public class RelatorioBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EstadoService estadoService;
	
	@Inject
	private MunicipioService municipioService;
	
	@Inject
	private RelatorioService service;
	
	@Inject
	private Mensagem mensagem;
	
	@Inject
	private MessageProvider provider;
	
	@Getter @Setter
	private Municipio municipioAtividade;
	
	@Getter @Setter
	private RelatorioDTO relatorio;
	
	@Getter @Setter
	private transient PanelGrid panelGridRelatorio;
	
	@Getter @Setter
	private boolean opcaoRadioButton = true;

	@Inject @Getter
	private RegiaoBean regiaoBean;

	@Getter @Setter
	private RegiaoDTO regiaoDTO;
	
	@Getter @Setter
	private Date dtFinalAssociado;
	
	@Getter @Setter
	private Date dtInicialAssociado;
	
	@Getter @Setter
	private Status statusAssociado;
	
	@Getter
	private List<Municipio> municipios;
	
	@Getter
	private List<Municipio> municipiosFiltrados;
	
	@Getter @Setter
	private List<OpcaoRelatorioEnum> opcoesRelatorio;
	
	@Getter
	private List<OpcaoRelatorioEnum> opcoes = criarOpcoesRelatorio();
	
	@Getter
	private List<Status> statusList = Arrays.asList(Status.values());
	
	private transient StreamedContent streamedContent;
	
	@PostConstruct
	public void init() {
		buscarEstadoReferencia();
		verificarOpcoesSelecionadas();
		opcoesRelatorio = listarOpcoesRelatorio();
		if(panelGridRelatorio != null) {
			PrimeFaces.current().ajax().update(panelGridRelatorio.getClientId());			
		}
	}
	
	public void salvarOpcoes() {
		try {
			incluirOpcoes(opcoesRelatorio);
			excluirOpcoes(opcoesRelatorio);
			relatorio = service.salvar(relatorio);
			opcoesRelatorio = listarOpcoesRelatorio();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			mensagem.error("panelGridRelatorio", e.getLocalizedMessage());
		}
	}
	
	public void filtrarMunicipioAtividade(final AjaxBehaviorEvent event) {
		
		municipiosFiltrados = new ArrayList<>();
		final SelectOneMenu selectOneMenu = (SelectOneMenu) event.getComponent();
		final RegiaoDTO regiaoDTOSubmitted = (RegiaoDTO) selectOneMenu.getValue();
		
		if(regiaoDTOSubmitted != null) {
			municipios.forEach(municipio -> {
				if(municipio.getRegiao() != null && municipio.getRegiao().getId().equals(regiaoDTOSubmitted.getCodigoRegiao())) {
					municipiosFiltrados.add(municipio);
				}
			});
		
		} else {
			municipiosFiltrados = new ArrayList<>(municipios);
		}
	}
	
	public StreamedContent getImprimirRelatorio() {
		
		relatorio.setRegiao(regiaoDTO);
		relatorio.setStatusAssociado(statusAssociado);
		relatorio.setDtInicialAssociado(dtInicialAssociado);
		relatorio.setDtFinalAssociado(dtFinalAssociado);
		relatorio.setMunicipioAtividade(municipioAtividade);
		
		final List<OpcaoRelatorioEnum> enums = Collections.synchronizedList(opcoesRelatorio);
		
		try(final XSSFWorkbook workBook = new XSSFWorkbook()) {
			final List<AssociadoRelDTO> associados = service.buscarRelatorio(relatorio);
			
			final XSSFSheet sheet = workBook.createSheet("Associados");
			XSSFRow row = sheet.createRow(0);
			
			final List<OpcaoRelatorioEnum> opcaoTelaSelecao = enums.stream()
				    .filter(OpcaoRelatorioEnum::getTelaSelecao)
				    .collect(Collectors.toList());
			
			for(OpcaoRelatorioEnum opcao : opcaoTelaSelecao) {
				int i = row.getLastCellNum() == -1 ? 0 : row.getLastCellNum();
				XSSFCell cell = row.createCell(i);
				cell.setCellValue(opcao.getNome());
			}
			
			for(AssociadoRelDTO associado : associados) {
				
				row = sheet.createRow(sheet.getLastRowNum() + 1);
				
				for(OpcaoRelatorioEnum opcao : opcaoTelaSelecao) {
					if(opcao.getAcessoBanco().equals(Boolean.TRUE)
							&& !MUNICIPIO.equals(opcao)
							&& !ESTADO.equals(opcao)) {
						Field[] fields = associado.getClass().getDeclaredFields();
						preencherFields(row, associado, opcao, fields);
						fields = associado.getClass().getSuperclass().getDeclaredFields();
						preencherFields(row, associado, opcao, fields);
					} else if(opcao.equals(ATIVIDADE)
							&& Objects.nonNull(associado.getMunicipiosAtividade())) {
						preencherMunicipioAtividade(row, associado);
					} else if(opcao.equals(TELEFONE)
							&& Objects.nonNull(associado.getTelefones())) {
						preencherTelefones(row, associado);
					} else if(opcao.equals(MUNICIPIO)) {
						preencherMunicipio(row, associado);
					} else if(opcao.equals(ESTADO)) {
						preencherEstado(row, associado);
					}
				}
			}
			
			final Calendar calendar = Calendar.getInstance();
			final SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyyHHmmss");
			final String nomeArquivo = "associados_" + formatter.format(calendar.getTime());
			
			realizarDownloadArquivo(workBook, nomeArquivo);
			
		} catch(IOException e) {
			log.warn(provider.getMensagem("warn.downloadmultiplo"));
		} catch(Exception e) {
			log.error(e.getLocalizedMessage(), e);
		}
		
		return streamedContent;
	}

	private List<OpcaoRelatorioEnum> criarOpcoesRelatorio() {
		return Stream.of(OpcaoRelatorioEnum.values())
				.filter(OpcaoRelatorioEnum::getTelaSelecao)
				.collect(Collectors.toList());
	}

	private void realizarDownloadArquivo(final XSSFWorkbook workBook, final String nomeArquivo) throws IOException {
		
		final File file = File.createTempFile(nomeArquivo, ".xlsx");
		
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		
		workBook.write(fileOutputStream);
		
		InputStream inputStream = new FileInputStream(file);
		final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(IOUtils.toByteArray(inputStream));
		
		streamedContent = DefaultStreamedContent.builder()
			.name(nomeArquivo + ".xlsx")
			.contentType("application/vnd.ms-excel")
			.stream(() -> byteArrayInputStream)
			.build();
			
		workBook.close();
		fileOutputStream.close();
		inputStream.close();
	}

	private void preencherFields(XSSFRow row, 
			AssociadoRelDTO associado, 
			OpcaoRelatorioEnum opcao, 
			Field[] fields)
			throws IllegalAccessException {
		for(Field field : fields) {
			field.setAccessible(true);
			String fieldAssociado = String.valueOf(field.get(associado));
			if(field.getName().equals(opcao.getValorDb())) {
				int i = row.getLastCellNum() == -1 ? 0 : row.getLastCellNum();
				XSSFCell cell = row.createCell(i);
				cell.setCellValue("null".contains(fieldAssociado) ? "" : fieldAssociado);
			}
		}
	}
	
	private void preencherTelefones(final XSSFRow row, final AssociadoRelDTO associado) {
	    Optional.ofNullable(associado.getTelefones())
        .ifPresent(telefones -> {
            final String telefoneStr = String.join(", ", telefones);
            final int i = row.getLastCellNum() == -1 ? 0 : row.getLastCellNum();
            final XSSFCell cell = row.createCell(i);
            cell.setCellValue(telefoneStr);
        });
	}

	
	private void preencherMunicipioAtividade(final XSSFRow row, final AssociadoRelDTO associado) {
		Optional.ofNullable(associado.getMunicipiosAtividade())
        .ifPresent(muniAtividade -> {
            final String municipiosStr = muniAtividade.stream()
                    .map(ma -> ma.getMunicipio().getNome())
                    .collect(Collectors.joining(", "));
            final int i = row.getLastCellNum() == -1 ? 0 : row.getLastCellNum();
            final XSSFCell cell = row.createCell(i);
            cell.setCellValue(municipiosStr);
        });
	}
	
	private void preencherMunicipio(final XSSFRow row, 
			final AssociadoRelDTO associado) {
		Optional.ofNullable(associado.getMunicipio())
			.ifPresent(municipio -> {
				int i = row.getLastCellNum() == -1 ? 0 : row.getLastCellNum();
				XSSFCell cell = row.createCell(i);
				cell.setCellValue(associado.getMunicipio());
			});
	}
	
	private void preencherEstado(final XSSFRow row, 
			final AssociadoRelDTO associado) {
		Optional.ofNullable(associado.getEstado())
			.ifPresent(estado -> {
				int i = row.getLastCellNum() == -1 ? 0 : row.getLastCellNum();
				XSSFCell cell = row.createCell(i);
				cell.setCellValue(associado.getEstado());
			});
	}
	
	private List<OpcaoRelatorioEnum> listarOpcoesRelatorio() {
	    final Map<Integer, OpcaoRelatorioEnum> opcaoRelatorioEnumMap = new ConcurrentHashMap<>(Stream.of(OpcaoRelatorioEnum.values())
	            .collect(Collectors.toMap(OpcaoRelatorioEnum::getCodigo, Function.identity())));
	    List<OpcaoRelatorioDTO> opcoes = Collections.synchronizedList(relatorio.getOpcoes());

	    return opcoes.parallelStream()
	            .map(opcaoRelatorio -> {
	                if (Objects.nonNull(opcaoRelatorio.getOpcao())) {
	                    opcaoRelatorio.setOpcao(opcaoRelatorioEnumMap.get(opcaoRelatorio.getOpcao().getCodigo()));
	                }
	                return opcaoRelatorio;
	            })
	            .filter(opcaoRelatorio -> Objects.nonNull(opcaoRelatorio.getOpcao())
	                    && opcaoRelatorio.getOpcao().getTelaSelecao())
	            .map(OpcaoRelatorioDTO::getOpcao)
	            .collect(Collectors.toList());
	}

	
	private void buscarEstadoReferencia() {
		try {
			final Estado estado = estadoService.getEstadoById(11);
			municipios = municipioService.getMunicipiosByEstado(estado);
		} catch (Exception e) {
			mensagem.error("panelGridRelatorio", e.getLocalizedMessage());
		}
	}
	
	protected void verificarOpcoesSelecionadas() {
		relatorio = service.consultarOpcoesSalvas();
		if(relatorio.getId() == null) {
			inicializarSelecionados();
		} 
	}
	
	private void inicializarSelecionados() {
		List<OpcaoRelatorioDTO> opcoesRelatEnum = new ArrayList<>(
			Arrays.asList(new OpcaoRelatorioDTO(OpcaoRelatorioEnum.NOME),
				new OpcaoRelatorioDTO(OpcaoRelatorioEnum.CPF),
				new OpcaoRelatorioDTO(OpcaoRelatorioEnum.TELEFONE),
				new OpcaoRelatorioDTO(OpcaoRelatorioEnum.EMAIL)));

		relatorio = new RelatorioDTO(opcoesRelatEnum);
	}

	private void incluirOpcoes(List<OpcaoRelatorioEnum> opcoes) {
		if(opcoes != null) {
			for(OpcaoRelatorioEnum opcao : opcoes) {
				if(!listarOpcoesRelatorio().contains(opcao)) {
					OpcaoRelatorioDTO novaOpcao = new OpcaoRelatorioDTO(opcao);
					relatorio.getOpcoes().add(novaOpcao);
				}
			}	
		}
	}
	
	private void excluirOpcoes(List<OpcaoRelatorioEnum> opcoes) {
		Iterator<OpcaoRelatorioDTO> iterator = relatorio.getOpcoes().iterator();
		while(iterator.hasNext()) {
			OpcaoRelatorioDTO opcaoRelatorio = iterator.next();
			if(opcoes == null || !opcoes.contains(opcaoRelatorio.getOpcao())) {
				iterator.remove();
			}
		}
	}
	
}
