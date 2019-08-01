/**
 * Copyright 2019 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.smallrye.asyncapi.tck;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.testng.annotations.Test;

import io.restassured.response.ValidatableResponse;

/**
 * This test covers Async API documents that are specified by the
 * META-INF/asyncapi.json file. It verifies that the /asyncapi
 * endpoint returns the correct content for these static files.
 */
public class StaticDocumentTest extends AppTestBase {

    @Deployment(name = "static")
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "static.war")
                .addAsManifestResource("simpleapi.json", "asyncapi.json");
    }

    @RunAsClient
    @Test(dataProvider = "formatProvider")
    public void testStaticDocument(String type) {
        ValidatableResponse vr = callEndpoint(type);

        vr.body("asyncapi", startsWith("2.0."));

        vr.body("info.description", equalTo("This is a very simple AsyncAPI file."));
        vr.body("info.version", equalTo("1.0.1"));
        vr.body("info.title", equalTo("AsyncAPI 2.0 App"));
    }
}
