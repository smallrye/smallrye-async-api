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

public class MergeTest extends AppTestBase {
    @Deployment(name = "merge")
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "merge.war")
                .addPackages(true, "io.smallrye.asyncapi.apps.merge")
                .addAsManifestResource("simpleapi.json", "asyncapi.json")
                .addAsManifestResource("merge.properties", "microprofile-config.properties");
    }

    @RunAsClient
    @Test(dataProvider = "formatProvider")
    public void testVersion(String type) {
        ValidatableResponse vr = callEndpoint(type);
        vr.body("asyncapi", startsWith("2.0."));
    }

    @RunAsClient
    @Test(dataProvider = "formatProvider")
    public void testInfo(String type) {
        ValidatableResponse vr = callEndpoint(type);
        vr.body("info.title", equalTo("AsyncAPI 2.0 App"));
        vr.body("info.version", equalTo("1.0.1"));
        vr.body("info.description", equalTo("This is a very simple AsyncAPI file."));
        vr.body("info.termsOfService", equalTo("https://www.example.org/tos"));
    }

    @RunAsClient
    @Test(dataProvider = "formatProvider")
    public void testContact(String type) {
        ValidatableResponse vr = callEndpoint(type);
        vr.body("info.contact.name", equalTo("Sam Smith"));
        vr.body("info.contact.url", equalTo("https://www.example.com/users/ssmith"));
        vr.body("info.contact.email", equalTo("ssmith@example.com"));
    }

    @RunAsClient
    @Test(dataProvider = "formatProvider")
    public void testLicense(String type) {
        ValidatableResponse vr = callEndpoint(type);
        vr.body("info.license.name", equalTo("GNU AGPLv3"));
        vr.body("info.license.url", equalTo("https://www.gnu.org/licenses/agpl.txt"));
    }
}
