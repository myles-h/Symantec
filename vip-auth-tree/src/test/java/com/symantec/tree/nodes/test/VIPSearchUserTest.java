package com.symantec.tree.nodes.test;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.forgerock.json.JsonValue.object;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.MockitoAnnotations.initMocks;
import java.util.HashMap;
import java.util.Map;

import org.forgerock.json.JsonValue;
import org.forgerock.openam.auth.node.api.Action;
import org.forgerock.openam.auth.node.api.ExternalRequestContext;
import org.forgerock.openam.auth.node.api.SharedStateConstants;
import org.forgerock.openam.auth.node.api.TreeContext;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.symantec.tree.nodes.VIPSearchUser;
import com.symantec.tree.request.util.VIPGetUser;

/**
 * 
 * @author Symantec
 * @category test test class for "VIPSearchUser"
 *
 */
@Test
public class VIPSearchUserTest {

	@Mock
	private VIPGetUser vipSearchUser;

	@BeforeMethod
	public void before() {

		initMocks(this);

	}

	@Test
	public void nodeProcessWithTrueOutcome() {
		given(vipSearchUser.viewUserInfo(anyString())).willReturn(true);
		given(vipSearchUser.getMobInfo(anyString())).willReturn("91565656543");

		TreeContext context = getTreeContext(new HashMap<>());

		context.sharedState.put(SharedStateConstants.USERNAME, "ruchika");

		//WHEN
		VIPSearchUser node = new VIPSearchUser();
		Action action = node.process(context);

		// THEN
		assertThat(action.callbacks).isEmpty();
		assertThat(action.outcome).isEqualTo("true");

	}
	

	private TreeContext getTreeContext(Map<String, String[]> parameters) {
		return new TreeContext(JsonValue.json(object(1)),
				new ExternalRequestContext.Builder().parameters(parameters).build(), emptyList());
	}
}
