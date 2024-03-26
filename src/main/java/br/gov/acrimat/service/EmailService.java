package br.gov.acrimat.service;

import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.persistence.Query;

import org.apache.commons.compress.utils.Lists;
import org.apache.xmlgraphics.image.loader.ImageException;
import org.jobrunr.jobs.JobId;

import br.gov.acrimat.dto.EmailDTO;
import br.gov.acrimat.dto.EmailMapDTO;
import br.gov.acrimat.exception.EmailNotFound;
import br.gov.acrimat.exception.EmailNotSendException;
import br.gov.acrimat.model.Associado;
import br.gov.acrimat.modelutils.impl.ModelDao;
import br.gov.acrimat.util.impl.SmtpProvider;
import lombok.extern.jbosslog.JBossLog;

@Named
@JBossLog
@ApplicationScoped
public class EmailService extends ModelDao<Associado> {

	private static final long serialVersionUID = 1L;
	
	private static final String SMTP_AUTH = "mail.smtp.auth";
	private static final String SMTP_HOST = "mail.smtp.host";
	private static final String SMTP_PORT = "mail.smtp.port";
	private static final String SMTP_USERNAME = "mail.smtp.username";
	private static final String SMTP_PASSWORD = "mail.smtp.password";
	private static final String SMTP_SSL_TRUST = "mail.smtp.ssl.trust";
	private static final String SMTP_START_TLS_ENABLED = "mail.smtp.starttls.enable";
	private static final String SMTP_MAX_QUANTITY = "mail.smtp.max.quantity";
	
	private SmtpProvider smtpProvider;
	
	private JobRunrService jobRunrService;
	
	private transient Session session;
	
	protected EmailService() {
		//It won't be used
	}
	
	@Inject
	public EmailService(final JobRunrService jobRunrService,
			final SmtpProvider smtpProvider) {
		this.smtpProvider = smtpProvider;
		this.jobRunrService = jobRunrService;
		this.session = getSession();
		setClazz(Associado.class);
	}
	
	public void send(final EmailDTO email) {
		
		Instant when = null;
		JobId schedule = null;
		final HashMap<Integer, List<String>> jobList = createEmails();
		
		for(Map.Entry<Integer, List<String>> mapEntry : jobList.entrySet()) {
			
			if(when == null) {
				when = Clock.systemUTC().instant();
			}
			
			try {
				final UUID jobId = UUID.randomUUID();
				email.setJobId(jobId.toString());
				email.setProperties(getProperties());
				email.setUserName(smtpProvider.getMensagem(SMTP_USERNAME));
				email.setPassword(smtpProvider.getMensagem(SMTP_PASSWORD));
				email.setEmailMap(new EmailMapDTO(mapEntry.getKey(), new ArrayList<>(mapEntry.getValue())));
				
				schedule = jobRunrService.getJobRunr().getJobScheduler().schedule(
					jobId, when, ()->startJob(email)
				);
				log.infof("Lista de email {%s} programada para %s", mapEntry.getKey(), when);
			} catch(Exception e) {
				if(schedule != null) {
					jobRunrService.getJobRunr().getJobScheduler().delete(schedule);
				}
				throw new EmailNotSendException(e.getLocalizedMessage(), e);
			} finally {
				when = when.plus(Duration.ofMinutes(1));
			}
		}
	}
	
	public void startJob(final EmailDTO email) throws IOException, ImageException {
		
		session = getSession(email.getProperties(), email.getUserName(), email.getPassword());
		
		final EmailScheduleService emailScheduleService = new EmailScheduleService(session);
		emailScheduleService.sendMessage(email);
	}
	
	private final Properties getProperties() {
		final Properties prop = new Properties();
		final String mensagem = smtpProvider.getMensagem(SMTP_AUTH);
		prop.put(SMTP_AUTH, mensagem);
		prop.put(SMTP_START_TLS_ENABLED, smtpProvider.getMensagem(SMTP_START_TLS_ENABLED));
		prop.put(SMTP_HOST, smtpProvider.getMensagem(SMTP_HOST));
		prop.put(SMTP_PORT, smtpProvider.getMensagem(SMTP_PORT));
		prop.put(SMTP_SSL_TRUST, smtpProvider.getMensagem(SMTP_SSL_TRUST));
		return prop;
	}
	
	private final Session getSession() {
		return getSession(
			getProperties(),
			smtpProvider.getMensagem(SMTP_USERNAME), 
			smtpProvider.getMensagem(SMTP_PASSWORD)
		);
	}
	
	private final Session getSession(final Properties properties, 
			final String userName, final String password) {
		if(session != null) {
			return session;
		}
		
		return Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(
					userName, 
					password
				);
			}
		});
	}
	
	private final HashMap<Integer, List<String>> createEmails() {
		
		final HashMap<Integer, List<String>> emailsMap = new HashMap<>();
		
		Integer count = 0;
		final Integer maxQuantity = Integer.valueOf(smtpProvider.getMensagem(SMTP_MAX_QUANTITY));
		
		final List<String> emailsList = getEmails();
		final Iterator<String> iterator = emailsList.iterator();
		
		while(iterator.hasNext()) {
			emailsMap.computeIfAbsent(count, key -> Lists.newArrayList());
			emailsMap.get(count).add(iterator.next());
			
			if(emailsMap.get(count).size() >= maxQuantity) {
				count++;
			}
		}
		
		return emailsMap;
	}
	
	@SuppressWarnings("unchecked")
	private final List<String> getEmails() {
		
		List<String> emails = Lists.newArrayList();
		final StringBuilder builder = new StringBuilder();
		
		try {
			builder.append("select distinct associado.email from associados as associado ");
			builder.append("where associado.email ");
			builder.append("REGEXP '^[^@]+@[^@]+.[^@]{2,}$' and associado.email != '' ");
			builder.append("and associado.ativo = 'S' ");
			builder.append("group by associado.email order by associado.email");
			
			final Query nativeQuery = getEntityManager().createNativeQuery(builder.toString());
			emails = nativeQuery.getResultList();
		} catch(Exception e) {
			throw new EmailNotFound(e);
		}
		
		return emails;
	}
}
