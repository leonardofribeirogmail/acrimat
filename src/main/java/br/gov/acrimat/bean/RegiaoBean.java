package br.gov.acrimat.bean;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import br.gov.acrimat.dto.RegiaoDTO;
import br.gov.acrimat.modelutils.impl.ModelBean;
import br.gov.acrimat.service.RegiaoService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jbosslog.JBossLog;

@Named
@JBossLog
@SessionScoped
public class RegiaoBean extends ModelBean {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private RegiaoService service;
	
	@Getter @Setter
	private RegiaoDTO regiaoDTO;
	
	@Getter
	private List<RegiaoDTO> listaRegiaoDto;
	
	@PostConstruct
	public void init() {
		try {
			listaRegiaoDto = service.selecionarRegioesMunicipioAtv();			
		} catch (Exception e) {
			log.error(e);
		}
	}
}
