package com.symantec.tree.request.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.Random;

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

public class ConfirmRisk {
	private final Debug debug = Debug.getInstance("VIP");
		
	public String confirmRisk(String userName,String eventID,String key_store,String key_store_pass) throws NodeProcessException {
		HttpClientUtil clientUtil = HttpClientUtil.getInstance();
		HttpPost post = new HttpPost(getURL());
		post.setHeader("CONTENT-TYPE", "text/xml; charset=ISO-8859-1");
		String payLoad = getPayload(userName,eventID);
		debug.message("Confirm Risk Request Payload: " + payLoad);
		String status;
		try {
			//TODO Duplicate Code
			HttpClient httpClient = clientUtil.getHttpClientForgerock(key_store,key_store_pass);
			post.setEntity(new StringEntity(payLoad));
			HttpResponse response = httpClient.execute(post);
			HttpEntity entity = response.getEntity();
			String body = IOUtils.toString(entity.getContent());
			debug.message("Confirm Risk Response is "+body);
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource src = new InputSource();
			src.setCharacterStream(new StringReader(body));
			Document doc = builder.parse(src);
			String statusMessage = doc.getElementsByTagName("statusMessage").item(0).getTextContent();
			status = doc.getElementsByTagName("status").item(0).getTextContent();
			
			debug.message("Confirm Risk request response code is "+status);
			
		} catch (IOException | ParserConfigurationException | SAXException e) {
			throw new NodeProcessException(e);
		}

		return status;
	}
	
	private String getPayload(String userName,String eventID) {
		debug.message("getting payload for Confirm Risk");
		return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" "
		       + "xmlns:vip=\"https://schemas.symantec.com/vip/2011/04/vipuserservices\">" + "<soapenv:Header/>"
		       + "<soapenv:Body>" + "<vip:ConfirmRiskRequest>" + "<vip:requestId>" + new Random().nextInt(10) + 11111
		       + "</vip:requestId>" + "<vip:UserId>" + userName + "</vip:UserId>"
		       + "<vip:EventId>" + eventID + "</vip:EventId>"
		       + "</vip:ConfirmRiskRequest>" + "</soapenv:Body>"
		       + "</soapenv:Envelope>";

	}
	
	/**
	 * 
	 * @return AuthenticationServiceURL
	 */
	private String getURL() {
		return GetVIPServiceURL.serviceUrls.get("AuthenticationServiceURL");
	}
}
