package br.gov.acrimat.listener;

import java.lang.reflect.Field;

import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import br.gov.acrimat.model.Associado;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
public class AssociadoListener {

	@PostLoad
	public void beforeLoad(final Associado associado) {
		preencherField(associado);
	}
	
	@PreUpdate
	@PrePersist
	public void afterLoad(final Associado associado) {
		preencherField(associado);
	}
	
	private void preencherField(final Associado associado) {
		Field[] fields = associado.getClass().getDeclaredFields();
		for(Field field : fields) {
			field.setAccessible(true);
			try {
				Object generico = field.get(associado);
				if(generico != null && field.getType().equals(String.class)) {
					field.set(associado, String.valueOf(generico).trim());
				}
			} catch(Exception e) {
				log.error(e.getLocalizedMessage(), e);
			}
		}
	}
}
