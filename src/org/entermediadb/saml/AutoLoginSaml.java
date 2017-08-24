package org.entermediadb.saml;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.entermediadb.authenticate.AutoLoginProvider;
import org.entermediadb.authenticate.AutoLoginResult;
import org.entermediadb.authenticate.BaseAutoLogin;
import org.openedit.OpenEditException;
import org.openedit.WebPageRequest;
import org.openedit.util.StringEncryption;

import com.onelogin.saml2.Auth;
import com.onelogin.saml2.settings.Saml2Settings;

public class AutoLoginSaml extends BaseAutoLogin implements AutoLoginProvider
{
		private static final Log log = LogFactory.getLog(AutoLoginSaml.class);



	public AutoLoginResult autoLogin(WebPageRequest inReq)
	{
		try
		{
			HttpServletRequest request = inReq.getRequest();
			
			Auth auth = 	new Auth( request, inReq.getResponse());
		
			
				auth.login("/saml/consume.html");
			
		}
		catch (Exception e)
		{
		throw new OpenEditException(e);
		}
		return null;

	}

}
