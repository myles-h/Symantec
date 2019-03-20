package com.symantec.tree.nodes;

import com.google.inject.assistedinject.Assisted;
import com.sun.identity.shared.debug.Debug;
import com.symantec.tree.config.Constants.VIPIA;
import com.symantec.tree.request.util.EvaluateRisk;
import com.google.common.collect.ImmutableList;
import org.forgerock.json.JsonValue;
import org.forgerock.openam.auth.node.api.*;
import org.forgerock.openam.auth.node.api.Action.ActionBuilder;
import org.forgerock.util.i18n.PreferredLocales;
import static com.symantec.tree.config.Constants.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import javax.inject.Inject;

/**
 * 
 * @author Sacumen (www.sacumen.com) <br>
 *         <br>
 * @category Node
 *
 */
@Node.Metadata(outcomeProvider = VIPIAAuthentication.SymantecOutcomeProvider.class, configClass = VIPIAAuthentication.Config.class)
public class VIPIAAuthentication implements Node {

	private EvaluateRisk evaluateRisk;
	private static final String BUNDLE = "com/symantec/tree/nodes/VIPIAAuthentication";
	private final Debug debug = Debug.getInstance("VIP");

	/**
	 * Configuration for the node.
	 */
	public interface Config {
	}

	/**
	 * 
	 */
	@Inject
	public VIPIAAuthentication(EvaluateRisk evaluateRisk) {
		this.evaluateRisk = evaluateRisk;
	}

	/**
	 * The possible outcomes for the DisplayCredentail.
	 */
	private enum Symantec {
		/**
		 * Successful.
		 */
		TRUE,
		/**
		 * failed.
		 */
		FALSE,
		/**
		 * Disabled.
		 */
		ERROR

	}

	private ActionBuilder goTo(Symantec outcome) {
		return Action.goTo(outcome.name());
	}

	/**
	 * Defines the possible outcomes from this SymantecOutcomeProvider node.
	 */
	public static class SymantecOutcomeProvider implements OutcomeProvider {
		@Override
		public List<Outcome> getOutcomes(PreferredLocales locales, JsonValue nodeAttributes) {
			ResourceBundle bundle = locales.getBundleInPreferredLocale(VIPIAAuthentication.BUNDLE,
					SymantecOutcomeProvider.class.getClassLoader());
			return ImmutableList.of(new Outcome(Symantec.TRUE.name(), bundle.getString("trueOutcome")),
					new Outcome(Symantec.FALSE.name(), bundle.getString("falseOutcome")),
					new Outcome(Symantec.ERROR.name(), bundle.getString("errorOutcome")));
		}
	}

	/**
	 * Main logic of the node.
	 * 
	 * @throws NodeProcessException
	 */
	@Override
	public Action process(TreeContext context) throws NodeProcessException {
		JsonValue transientState = context.sharedState;
		JsonValue sharedState = context.sharedState;

		debug.message("Authentication IA Data.....");
		InetAddress localhost = null;

		//TODO Duplicate code

		//TODO This is the IP Address the AM is running on, not the IP Address that the client is connecting from
		// use context.request.clientIP to get this address
		try {
			localhost = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			new NodeProcessException(e.getLocalizedMessage());
		}
		String ip = localhost.getHostAddress().trim();
		String userAgent = VIPIA.TEST_AGENT;

		debug.message("Auth data in AI Authentication is " + context.sharedState.get(VIPIA.AUTH_DATA).asString());
		HashMap<String, String> evaluateRiskResponseAttribute = evaluateRisk.evaluateRisk(sharedState.get(SharedStateConstants.USERNAME).asString(),
				ip, context.sharedState.get(VIPIA.AUTH_DATA).asString(), userAgent,
				sharedState.get(KEY_STORE_PATH).asString(), sharedState.get(KEY_STORE_PASS).asString());

		debug.message("status in IA authentication is " + evaluateRiskResponseAttribute.get("status"));
		debug.message("score in IA authentication is " + evaluateRiskResponseAttribute.get("score"));


		String status = evaluateRiskResponseAttribute.get("status");
		
		if (status.equals(VIPIA.REGISTERED)) {
			transientState.put(VIPIA.SCORE,evaluateRiskResponseAttribute.get(VIPIA.SCORE));
			return goTo(Symantec.TRUE).replaceTransientState(transientState).build();
		}
		else if(status.equals(VIPIA.NOT_REGISTERED)){
			return goTo(Symantec.FALSE).build();
		}
		else {
			sharedState.put(DISPLAY_ERROR,"Getting some error while executing Evaluate Risk request, Please contact to Administrator");
			return goTo(Symantec.ERROR).build();
		}
	}

}