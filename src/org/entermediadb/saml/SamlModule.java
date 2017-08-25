package org.entermediadb.saml;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.entermediadb.asset.MediaArchive;
import org.entermediadb.asset.modules.BaseMediaModule;
import org.openedit.WebPageRequest;
import org.openedit.event.WebEvent;
import org.openedit.page.manage.PageManager;
import org.openedit.users.User;
import org.openedit.users.UserSearcher;

import com.onelogin.saml2.Auth;
import com.onelogin.saml2.settings.Saml2Settings;

public class SamlModule extends BaseMediaModule
{
	protected PageManager fieldPageManager;
	
	private static final Log log = LogFactory.getLog(SamlModule.class);

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
			inReq.setCancelActions(true);
			inReq.setHasRedirected(true);

			
			
	}
	
	
	public void processResponse(WebPageRequest inReq)  throws Exception{
		
		
		Auth auth = 	new Auth( inReq.getRequest(), inReq.getResponse());
		auth.processResponse();
		if(auth.isAuthenticated()){
			Map<String, List<String>> attributes = auth.getAttributes();
			String metadata = auth.getSettings().getSPMetadata();

			log.info(metadata);
			
		} else{
			
			log.info(auth.getLastErrorReason());

		}
		MediaArchive archive = getMediaArchive(inReq);
		UserSearcher searcher = (UserSearcher) archive.getSearcher("user");
//		User target = searcher.getUserByEmail(email);
//		if (target == null)
//		{
//			target = (User) searcher.createNewData();
//			target.setFirstName(firstname);
//			target.setLastName(lastname);
//			target.setEmail(email);
//			target.setEnabled(true);
//			searcher.saveData(target, null);			
//		}
//		if(target != null){
//			
//			inReq.putSessionValue(searcher.getCatalogId() + "user", target);
//			String md5 = getCookieEncryption().getPasswordMd5(target.getPassword());
//			String value = target.getUserName() + "md542" + md5;
//			inReq.putPageValue("entermediakey", value);
//			inReq.putPageValue( "user", target);
//			if(getEventManager() != null)
//			{
//				WebEvent event = new WebEvent();
//				event.setSearchType("userprofile");
//				event.setCatalogId(searcher.getCatalogId());
//				event.setOperation("saved");
//				event.setProperty("dataid", target.getId());
//				event.setProperty("id", target.getId());
//
//				event.setProperty("applicationid", inReq.findValue("applicationid"));
//
//				getEventManager().fireEvent(event);
//			}
//
//
//		}

		
		
		
	}
	
	
	
	
	
	
	
}
