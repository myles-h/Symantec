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
 * @author Sacumen(www.sacumen.com) <br> <br>
 * @Description: Checking OTP with CheckOtpRequest.
 *
 */
public class CheckVIPOtp {

	private final Debug debug = Debug.getInstance("VIP");

	/**
	 * 
	 * @param userName
	 * @param otpValue
	 * @return status code of response
	 * @throws NodeProcessException
	 */
	public String checkOtp(String userName, String otpValue,String key_store,String key_store_pass) throws NodeProcessException {

		HttpClientUtil clientUtil = HttpClientUtil.getInstance();
		HttpPost post = new HttpPost(getURL());
		post.setHeader("CONTENT-TYPE", "text/xml; charset=ISO-8859-1");
		//TODO Duplicate Code
		String payLoad = getViewUserPayload(userName, otpValue);
		String status;
		debug.message("Request Payload: " + payLoad);
		try {
			HttpClient httpClient = clientUtil.getHttpClientForgerock(key_store,key_store_pass);
			post.setEntity(new StringEntity(payLoad));
			HttpResponse response = httpClient.execute(post);
			HttpEntity entity = response.getEntity();
			String body = IOUtils.toString(entity.getContent());
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource src = new InputSource();
			src.setCharacterStream(new StringReader(body));
			Document doc = builder.parse(src);
<<<<<<< HEAD
			status = doc.getElementsByTagName("status").item(0).getTextContent();		
=======
			status = doc.getElementsByTagName("status").item(0).getTextContent();
			//TODO status message never used
			String statusMessage = doc.getElementsByTagName("statusMessage").item(0).getTextContent();
		
>>>>>>> remotes/origin/no_sdk_frank_changes
		} catch (IOException | ParserConfigurationException | SAXException e) {
			debug.error("Not able to process Request");
			throw new NodeProcessException(e);
		}
		return status;

	}

	/**
	 * 
	 * @param userName
	 * @param otpValue
	 * @return CheckOtpRequest payload
	 */
<<<<<<< HEAD
	private String getViewUserPayload(String userName, String otpValue) {
		debug.message("getting CheckOtpRequest payload");
=======
	private static String getViewUserPayload(String userName, String otpValue) {
		//TODO Duplicate Code
		logger.info("getting CheckOtpRequest payload");
>>>>>>> remotes/origin/no_sdk_frank_changes
		return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" "
				+ "xmlns:vip=\"https://schemas.symantec.com/vip/2011/04/vipuserservices\">" + "   <soapenv:Header/>"
				+ "   <soapenv:Body>" + "      <vip:CheckOtpRequest>" + "<vip:requestId>" + new Random().nextInt(10)
				+ 11111 + "</vip:requestId>" + "<vip:userId>" + userName + "</vip:userId>"
				+ "         <vip:otpAuthData>" + "            <vip:otp>" + otpValue + "</vip:otp>           "
				+ "         </vip:otpAuthData>        " + "      </vip:CheckOtpRequest>" + "   </soapenv:Body>"
				+ "</soapenv:Envelope>";
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
