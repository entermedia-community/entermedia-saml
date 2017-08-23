package org.entermediadb.saml;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.entermediadb.asset.modules.BaseMediaModule;
import org.openedit.WebPageRequest;
import org.openedit.page.manage.PageManager;

import com.onelogin.saml2.Auth;
import com.onelogin.saml2.settings.Saml2Settings;

public class SamlModule extends BaseMediaModule
{
	protected PageManager fieldPageManager;
	
	
	public void startAuthentication(WebPageRequest inReq) throws Exception{
		HttpServletRequest request = inReq.getRequest();
		
		
		
	
		Auth auth = 	new Auth( request, inReq.getResponse());
		List settings = auth.getSettings().checkSPSettings();
		Boolean certs = auth.getSettings().checkSPCerts();
		
		Saml2Settings settings3 = auth.getSettings();
		String metadata = settings3.getSPMetadata();
		List<String> errors = Saml2Settings.validateMetadata(metadata);
		
		String x = request.getPathInfo();
			auth.login("/saml/consume.html");
						

			
			
	}
	
	
	public void processResponse(WebPageRequest inReq)  throws Exception{
		
		
		Auth auth = 	new Auth( inReq.getRequest(), inReq.getResponse());
		
		auth.processResponse();
		if(auth.isAuthenticated()){
			Map<String, List<String>> attributes = auth.getAttributes();
			String metadata = auth.getSettings().getSPMetadata();

			
		}
		
		
		
		
	}
	
	
	
	
	
	
	
}
