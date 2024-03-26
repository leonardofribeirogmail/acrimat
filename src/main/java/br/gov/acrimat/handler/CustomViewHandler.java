package br.gov.acrimat.handler;

import java.io.IOException;
import java.util.Locale;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.application.ViewHandlerWrapper;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import br.gov.acrimat.util.Mensagem;
import br.gov.acrimat.util.impl.MessageProvider;

public class CustomViewHandler extends ViewHandlerWrapper {
	
	@Inject
	private Mensagem mensagem;
	
	@Inject
	private MessageProvider provider;
	
	private ViewHandler parent;
	
	public CustomViewHandler(ViewHandler parent) {
		this.parent = parent;
	}

	@Override
	public Locale calculateLocale(FacesContext context) {
		return parent.calculateLocale(context);
	}

	@Override
	public String calculateRenderKitId(FacesContext context) {
		return parent.calculateRenderKitId(context);
	}

	@Override
	public UIViewRoot createView(FacesContext context, String viewId) {
		return parent.createView(context, viewId);
	}

	@Override
	public String getActionURL(FacesContext context, String viewId) {
		return parent.getActionURL(context, viewId);
	}

	@Override
	public String getResourceURL(FacesContext context, String path) {
		return parent.getResourceURL(context, path);
	}

	@Override
	public void renderView(FacesContext context, UIViewRoot viewToRender) throws IOException, FacesException {
		parent.renderView(context, viewToRender);
	}

	@Override
	public UIViewRoot restoreView(FacesContext context, String viewId) {
		UIViewRoot root =null;
		root = parent.restoreView(context, viewId);
		if(root == null) {
			root = createView(context, viewId);
			mensagem.info("panelGrid", provider.getMensagem("info.paginarestaurada"));
		}
		return root;
	}

	@Override
	public void writeState(FacesContext context) throws IOException {
		parent.writeState(context);
	}

	@Override
	public ViewHandler getWrapped() {
		return parent;
	}

}
