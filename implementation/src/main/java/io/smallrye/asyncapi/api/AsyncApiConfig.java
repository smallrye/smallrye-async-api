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

import java.util.Set;

/**
 * Accessor to AsyncAPI configuration options.
 *
 * mp.asyncapi.model.reader : Configuration property to specify the fully qualified name of the AAIModelReader implementation.
 * mp.asyncapi.filter : Configuration property to specify the fully qualified name of the AAIFilter implementation.
 * mp.asyncapi.scan.disable : Configuration property to disable annotation scanning. Default value is false.
 * mp.asyncapi.scan.packages : Configuration property to specify the list of packages to scan.
 * mp.asyncapi.scan.classes : Configuration property to specify the list of classes to scan.
 * mp.asyncapi.scan.exclude.packages : Configuration property to specify the list of packages to exclude from scans.
 * mp.asyncapi.scan.exclude.classes : Configuration property to specify the list of classes to exclude from scans.
 * mp.asyncapi.servers : Configuration property to specify the list of global servers that provide connectivity information.
 *
 * @author eric.wittmann@gmail.com
 */
public interface AsyncApiConfig {

    public String modelReader();

    public String filter();

    public boolean scanDisable();

    public Set<String> scanPackages();

    public Set<String> scanClasses();

    public Set<String> scanExcludePackages();

    public Set<String> scanExcludeClasses();

    public Set<String> servers();

    public boolean scanDependenciesDisable();

    public Set<String> scanDependenciesJars();

    public boolean schemaReferencesEnable();

    public String customSchemaRegistryClass();

}
