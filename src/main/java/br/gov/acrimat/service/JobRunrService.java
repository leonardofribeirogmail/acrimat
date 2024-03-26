package br.gov.acrimat.service;

import java.io.Serializable;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.jobrunr.configuration.JobRunr;
import org.jobrunr.configuration.JobRunrConfiguration.JobRunrConfigurationResult;
import org.jobrunr.server.BackgroundJobServerConfiguration;
import org.jobrunr.storage.InMemoryStorageProvider;

import lombok.Getter;

@Named
@ApplicationScoped
public class JobRunrService implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Resource(lookup = "java:jboss/datasources/acrimathml")
	private transient DataSource dataSource;
	
	@Getter
	private transient JobRunrConfigurationResult jobRunr;
	
	@Inject
	protected JobRunrService() {
		jobRunr = createJobRunrConfiguration();
	}
	
	private JobRunrConfigurationResult createJobRunrConfiguration() {
		if(jobRunr == null) {			
			jobRunr = JobRunr.configure()
			.useStorageProvider(new InMemoryStorageProvider())
			.useBackgroundJobServer(BackgroundJobServerConfiguration.usingStandardBackgroundJobServerConfiguration())
			.initialize();
		}
		
		return jobRunr;
	}
}
