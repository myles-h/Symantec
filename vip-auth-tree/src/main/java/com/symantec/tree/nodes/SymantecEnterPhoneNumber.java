/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * 
 *
 * Copyright 2018 Sacumen.
 */


package com.symantec.tree.nodes;

import static org.forgerock.openam.auth.node.api.Action.send;
import static org.forgerock.openam.auth.node.api.SharedStateConstants.ONE_TIME_PASSWORD;

import java.util.ResourceBundle;

import javax.inject.Inject;
import javax.security.auth.callback.NameCallback;
//import javax.security.auth.callback.PasswordCallback;


import org.forgerock.guava.common.base.Strings;
import org.forgerock.json.JsonValue;
import org.forgerock.openam.auth.node.api.Action;
import org.forgerock.openam.auth.node.api.Node;
import org.forgerock.openam.auth.node.api.SingleOutcomeNode;
import org.forgerock.openam.auth.node.api.TreeContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.symantec.tree.request.util.SMSVoiceRegister;

import static com.symantec.tree.config.Constants.CREDID;
import static com.symantec.tree.config.Constants.MOBNUM;
import static com.symantec.tree.config.Constants.CREDCHOICE;
/**
 * A node which collects Phone number from the user via a Name callback.
 *
 */
@Node.Metadata(outcomeProvider  = SingleOutcomeNode.OutcomeProvider.class,
               configClass      = SymantecEnterPhoneNumber.Config.class)
public class SymantecEnterPhoneNumber extends SingleOutcomeNode {

    private static final String BUNDLE = "com/symantec/tree/nodes/SymantecEnterPhoneNumber";
    private final Logger logger = LoggerFactory.getLogger("symantec");
    private SMSVoiceRegister svRegister;
    /**
     * Configuration for the node.
     */
    public interface Config {}


    /**
     * Create the node.
     */
    @Inject
    public SymantecEnterPhoneNumber() {
    	svRegister= new SMSVoiceRegister();

    }
    
	private Action collectOTP(TreeContext context) {
		ResourceBundle bundle = context.request.locales.getBundleInPreferredLocale(BUNDLE, getClass().getClassLoader());
		//return send(new PasswordCallback(bundle.getString("callback.phoneNumber"), true)).build();
		//TextInputCallback textIPCB =new TextInputCallback(bundle.getString("callback.phoneNumber"),"Enter PhoneNumber");
		NameCallback nicb =new NameCallback(bundle.getString("callback.phoneNumber"),"Enter PhoneNumber");
		
		return send(nicb).build();
	}

	/**
	 * 	This Node method displays Text block to enter PhoneNumber.
	 */
    @Override
    public Action process(TreeContext context) {
    	logger.debug("Collect PhoneNumber started");
        JsonValue sharedState = context.sharedState;
        
               return context.getCallback(NameCallback.class)
                .map(NameCallback::getName)
                .map(String::new)
                .filter(name -> !Strings.isNullOrEmpty(name))
                .map(name -> {
                	logger.debug("CredID has been collected and placed  into the Shared State");
                	String credType= context.sharedState.get(CREDCHOICE).asString();
                	if(credType.equalsIgnoreCase("SMS") ) {
                	
                	svRegister.smsRegister(name);
                	return goToNext()
                	.replaceSharedState(sharedState.copy().put(MOBNUM, name)).build();

                	}else if(credType.equalsIgnoreCase("VOICE")){
                	
                	svRegister.voiceRegister(name);
                	return goToNext()
                	.replaceSharedState(sharedState.copy().put(MOBNUM, name)).build();

                	}else
                	return goToNext()
                	.replaceSharedState(sharedState.copy().put(CREDID, name)).build();
                	
                })
      
                .orElseGet(() -> {
                	logger.debug("Enter Credential ID");
                    return collectOTP(context);
                });
    }
}