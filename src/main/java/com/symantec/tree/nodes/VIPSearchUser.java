package com.symantec.tree.nodes;

import static com.symantec.tree.config.Constants.DISPLAY_ERROR;
import static com.symantec.tree.config.Constants.MOB_NUM;
import static com.symantec.tree.config.Constants.NO_CREDENTIALS_REGISTERED;
import static com.symantec.tree.config.Constants.NO_CRED_REGISTERED;
import static com.symantec.tree.config.Constants.SUCCESS_CODE;
import static com.symantec.tree.config.Constants.USER_DOES_NOT_EXIST;
import static com.symantec.tree.config.Constants.VIP_CRED_REGISTERED;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import com.symantec.tree.request.util.GetVIPServiceURL;
import com.symantec.tree.request.util.VIPGetUser;
import javax.inject.Inject;
import com.google.common.collect.ImmutableList;
import org.forgerock.json.JsonValue;
import org.forgerock.openam.auth.node.api.Action;
import org.forgerock.openam.auth.node.api.Action.ActionBuilder;
import org.forgerock.openam.auth.node.api.Node;
import org.forgerock.openam.auth.node.api.NodeProcessException;
import org.forgerock.openam.auth.node.api.OutcomeProvider;
import org.forgerock.openam.auth.node.api.TreeContext;
import org.forgerock.util.i18n.PreferredLocales;

/**
 * 
 * @author Sacumen(www.sacumen.com) <br> <br>
 * @category Node
 * @Descrition "VIP Search User" node with TRUE,FALSE and ERROR outcome. If TRUE, it will go to "VIP Push Auth User". If False, go to
 *             "VIP Register User" and if ERROR, It will go to "VIP Display Error" Page.
 *
 */
@Node.Metadata(outcomeProvider = VIPSearchUser.SymantecOutcomeProvider.class, configClass = VIPSearchUser.Config.class)
public class VIPSearchUser implements Node {
    
	private Logger logger = LoggerFactory.getLogger(VIPSearchUser.class);
	private static final String BUNDLE = "com/symantec/tree/nodes/VIPSearchUser";

	/**
	 * Configuration for the node.
	 */
	 public interface Config {

	}
	 
	private VIPGetUser vipSearchUser;

	/**
	 * Create the node.
	 *
	 */
	@Inject
	public VIPSearchUser(VIPGetUser vipSearchUser) {
		this.vipSearchUser = vipSearchUser;
	}

	/**
	 * Main logic of the node.
	 * @throws NodeProcessException 
	 */
	@Override
	public Action process(TreeContext context) throws NodeProcessException {
		logger.info("VIP Search User");
		GetVIPServiceURL vip = GetVIPServiceURL.getInstance();

		String statusCode = vipSearchUser.viewUserInfo(vip.getUserName(),vip.getKeyStorePath(),vip.getKeyStorePasswod());
        logger.debug("status code in VIP Search User"+statusCode);
		String mobNum;

			if (statusCode.equalsIgnoreCase(SUCCESS_CODE)) {
				mobNum = vipSearchUser.getMobInfo(vip.getUserName(),vip.getKeyStorePath(),vip.getKeyStorePasswod());
				logger.debug("Phone Number in VIP Search User" + mobNum);

				if (mobNum != null && mobNum.equalsIgnoreCase(NO_CRED_REGISTERED)) {
					logger.debug("No Credential Registered in VIP Search User");
					context.transientState.put(NO_CREDENTIALS_REGISTERED, true);
					return goTo(Symantec.FALSE).build();
				} else if (mobNum != null && mobNum.equalsIgnoreCase(VIP_CRED_REGISTERED)) {
					logger.debug("VIP Credential Registered in VIP Search User");
					return goTo(Symantec.TRUE).build();
				} else {
					logger.info("Fall back options in VIP Search User");

					context.sharedState.put(MOB_NUM, mobNum);
					return goTo(Symantec.TRUE).build();
				}
			} else if(statusCode.equalsIgnoreCase(USER_DOES_NOT_EXIST)) {
				return goTo(Symantec.FALSE).build();
			}else {
				context.sharedState.put(DISPLAY_ERROR,"User is locked, Please contact to administrator");
				return goTo(Symantec.ERROR).build();
			}
	}
	
	private ActionBuilder goTo(Symantec outcome) {
		return Action.goTo(outcome.name());
	}
	
	/**
	 * The possible outcomes for the SymantecVerifyAuth.
	 */
	public enum Symantec {
		/**
		 * Successful.
		 */
		TRUE,
		/**
		 * failed.
		 */
		FALSE,
		/**
		 * Locked.
		 */
		ERROR

	}
	
	/**
	 * Defines the possible outcomes from this SymantecOutcomeProvider node.
	 */
	public static class SymantecOutcomeProvider implements OutcomeProvider {
		@Override
		public List<Outcome> getOutcomes(PreferredLocales locales, JsonValue nodeAttributes) {
			ResourceBundle bundle = locales.getBundleInPreferredLocale(VIPSearchUser.BUNDLE,
					SymantecOutcomeProvider.class.getClassLoader());
			return ImmutableList.of(new Outcome(Symantec.TRUE.name(), bundle.getString("trueOutcome")),
					new Outcome(Symantec.FALSE.name(), bundle.getString("falseOutcome")),
					new Outcome(Symantec.ERROR.name(), bundle.getString("errorOutcome")));
		}
	}

}