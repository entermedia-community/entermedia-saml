package org.entermediadb.saml;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.entermediadb.asset.modules.BaseMediaModule;
import org.openedit.OpenEditException;
import org.openedit.WebPageRequest;
import org.openedit.data.SearcherManager;
import org.openedit.event.EventManager;
import org.openedit.event.WebEvent;
import org.openedit.page.manage.PageManager;
import org.openedit.users.User;
import org.openedit.users.UserManager;
import org.openedit.users.UserSearcher;

import com.onelogin.saml2.Auth;
import com.onelogin.saml2.settings.Saml2Settings;

public class SamlModule extends BaseMediaModule
{
	protected PageManager fieldPageManager;
	protected UserManager fieldUserManager;
	protected EventManager fieldEventManager;
	protected SearcherManager fieldSearcherManager;

	
	
	public PageManager getPageManager()
	{
		return fieldPageManager;
	}


	public void setPageManager(PageManager inPageManager)
	{
		fieldPageManager = inPageManager;
	}


	public UserManager getUserManager()
	{
		return fieldUserManager;
	}


	public void setUserManager(UserManager inUserManager)
	{
		fieldUserManager = inUserManager;
	}


	public EventManager getEventManager()
	{
		return fieldEventManager;
	}


	public void setEventManager(EventManager inEventManager)
	{
		fieldEventManager = inEventManager;
	}


	private static final Log log = LogFactory.getLog(SamlModule.class);

	public void startAuthentication(WebPageRequest inReq) throws Exception{

		HttpServletRequest request = inReq.getRequest();
		String appid = inReq.findValue("applicationid");
		inReq.getRequest().getSession().setAttribute("targetapp", appid);
		String catalogid = inReq.findValue("catalogid");
		inReq.getRequest().getSession().setAttribute("catalogid", appid);
		log.info("Checking SAML authentication");
		
		
		Auth auth = 	new Auth( request, inReq.getResponse());
		List settings = auth.getSettings().checkSPSettings();
		Boolean certs = auth.getSettings().checkSPCerts();
		Saml2Settings settings3 = auth.getSettings();
		String metadata = settings3.getSPMetadata();
		
		List<String> errors = Saml2Settings.validateMetadata(metadata);
		
		String x = request.getPathInfo();
		auth.login("/saml/consume.html");
		
			
			
	}
	
	public void loadMetadata(WebPageRequest inReq) throws Exception{

		HttpServletRequest request = inReq.getRequest();
		String appid = inReq.findValue("applicationid");
		inReq.getRequest().getSession().setAttribute("targetapp", appid);
		String catalogid = inReq.findValue("catalogid");
		inReq.getRequest().getSession().setAttribute("catalogid", appid);
		log.info("Checking SAML authentication");
		
		
		Auth auth = 	new Auth( request, inReq.getResponse());
		Saml2Settings settings3 = auth.getSettings();
		String metadata = settings3.getSPMetadata();
		
		inReq.putPageValue("metadata", metadata);
			
			
	}
	
	
	
	
	public void processResponse(WebPageRequest inReq)  throws Exception{
		
		
		Auth auth = 	new Auth( inReq.getRequest(), inReq.getResponse());
		auth.processResponse();
		log.info("Checking SAML response");

		if(auth.isAuthenticated()){
			Map<String, List<String>> attributes = auth.getAttributes();
			log.info(attributes);
			log.info(attributes.keySet());
			String metadata = auth.getSettings().getSPMetadata();
			String targetapp = (String) inReq.getSessionValue("targetapp");
			String targetcatalog = (String) inReq.getSessionValue("targetcatalog");
			if(targetcatalog == null){
				targetcatalog = inReq.findValue("targetcatalog");
			}
			if(targetcatalog == null){
				targetcatalog = "system";
			}
			
			
			
			if(targetapp == null){
				targetcatalog = inReq.findValue("targetapp");
			}
			
			
		//	MediaArchive archive = getMediaArchive(inReq);
			UserSearcher searcher = (UserSearcher) getSearcherManager().getSearcher(targetcatalog,"user");
			
			String emailattribute = getMediaArchive(targetcatalog).getCatalogSettingValue("saml-email-attribute");
			if(emailattribute == null){
				emailattribute = "User.email";
			}
			if(attributes.get(emailattribute) == null){
				throw new OpenEditException("Couldn't find email to match in attributes.  Attributes available are " + attributes + " Please add saml-email-attribute to catalogsettings.  Catalog was: " + targetcatalog);
			}
			String email = attributes.get(emailattribute).get(0);
			
			//TODO - map these to property detail lookups.
			
//			String firstname = null;
//			String lastname = null;
//
//			if(attributes.get("User.FirstName").size() > 0){
//				firstname = attributes.get("User.FirstName").get(0);
//			}
//
//			if(attributes.get("User.LastName").size() > 0){
//				lastname = attributes.get("User.LastName").get(0);
//			}
//			
			
			
			User target = searcher.getUserByEmail(email);
			if (target == null)
			{
				target = (User) searcher.createNewData();
//				target.setFirstName(firstname);
//				target.setLastName(lastname);
				target.setEmail(email);
				target.setEnabled(true);
				searcher.saveData(target, null);			
			}
			if(target != null){
				
				inReq.putSessionValue(searcher.getCatalogId() + "user", target);
//				String md5 = getCookieEncryption().getPasswordMd5(target.getPassword());
//				String value = target.getUserName() + "md542" + md5;
//				inReq.putPageValue("entermediakey", value);
				inReq.putPageValue( "user", target);
				if(getEventManager() != null)
				{
					WebEvent event = new WebEvent();
					event.setSearchType("userprofile");
					event.setCatalogId(searcher.getCatalogId());
					event.setOperation("saved");
					event.setProperty("dataid", target.getId());
					event.setProperty("id", target.getId());
	
					event.setProperty("applicationid", inReq.findValue("applicationid"));
	
					getEventManager().fireEvent(event);
				}
	
	
			}

			
			if(targetapp != null){
				inReq.redirect("/" + targetapp + "/index.html");
			}
			else{
				inReq.redirect("/index.html");

			}
			
		} else{
			log.info("Couldn't authenticate: ");

			log.info(auth.getLastErrorReason());

		}
		
		
		
	}
	
	
	
	public void handleRedirect(WebPageRequest inReq){
		
		String targetapp = (String) inReq.getSessionValue("targetapp");
		if(inReq.getUser() != null){
			if(targetapp != null){
				inReq.redirect("/" + targetapp + "/index.html");
			}
			else{
				inReq.redirect("/index.html");

			}
			
			
		}
		
		
	}
	
	
	
}
