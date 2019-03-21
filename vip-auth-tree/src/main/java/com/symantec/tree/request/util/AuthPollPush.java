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

/**
 * 
 * @author Sacumen (www.sacumen.com) <br> <br>
 * getting status of poll push request using PollPushStatusRequest
 *
 */
public class AuthPollPush {

	private final Debug debug = Debug.getInstance("VIP");

	/**
	 * 
	 * @param authId
	 * @return response status code
	 * @throws NodeProcessException 
	 */
	public String authPollPush(String authId,String key_store,String key_store_pass) throws NodeProcessException {

		HttpClientUtil clientUtil = HttpClientUtil.getInstance();
		HttpPost post = new HttpPost(getURL());
		post.setHeader("CONTENT-TYPE", "text/xml; charset=ISO-8859-1");
		String payLoad = getViewUserPayload(authId);
		debug.message("Request Payload in authPollPush: " + payLoad);
		try {
			//TODO Duplicate Code
			HttpClient httpClient = clientUtil.getHttpClientForgerock(key_store,key_store_pass);
			post.setEntity(new StringEntity(payLoad));
			HttpResponse response = httpClient.execute(post);
			HttpEntity entity = response.getEntity();
			String body = IOUtils.toString(entity.getContent());
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource src = new InputSource();
			src.setCharacterStream(new StringReader(body));
			Document doc = builder.parse(src);
			String status = doc.getElementsByTagName("status").item(1).getTextContent();
			return status;

		} catch (IOException | ParserConfigurationException | SAXException e) {
			debug.error("Not able to process Request");
			throw new NodeProcessException(e);
		}
	}

	/**
	 * 
	 * @param authId
	 * @return PollPushStatusRequest payload
	 */
	private String getViewUserPayload(String authId) {
		debug.message("getting payload for PollPushStatusRequest");
		return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
				"xmlns:vip=\"https://schemas.symantec.com/vip/2011/04/vipuserservices\">" +
				"<soapenv:Header/>" +
				"<soapenv:Body>" +
				"<vip:PollPushStatusRequest>" +
				"<vip:requestId>" + new Random().nextInt(10) + 11111 + "</vip:requestId>" +
				"<vip:transactionId>" + authId + "</vip:transactionId>" +
				"</vip:PollPushStatusRequest>" +
				"</soapenv:Body>" +
				"</soapenv:Envelope>";

	}

	/**
	 * 
	 * @return QueryServiceURL
	 * @throws NodeProcessException 
	 */
	private String getURL() throws NodeProcessException {
		return GetVIPServiceURL.serviceUrls.get("QueryServiceURL");
	}

}
