package br.gov.acrimat.service;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;

import br.gov.acrimat.dto.AssociadoRelDTO;
import br.gov.acrimat.dto.OpcaoRelatorioDTO;
import br.gov.acrimat.dto.RelatorioDTO;
import br.gov.acrimat.enums.OpcaoRelatorioEnum;
import br.gov.acrimat.enums.Status;
import br.gov.acrimat.model.Associado;
import br.gov.acrimat.model.Municipio;
import br.gov.acrimat.model.MunicipioAtividade;
import br.gov.acrimat.model.OpcaoRelatorio;
import br.gov.acrimat.model.Regiao;
import br.gov.acrimat.model.Relatorio;
import br.gov.acrimat.model.Telefone;
import br.gov.acrimat.model.Usuario;
import br.gov.acrimat.modelutils.impl.ModelDao;
import br.gov.acrimat.security.Authentication;
import br.gov.acrimat.security.ManagerConnection;
import br.gov.acrimat.utils.MethodMapUtils;
import lombok.extern.jbosslog.JBossLog;

@Named
@JBossLog
@RequestScoped
public class RelatorioService extends ModelDao<Relatorio> {

	private static final long serialVersionUID = 1L;

	@Inject
	private Authentication authentication;

	@Inject
	private ColumnService columnService;

	@Inject
	private ManagerConnection connection;
	
	@Inject
	private MethodMapUtils methodMapUtils;

	private transient ModelMapper mapper = new ModelMapper();
	
	private transient Join<Municipio, Regiao> joinMunicipioRegiao;
	
	private transient Join<Associado, MunicipioAtividade> joinAssociadoMunicipioAtividade;
	
	private transient Join<Associado, Municipio> joinAssociadoMunicipio;

	@PostConstruct
	public void init() {
		setClazz(Relatorio.class);
	}

	public RelatorioDTO consultarOpcoesSalvas() {
		final Usuario usuario = authentication.getUsuario();
		final RelatorioDTO relatorio = new RelatorioDTO();
		final List<Tuple> tuples = columnService.getRelatorios(usuario);
		final List<OpcaoRelatorioDTO> opcoes = new ArrayList<>();

		for (Tuple tuple : tuples) {
			relatorio.setId((Long) tuple.get(0));
			opcoes.add(new OpcaoRelatorioDTO((Long) tuple.get(1), (OpcaoRelatorioEnum) tuple.get(2)));
		}

		relatorio.setOpcoes(opcoes);

		return relatorio;
	}

	public RelatorioDTO salvar(RelatorioDTO relatorio) {
		Relatorio novoRelatorio = mapper.map(relatorio, Relatorio.class);
		novoRelatorio.setUsuario(authentication.getUsuario());
		corrigirRelatorioDaOpcaoRelatorio(novoRelatorio);

		novoRelatorio = merge(novoRelatorio);
		
		return Optional.ofNullable(novoRelatorio).map(rel -> mapper.map(rel, RelatorioDTO.class))
				.orElse(null);
	}

	public List<AssociadoRelDTO> buscarRelatorio(final RelatorioDTO relatorio) {

		final List<AssociadoRelDTO> associados = Collections.synchronizedList(new ArrayList<>());
		final List<String> opcoesSelecionadas = relatorio.extrairOpcoesSelecionadas();
		
		if(!opcoesSelecionadas.contains(OpcaoRelatorioEnum.ID.getValorDb())) {
			opcoesSelecionadas.add(OpcaoRelatorioEnum.ID.getValorDb());
		}

		final List<Selection<?>> selections = new ArrayList<>();
		final List<Predicate> predicates = new ArrayList<>();

		final CriteriaBuilder builder = connection.getCriteriaBuilder();
		final CriteriaQuery<Tuple> criteria = builder.createQuery(Tuple.class);
		criteria.distinct(true);

		final Status statusAssociado = relatorio.getStatusAssociado();
		final Root<Associado> root = criteria.from(Associado.class);
		
		opcoesSelecionadas.stream()
		.forEach(opcao -> {
			if(OpcaoRelatorioEnum.REGIAOATIVIDADE.getValorDb().equals(opcao)) {
				preencherJoinMunicipioAtividade(relatorio, root);
				preencherJoinRegiao();
				selections.add(joinMunicipioRegiao.get("nome").alias(opcao));
			} else if(OpcaoRelatorioEnum.MUNICIPIO.getValorDb().equals(opcao)) {
				preencherJoinAssociadoMunicipio(root);
				selections.add(joinAssociadoMunicipio.get("nome").alias(opcao));
			} else if(OpcaoRelatorioEnum.ESTADO.getValorDb().equals(opcao)) {
				preencherJoinAssociadoMunicipio(root);
				selections.add(joinAssociadoMunicipio.get("estado").alias(opcao));
			} else {
				selections.add(root.get(opcao).alias(opcao));
			}
		});

		preencherDataFiliacao(relatorio, builder, root, predicates);

		criteria.multiselect(selections);
		
		if(!statusAssociado.equals(Status.AMBOS)) {
			predicates.add(builder.and(builder.equal(root.get("ativo"), 
					Boolean.valueOf(statusAssociado.getValue()))));
		}

		preencherPredicates(relatorio, predicates, builder, root);
		filtrarPorMunicipio(predicates, builder, criteria);

		try {
			final List<Tuple> resultList = connection.createQuery(criteria).getResultList();
			Optional.ofNullable(resultList)
			.filter(result -> !result.isEmpty())
			.ifPresent(result -> {
				preencherAssociadosDTO(opcoesSelecionadas, result, associados);
				preencherTelefones(relatorio, associados);
				preencherMunicipioAtividade(relatorio, associados);
				preencherMunicipio(associados, result);
				preencherEstado(associados, result);
			});
		} catch (Exception e) {
			log.error(e.getLocalizedMessage(), e);
		}

		return associados;
	}
	
	private void preencherMunicipio(final List<AssociadoRelDTO> associados,
            final List<Tuple> resultList) {

		final Map<Long, List<AssociadoRelDTO>> associadosPorId = associados.parallelStream()
		.collect(Collectors.groupingBy(AssociadoRelDTO::getId));
		
		resultList.parallelStream()
		.filter(tuple -> Objects.nonNull(tuple.get(OpcaoRelatorioEnum.MUNICIPIO.getValorDb())))
		.filter(tuple -> tuple.get(OpcaoRelatorioEnum.MUNICIPIO.getValorDb()) instanceof String)
		.filter(tuple -> associadosPorId.containsKey(tuple.get("id")))
		.forEach(tuple -> {
			final Long associadoId = (Long) tuple.get("id");
			final String municipio = (String) tuple.get(OpcaoRelatorioEnum.MUNICIPIO.getValorDb());
			final List<AssociadoRelDTO> associadosEncontrados = associadosPorId.get(associadoId);
			associadosEncontrados.forEach(associado -> associado.setMunicipio(municipio));
		});
	}

	
	private void preencherEstado(final List<AssociadoRelDTO> associados, 
			final List<Tuple> resultList) {
		
		final Map<Long, List<AssociadoRelDTO>> associadosPorId = associados.parallelStream()
	            .collect(Collectors.groupingBy(AssociadoRelDTO::getId));

	    resultList.parallelStream()
        .filter(tuple -> Objects.nonNull(tuple.get(OpcaoRelatorioEnum.ESTADO.getValorDb())))
        .filter(tuple -> tuple.get(OpcaoRelatorioEnum.ESTADO.getValorDb()) instanceof String)
        .filter(tuple -> associadosPorId.containsKey(tuple.get("id")))
        .forEach(tuple -> {
            final Long associadoId = (Long) tuple.get("id");
            final String estado = (String) tuple.get(OpcaoRelatorioEnum.ESTADO.getValorDb());
            final List<AssociadoRelDTO> associadosEncontrados = associadosPorId.get(associadoId);
            associadosEncontrados.forEach(associado -> associado.setEstado(estado));
        });

	}

	private List<AssociadoRelDTO> preencherTelefones(final RelatorioDTO relatorio, final List<AssociadoRelDTO> associados) {
	    final boolean telefoneIsPresent = relatorio.getOpcoes().stream()
	            .flatMap(opcaoRelatorio -> Stream.of(opcaoRelatorio.getOpcao()))
	            .anyMatch(opcaoRelatorioEnum -> opcaoRelatorioEnum.equals(OpcaoRelatorioEnum.TELEFONE));

	    if (telefoneIsPresent) {
	        final List<Long> ids = associados.stream()
	                .map(AssociadoRelDTO::getId)
	                .distinct()
	                .collect(Collectors.toUnmodifiableList());

	        final CriteriaBuilder builder = connection.getCriteriaBuilder();
	        final CriteriaQuery<Tuple> criteria = builder.createQuery(Tuple.class);
	        criteria.distinct(true);

	        final Root<Associado> root = criteria.from(Associado.class);
	        final Join<Associado, Telefone> joinAssociadoTelefone = root.join("telefones", JoinType.LEFT);
	        final Predicate predicate = root.get("id").in(ids);

	        criteria.multiselect(List.of(root.get("id"), joinAssociadoTelefone.get("numero")));
	        criteria.where(predicate);

	        final List<Tuple> resultList = connection.createQuery(criteria).getResultList();

	        preenchaTelefoneAssociadoSeEncontrado(associados, resultList);
	    }

	    return associados;
	}


	private void preenchaTelefoneAssociadoSeEncontrado(final List<AssociadoRelDTO> associados, final List<Tuple> resultList) {
		final Map<Long, Set<String>> associadosTelefones = resultList.stream()
			    .collect(Collectors.groupingBy(
			        tuple -> tuple.get(0, Long.class), 
			        Collectors.mapping(tuple -> tuple.get(1, String.class), Collectors.toSet())
			    ));

	    associados.forEach(associado -> {
	        final Long id = associado.getId();
	        final Set<String> telefones = associadosTelefones.get(id);
	        associado.setTelefones(new HashSet<>());
	        Optional.ofNullable(telefones)
	        .ifPresent(t -> t.stream().filter(StringUtils::isNotEmpty).forEach(associado.getTelefones()::add));
	    });
	}
	
	private List<AssociadoRelDTO> preencherMunicipioAtividade(final RelatorioDTO relatorio, final List<AssociadoRelDTO> associados) {
		final boolean municipioAtividadeIsPresent = relatorio.getOpcoes().stream()
				.flatMap(opcaoRelatorio -> Stream.of(opcaoRelatorio.getOpcao()))
				.anyMatch(OpcaoRelatorioEnum.ATIVIDADE::equals);
		
		if(municipioAtividadeIsPresent) {
			final List<Long> ids = associados.stream().map(AssociadoRelDTO::getId)
					.distinct().collect(Collectors.toUnmodifiableList());
			
			final CriteriaBuilder builder = connection.getCriteriaBuilder();
			final CriteriaQuery<MunicipioAtividade> criteria = builder.createQuery(MunicipioAtividade.class);
			criteria.distinct(true);
			
			final Root<Associado> root = criteria.from(Associado.class);
			final Join<Associado, MunicipioAtividade> joinAssociadoMunAtividade = root.join("municipiosAtividade", JoinType.LEFT);
			final Predicate predicate = root.get("id").in(ids);
			
			criteria.select(joinAssociadoMunAtividade);
			criteria.where(predicate);
			
			final List<MunicipioAtividade> resultList = connection.createQuery(criteria).getResultList();
			
			final List<Long> idsList = associados.stream()
	                .flatMap(associadoRelDTO -> Stream.of(associadoRelDTO.getId())).collect(Collectors.toUnmodifiableList());
			
	        final Map<Long, Set<MunicipioAtividade>> mapOfMunicipiosAtividade = new HashMap<>();
	        
	        resultList.stream().filter(Objects::nonNull)
	        .filter(municipioAtividade -> Objects.nonNull(municipioAtividade.getAssociado()))
	        .forEach(municipioAtividade -> {
	            Long id = municipioAtividade.getAssociado().getId();
	            mapOfMunicipiosAtividade.merge(id, Set.of(municipioAtividade), (m1, m2) -> {
	                Set<MunicipioAtividade> merged = new HashSet<>(m1);
	                merged.addAll(m2);
	                return merged;
	            });
	        });
	                
			associados.stream()
			        .filter(associado -> idsList.contains(associado.getId()))
			        .forEach(associado -> Optional.ofNullable(mapOfMunicipiosAtividade.get(associado.getId()))
			                .ifPresent(municipios -> associado.setMunicipiosAtividade(new HashSet<>(municipios))));
		}
		
		return associados;
	}

	private void preencherAssociadosDTO(final List<String> opcoesSelecionadas,
			final List<Tuple> resultList,
			final List<AssociadoRelDTO> associados) {

		for (Tuple tuple : resultList) {

			final AssociadoRelDTO associado = new AssociadoRelDTO();
			Field[] fields = associado.getClass().getDeclaredFields();

			preencherFields(opcoesSelecionadas, tuple, associado, fields);

			fields = associado.getClass().getSuperclass().getDeclaredFields();

			preencherFields(opcoesSelecionadas, tuple, associado, fields);

			associados.add(associado);
		}
	}

	private void preencherFields(final List<String> opcoesSelecionadas, 
			final Tuple tuple, final AssociadoRelDTO associado, final Field[] fields) {
		
		final Map<String, Method> allSetMethods = methodMapUtils.createSetMethodMap(associado.getClass());
		
	    opcoesSelecionadas.forEach(opcao -> Stream.of(fields)
            .filter(field -> field.getName().equals(opcao))
            .filter(field -> allSetMethods.containsKey(field.getName()))
            .findFirst()
            .ifPresent(field -> {
                try {
                    Object object = tuple.get(opcao);
                    Object novoValor = null;
                    if (object instanceof Timestamp) {
                        novoValor = converteTimeStamp((Timestamp) object);
                    } else if (object instanceof Time) {
                        novoValor = converteTime((Time) object);
                    } else if (object instanceof Date && Objects.nonNull(object)) {
                    	final Date date = ((Date) object);
                        final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        novoValor = formatter.format(date);
                    } else {
                        novoValor = object;
                    }
                    
                    allSetMethods.get(field.getName()).invoke(associado, novoValor);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    log.error(e);
                }
            })
	    );
	}

	private void filtrarPorMunicipio(final List<Predicate> predicates, final CriteriaBuilder builder,
			final CriteriaQuery<Tuple> criteria) {
		for (Predicate predicate : predicates) {
			if (criteria.getRestriction() != null) {
				criteria.where(builder.and(criteria.getRestriction(), predicate));
			} else {
				criteria.where(predicate);
			}
		}
	}

	private void preencherPredicates(RelatorioDTO relatorio, final List<Predicate> predicates,
			final CriteriaBuilder builder, Root<Associado> root) {
		
		boolean hasToGetJoinMunicAtividade = (relatorio.getMunicipioAtividade() != null || relatorio.getRegiao() != null);
		hasToGetJoinMunicAtividade = hasToGetJoinMunicAtividade && joinAssociadoMunicipioAtividade == null;
		
		if(hasToGetJoinMunicAtividade) {	
			preencherJoinMunicipioAtividade(relatorio, root);
			joinAssociadoMunicipioAtividade = root.join("municipiosAtividade", JoinType.INNER);
		}

		if (relatorio.getMunicipioAtividade() != null) {
			predicates.add(
					builder.equal(joinAssociadoMunicipioAtividade.get("municipio"), relatorio.getMunicipioAtividade()));
		}
		if (relatorio.getMunicipioEleitoral() != null) {
			predicates.add(builder.equal(root.get("municipioEleitoral"), relatorio.getMunicipioEleitoral()));
		}
		if(relatorio.getRegiao() != null) {
			preencherJoinRegiao();
			predicates.add(builder.equal(joinMunicipioRegiao.get("id"), relatorio.getRegiao().getCodigoRegiao()));
		}
	}
	
	private void preencherDataFiliacao(final RelatorioDTO relatorio, 
			final CriteriaBuilder builder, 
			final Root<Associado> root, 
			final List<Predicate> predicates) {
		
		
		final Path<Date> pathDataFiliacao = root.<Date>get("dataFiliacao");
		
	    if(Objects.nonNull(relatorio.getDtInicialAssociado())
	    		&& Objects.nonNull(relatorio.getDtFinalAssociado())) {
	    	predicates.add(builder.and(
				builder.greaterThanOrEqualTo(pathDataFiliacao, getDataFiliacao(relatorio.getDtInicialAssociado())),
				builder.lessThanOrEqualTo(pathDataFiliacao, relatorio.getDtFinalAssociado())
			));
	    } else if(Objects.nonNull(relatorio.getDtInicialAssociado())) {
	    	predicates.add(builder.greaterThanOrEqualTo(pathDataFiliacao, getDataFiliacao(relatorio.getDtInicialAssociado())));
	    } else if(Objects.nonNull(relatorio.getDtFinalAssociado())) {
	    	predicates.add(builder.lessThanOrEqualTo(pathDataFiliacao, getDataFiliacao(relatorio.getDtFinalAssociado())));
	    }
	}
	
	private Date getDataFiliacao(final Date date) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		return calendar.getTime();
	}

	private void corrigirRelatorioDaOpcaoRelatorio(Relatorio relatorio) {
		for (OpcaoRelatorio opcaoRelatorio : relatorio.getOpcoes()) {
			opcaoRelatorio.setRelatorio(relatorio);
		}
	}

	private String converteTime(final Time time) {
		final SimpleDateFormat formatter = new SimpleDateFormat("dd/mm/yyyy");

		if (time != null) {
			return formatter.format(time);
		}

		return "";
	}

	private String converteTimeStamp(final Timestamp time) {
		final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		if (time != null) {
			return sdf.format(time);
		}

		return "";
	}
	
	private void preencherJoinRegiao() {
		if(joinMunicipioRegiao == null) {
			final Join<MunicipioAtividade, Municipio> joinMunicipioAtividadeMunicipio = joinAssociadoMunicipioAtividade.join("municipio", JoinType.LEFT);
			joinMunicipioRegiao = joinMunicipioAtividadeMunicipio.join("regiao", JoinType.LEFT);
		}
	}
	
	private void preencherJoinMunicipioAtividade(final RelatorioDTO relatorio, final Root<Associado> root) {
		if(joinAssociadoMunicipioAtividade == null) {			
			final JoinType joinTypeCustom = relatorio.getMunicipioAtividade() != null ? JoinType.INNER : JoinType.LEFT;
			joinAssociadoMunicipioAtividade = root.join("municipiosAtividade", joinTypeCustom);
		}
	}
	
	private void preencherJoinAssociadoMunicipio(final Root<Associado> root) {
		if(joinAssociadoMunicipio == null) {
			joinAssociadoMunicipio = root.join("municipio", JoinType.LEFT);
		}
	}

}