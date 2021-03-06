/*******************************************************************************
 * Copyright (c) 2015 Pivotal Software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * and the Apache License v2.0 is available at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * You may elect to redistribute this code under either of these licenses.
 *
 *  Contributors:
 *     Pivotal Software, Inc. - initial API and implementation
 ********************************************************************************/
package org.eclipse.cft.server.tests.core;

import org.eclipse.cft.server.core.internal.CloudServerEvent;
import org.eclipse.cft.server.core.internal.client.CloudFoundryApplicationModule;
import org.eclipse.cft.server.tests.util.ModulesRefreshListener;

public abstract class AbstractRefreshCloudTest extends AbstractAsynchCloudTest {

	/**
	 * For refresh tests that do NOT explicitly test app deploy changes, make
	 * sure that the deployment event does not interferer with refresh jobs
	 * launched by the actual test cases. Since only one refresh job is active
	 * per behaviour, and any schedule requests for refresh are ignored if one
	 * is already active, handling the deploy change event will ensure that
	 * other refresh jobs launched by the test case are not ignored
	 */
	protected CloudFoundryApplicationModule deployAndWaitForDeploymentEvent(String appPrefix) throws Exception {
		return deployAndWaitForDeploymentEvent(appPrefix, false);
	}

	/**
	 * For refresh tests that do NOT explicitly test app deploy changes, make
	 * sure that the deployment event does not interferer with refresh jobs
	 * launched by the actual test cases. Since only one refresh job is active
	 * per behaviour, and any schedule requests for refresh are ignored if one
	 * is already active, handling the deploy change event will ensure that
	 * other refresh jobs launched by the test case are not ignored. Application
	 * is deployed in stop mode.
	 */
	protected CloudFoundryApplicationModule deployAndWaitForDeploymentEvent(String appPrefix, boolean deployedStopped)
			throws Exception {

		String appName = harness.getDefaultWebAppName(appPrefix);
		ModulesRefreshListener listener = ModulesRefreshListener.getListener(appName, cloudServer,
				CloudServerEvent.EVENT_APP_DEPLOYMENT_CHANGED);
		CloudFoundryApplicationModule appModule = deployApplication(appPrefix, deployedStopped);

		if (!deployedStopped) {
			waitForApplicationToStart(appModule.getLocalModule(), appPrefix);
		}

		assertModuleRefreshedAndDispose(listener, CloudServerEvent.EVENT_APP_DEPLOYMENT_CHANGED);
		return appModule;
	}
}
