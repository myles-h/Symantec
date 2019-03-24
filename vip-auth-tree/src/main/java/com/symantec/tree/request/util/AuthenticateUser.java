package com.symantec.tree.request.util;

import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.forgerock.openam.auth.node.api.NodeProcessException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.identity.shared.debug.Debug;
import com.symantec.tree.config.Constants.VIPAuthStatusCode;

/**
 * 
 * @author Sacumen (www.sacumen.com) <br> <br> 
 * Authenticate user using AuthenticateUserWithPushRequest
 *
 */
public class AuthenticateUser {

	private final Debug debug = Debug.getInstance("VIP");

	/**
	 * 
	 * @param userName
	 * @param displayMsgText
	 * @param displayMsgTitle
	 * @param displayMsgProfile
	 * @return transaction id if success else, null
	 * @throws NodeProcessException 
	 */
	public String authUser(String userName, String displayMsgText, String displayMsgTitle, String displayMsgProfile,
			String key_store,String key_store_pass) throws NodeProcessException {
		String transactionID = "";

		String payLoad = getViewUserPayload(userName, displayMsgText, displayMsgTitle, displayMsgProfile);
		
		debug.message("AuthenticateUserWithPushRequest Payload: " + payLoad);
		
		Document doc = HttpClientUtil.getInstance().executeRequst(getURL(), payLoad);

        String status = doc.getElementsByTagName("status").item(0).getTextContent();
		
        if (VIPAuthStatusCode.SUCCESS_CODE.equals(status)) {
				transactionID = doc.getElementsByTagName("transactionId").item(0).getTextContent();
			}

		return transactionID;
	}

	/**
	 * 
	 * @param userId
	 * @param displayMsgText
	 * @param displayMsgTitle
	 * @param displayMsgProfile
	 * @return AuthenticateUserWithPushRequest payload
	 */
	private String getViewUserPayload(String userId, String displayMsgText, String displayMsgTitle,
											 String displayMsgProfile) {
		debug.message("getting payload for AuthenticateUserWithPushRequest");

		return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
				"xmlns:vip=\"https://schemas.symantec.com/vip/2011/04/vipuserservices\">" +
				"<soapenv:Header/>" +
				"<soapenv:Body>" +
				"<vip:AuthenticateUserWithPushRequest>" +
				"<vip:requestId>" + Math.round(Math.random() * 100000) + "</vip:requestId>" +
				"<!--Optional:-->" +
				"" +
				"<vip:userId>" + userId + "</vip:userId>" +
				"<!--Optional:-->" +
				"<vip:pushAuthData>" +
				"<!--0 to 20 repetitions:-->" +
				"<vip:displayParameters>" +
				"<vip:Key>" + "display.message.text" + "</vip:Key>" +
				"<vip:Value>" + displayMsgText + "</vip:Value>" +
				"" +
				"</vip:displayParameters>" +
				"<vip:displayParameters>" +
				"<vip:Key>" + "display.message.title" + "</vip:Key>" +
				"<vip:Value>" + displayMsgTitle + "</vip:Value>" +
				"" +
				"</vip:displayParameters>" +
				"<vip:displayParameters>" +
				"<vip:Key>" + "display.message.profile" + "</vip:Key>" +
				"<vip:Value>" + displayMsgProfile + "</vip:Value>" +
				"" +
				"</vip:displayParameters>" +
				"" +
				"</vip:pushAuthData>" +
				"</vip:AuthenticateUserWithPushRequest>" +
				"</soapenv:Body>" +
				"</soapenv:Envelope>";
	}

	/**
	 * 
	 * @return AuthenticationServiceURL
	 * @throws NodeProcessException 
	 */
	private String getURL() throws NodeProcessException {
		return GetVIPServiceURL.serviceUrls.get("AuthenticationServiceURL");
	}

}
