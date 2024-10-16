/*
 * Copyright 2019 Red Hat
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.smallrye.asyncapi.spec;

/**
 * Configurable properties in MicroProfile AsyncAPI
 *
 * @author eric.wittmann@gmail.com
 */
public final class AAIConfig {

    private AAIConfig() {
    }

    public static final String MODEL_READER = "mp.asyncapi.model.reader";
    public static final String FILTER = "mp.asyncapi.filter";
    public static final String SCAN_DISABLE = "mp.asyncapi.scan.disable";
    public static final String SCAN_PACKAGES = "mp.asyncapi.scan.packages";
    public static final String SCAN_CLASSES = "mp.asyncapi.scan.classes";
    public static final String SCAN_EXCLUDE_PACKAGES = "mp.asyncapi.scan.exclude.packages";
    public static final String SCAN_EXCLUDE_CLASSES = "mp.asyncapi.scan.exclude.classes";
    public static final String SERVERS = "mp.asyncapi.servers";
    public static final String EXTENSIONS_PREFIX = "mp.asyncapi.extensions.";

}
