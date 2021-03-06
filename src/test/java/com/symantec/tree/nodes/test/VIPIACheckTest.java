package com.symantec.tree.nodes.test;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.forgerock.json.JsonValue.object;
import static org.forgerock.json.test.assertj.AssertJJsonValueAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.HashMap;
import java.util.Map;

import org.forgerock.json.JsonValue;
import org.forgerock.openam.auth.node.api.Action;
import org.forgerock.openam.auth.node.api.ExternalRequestContext;
import org.forgerock.openam.auth.node.api.NodeProcessException;
import org.forgerock.openam.auth.node.api.TreeContext;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.symantec.tree.config.Constants.VIPIA;
import com.symantec.tree.nodes.VIPIAEvaluateRisk;
import com.symantec.tree.request.util.EvaluateRisk;

@Test
public class VIPIACheckTest {
	
	@Mock
	private VIPIAEvaluateRisk.Config config;
	
	@Mock
	private EvaluateRisk evaluateRisk;
	
	@InjectMocks
	VIPIAEvaluateRisk node;
	
	@BeforeMethod
	public void before() {
  		initMocks(this);
	}
	
	@Test
	public void testProcessWithTrueOutcome() throws NodeProcessException {
		TreeContext context = getTreeContext(new HashMap<>());
        HashMap<String, String> map = new HashMap<>();
        
        context.sharedState.put(VIPIA.AUTH_DATA,"==zjghzmngdjrgns");

        
        map.put("EventId","VIP@123");
        map.put("status","0000");
        map.put("DeviceTag","VIP@123");
        map.put("score","100");

		given(evaluateRisk.evaluateRisk(any(),any(),any(),any(),any(),any())).willReturn(map);
				
		// WHEN
		Action action = node.process(context);
		
		//THEN
		assertThat(action.callbacks).isEmpty();
	    assertThat(context.sharedState).isObject().contains(entry(VIPIA.EVENT_ID, "VIP@123"));
	    assertThat(context.sharedState).isObject().contains(entry(VIPIA.DEVICE_TAG, "VIP@123"));

	    assertThat(action.outcome).isEqualTo("TRUE");
	}
	
	@Test
	public void testProcessWithFalseOutcome() throws NodeProcessException {
		TreeContext context = getTreeContext(new HashMap<>());
        HashMap<String, String> map = new HashMap<>();
        
        context.sharedState.put(VIPIA.AUTH_DATA,"==zjghzmngdjrgns");

        
        map.put("EventId","VIP@123");
        map.put("status","6009");
        map.put("DeviceTag","VIP@123");
        map.put("score","100");

		given(evaluateRisk.evaluateRisk(any(),any(),any(),any(),any(),any())).willReturn(map);
				
		// WHEN
		Action action = node.process(context);
		
		//THEN
		assertThat(action.callbacks).isEmpty();
	    assertThat(context.sharedState).isObject().contains(entry(VIPIA.EVENT_ID, "VIP@123"));
	    assertThat(context.sharedState).isObject().contains(entry(VIPIA.DEVICE_TAG, "VIP@123"));

	    assertThat(action.outcome).isEqualTo("FALSE");
	}
	
	
	private TreeContext getTreeContext(Map<String, String[]> parameters) {
		return new TreeContext(JsonValue.json(object(1)),
				new ExternalRequestContext.Builder().parameters(parameters).build(), emptyList());
	}

}
