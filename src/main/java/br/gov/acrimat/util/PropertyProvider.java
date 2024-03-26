package br.gov.acrimat.util;

import java.util.Objects;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;

public interface PropertyProvider {
	
	String KEY = "error.errointerno";
	
    default ResourceBundle getBundle(ResourceBundle bundle, final String propertyName) {
		if (bundle == null) {
			final FacesContext context = FacesContext.getCurrentInstance();
			bundle = context.getApplication().getResourceBundle(context, propertyName);
		}
		return bundle;
	}

    default String getMensagem(final ResourceBundle bundle, final String key) {
    	if(Objects.isNull(bundle) || StringUtils.isBlank(key)) {
    		return KEY;
    	}
		
		final boolean contem = bundle.containsKey(key);
		return contem ? bundle.getString(key) : key;
	}
}
