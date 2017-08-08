package org.entermediadb.saml;

import javax.servlet.http.HttpServletRequest;

import org.entermediadb.asset.modules.BaseMediaModule;
import org.openedit.WebPageRequest;

import com.onelogin.saml2.Auth;

public class SamlModule extends BaseMediaModule
{

	
	public void startAuthentication(WebPageRequest inReq) throws Exception{
		HttpServletRequest request = inReq.getRequest();
		Auth auth = new Auth(request, inReq.getResponse());
		if (request.getParameter("attrs") == null) {
			auth.login();
		} else {
			String x = request.getPathInfo();
			auth.login("/java-saml-tookit-jspsample/attrs.jsp");
		}
	}
	
	
}
