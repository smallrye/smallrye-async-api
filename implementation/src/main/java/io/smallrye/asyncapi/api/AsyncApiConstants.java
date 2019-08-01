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

package io.smallrye.asyncapi.api;

/**
 * @author eric.wittmann@gmail.com
 */
public final class AsyncApiConstants {

    public static final String ASYNC_API_VERSION = "2.0.0";

    public static final String SCAN_DEPENDENCIES_DISABLE = "mp.asyncapi.extensions.scan-dependencies.disable";
    public static final String SCAN_DEPENDENCIES_JARS = "mp.asyncapi.extensions.scan-dependencies.jars";
    public static final String SCHEMA_REFERENCES_ENABLE = "mp.asyncapi.extensions.schema-references.enable";
    public static final String CUSTOM_SCHEMA_REGISTRY_CLASS = "mp.asyncapi.extensions.custom-schema-registry.class";

    public static final String CLASS_SUFFIX = ".class";
    public static final String JAR_SUFFIX = ".jar";
    public static final String WEB_ARCHIVE_CLASS_PREFIX = "/WEB-INF/classes/";

    public static final String EXTENSION_PROPERTY_PREFIX = "x-";

    private static final String MIME_ANY = "*/*";
    public static final String[] DEFAULT_PARAMETER_MEDIA_TYPES = { MIME_ANY };
    public static final String[] DEFAULT_REQUEST_BODY_TYPES = { MIME_ANY };

    /**
     * Constructor.
     */
    private AsyncApiConstants() {
    }

}
