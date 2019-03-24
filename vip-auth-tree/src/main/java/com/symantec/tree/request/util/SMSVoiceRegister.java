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
 * @author Sacumen (www.sacumen.com) <br>
 *         <br>
 *         Executing RegisterRequest for SMS and Voice
 */
public class SMSVoiceRegister {
	private final Debug debug = Debug.getInstance("VIP");

	/**
	 * 
	 * @param credValue register SMS
	 * @throws NodeProcessException
	 */
	public String smsRegister(String credValue, String key_store, String key_store_pass) throws NodeProcessException {
		String payLoad = getSmsPayload(credValue);
		String status;
		debug.message("Request Payload: " + payLoad);

		Document doc = HttpClientUtil.getInstance().executeRequst(getURL(), payLoad);

		status = doc.getElementsByTagName("status").item(0).getTextContent();

		return status;

	}

	/**
	 * 
	 * @param credValue register voice
	 * @throws NodeProcessException
	 */
	public String voiceRegister(String credValue, String key_store, String key_store_pass) throws NodeProcessException {
		String payLoad = getVoicePayload(credValue);
		String status;
		debug.message("Request Payload: " + payLoad);

		Document doc = HttpClientUtil.getInstance().executeRequst(getURL(), payLoad);

		status = doc.getElementsByTagName("status").item(0).getTextContent();

		return status;

	}

	/**
	 * 
	 * @param credValue
	 * @return RegisterRequest payload
	 */
	private String getSmsPayload(String credValue) {
		debug.message("getting RegisterRequest payload for SMS");
		return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" "
				+ "xmlns:vip=\"https://schemas.symantec.com/vip/2011/04/vipuserservices\">" + "<soapenv:Header/>"
				+ "<soapenv:Body>" + "<vip:RegisterRequest>" + "<vip:requestId>" + new Random().nextInt(10) + 11111
				+ "</vip:requestId>" + "" + "<vip:smsDeliveryInfo>" + "<vip:phoneNumber>" + credValue
				+ "</vip:phoneNumber> " + "</vip:smsDeliveryInfo> " + "</vip:RegisterRequest>" + "</soapenv:Body>"
				+ "</soapenv:Envelope>";

	}

	/**
	 * 
	 * @param credValue
	 * @return RegisterRequest payload for voice
	 */
	private String getVoicePayload(String credValue) {
		debug.message("getting RegisterRequest payload for voice");
		return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" "
				+ "xmlns:vip=\"https://schemas.symantec.com/vip/2011/04/vipuserservices\">" + "<soapenv:Header/>"
				+ "<soapenv:Body>" + "<vip:RegisterRequest>" + "<vip:requestId>" + new Random().nextInt(10) + 11111
				+ "</vip:requestId>" + "" + "<vip:voiceDeliveryInfo>" + "<vip:phoneNumber>" + credValue
				+ "</vip:phoneNumber> " + "</vip:voiceDeliveryInfo> " + "</vip:RegisterRequest>" + "</soapenv:Body>"
				+ "</soapenv:Envelope>";

	}

	/**
	 * 
	 * @return ManagementServiceURL
	 */
	private String getURL() {
		return GetVIPServiceURL.serviceUrls.get("ManagementServiceURL");
	}

}