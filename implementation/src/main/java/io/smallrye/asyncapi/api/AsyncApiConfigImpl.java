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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.microprofile.config.Config;

import io.smallrye.asyncapi.spec.AAIConfig;

/**
 * Implementation of the {@link AsyncApiConfig} interface that gets config information from a
 * standard MP Config object.
 *
 * @author eric.wittmann@gmail.com
 */
public class AsyncApiConfigImpl implements AsyncApiConfig {

    private Config config;

    private String modelReader;
    private String filter;
    private Boolean scanDisable;
    private Set<String> scanPackages;
    private Set<String> scanClasses;
    private Set<String> scanExcludePackages;
    private Set<String> scanExcludeClasses;
    private Set<String> servers;
    private Boolean scanDependenciesDisable;
    private Set<String> scanDependenciesJars;
    private Boolean schemaReferencesEnable;
    private String customSchemaRegistryClass;

    /**
     * Constructor.
     *
     * @param config MicroProfile Config instance
     */
    public AsyncApiConfigImpl(Config config) {
        this.config = config;
    }

    /**
     * @return the MP config instance
     */
    protected Config getConfig() {
        // We cannot use ConfigProvider.getConfig() as the archive is not deployed yet - TCCL cannot be set
        return config;
    }

    /**
     * @see io.smallrye.asyncapi.api.AsyncApiConfig#modelReader()
     */
    @Override
    public String modelReader() {
        if (modelReader == null) {
            modelReader = getConfig().getOptionalValue(AAIConfig.MODEL_READER, String.class).orElse(null);
        }
        return modelReader;
    }

    /**
     * @see io.smallrye.asyncapi.api.AsyncApiConfig#filter()
     */
    @Override
    public String filter() {
        if (filter == null) {
            filter = getConfig().getOptionalValue(AAIConfig.FILTER, String.class).orElse(null);
        }
        return filter;
    }

    /**
     * @see io.smallrye.asyncapi.api.AsyncApiConfig#scanDisable()
     */
    @Override
    public boolean scanDisable() {
        if (scanDisable == null) {
            scanDisable = getConfig().getOptionalValue(AAIConfig.SCAN_DISABLE, Boolean.class).orElse(false);
        }
        return scanDisable;
    }

    /**
     * @see io.smallrye.asyncapi.api.AsyncApiConfig#scanPackages()
     */
    @Override
    public Set<String> scanPackages() {
        if (scanPackages == null) {
            String packages = getConfig().getOptionalValue(AAIConfig.SCAN_PACKAGES, String.class).orElse(null);
            scanPackages = asCsvSet(packages);
        }
        return scanPackages;
    }

    /**
     * @see io.smallrye.asyncapi.api.AsyncApiConfig#scanClasses()
     */
    @Override
    public Set<String> scanClasses() {
        if (scanClasses == null) {
            String classes = getConfig().getOptionalValue(AAIConfig.SCAN_CLASSES, String.class).orElse(null);
            scanClasses = asCsvSet(classes);
        }
        return scanClasses;
    }

    /**
     * @see io.smallrye.asyncapi.api.AsyncApiConfig#scanExcludePackages()
     */
    @Override
    public Set<String> scanExcludePackages() {
        if (scanExcludePackages == null) {
            String packages = getConfig().getOptionalValue(AAIConfig.SCAN_EXCLUDE_PACKAGES, String.class).orElse(null);
            scanExcludePackages = asCsvSet(packages);
        }
        return scanExcludePackages;
    }

    /**
     * @see io.smallrye.asyncapi.api.AsyncApiConfig#scanExcludeClasses()
     */
    @Override
    public Set<String> scanExcludeClasses() {
        if (scanExcludeClasses == null) {
            String classes = getConfig().getOptionalValue(AAIConfig.SCAN_EXCLUDE_CLASSES, String.class).orElse(null);
            scanExcludeClasses = asCsvSet(classes);
        }
        return scanExcludeClasses;
    }

    /**
     * @see io.smallrye.asyncapi.api.AsyncApiConfig#servers()
     */
    @Override
    public Set<String> servers() {
        if (servers == null) {
            String theServers = getConfig().getOptionalValue(AAIConfig.SERVERS, String.class).orElse(null);
            servers = asCsvSet(theServers);
        }
        return servers;
    }

    /**
     * @see io.smallrye.asyncapi.api.AsyncApiConfig#scanDependenciesDisable()
     */
    @Override
    public boolean scanDependenciesDisable() {
        if (scanDependenciesDisable == null) {
            scanDependenciesDisable = getConfig().getOptionalValue(AsyncApiConstants.SCAN_DEPENDENCIES_DISABLE, Boolean.class)
                    .orElse(false);
        }
        return scanDependenciesDisable;
    }

    /**
     * @see io.smallrye.asyncapi.api.AsyncApiConfig#scanDependenciesJars()
     */
    @Override
    public Set<String> scanDependenciesJars() {
        if (scanDependenciesJars == null) {
            String classes = getConfig().getOptionalValue(AsyncApiConstants.SCAN_DEPENDENCIES_JARS, String.class).orElse(null);
            scanDependenciesJars = asCsvSet(classes);
        }
        return scanDependenciesJars;
    }

    @Override
    public boolean schemaReferencesEnable() {
        if (schemaReferencesEnable == null) {
            schemaReferencesEnable = getConfig().getOptionalValue(AsyncApiConstants.SCHEMA_REFERENCES_ENABLE, Boolean.class)
                    .orElse(false);
        }
        return schemaReferencesEnable;
    }

    @Override
    public String customSchemaRegistryClass() {
        if (customSchemaRegistryClass == null) {
            customSchemaRegistryClass = getConfig()
                    .getOptionalValue(AsyncApiConstants.CUSTOM_SCHEMA_REGISTRY_CLASS, String.class).orElse(null);
        }
        return customSchemaRegistryClass;
    }

    private static Set<String> asCsvSet(String items) {
        Set<String> rval = new HashSet<>();
        if (items != null) {
            String[] split = items.split(",");
            for (String item : split) {
                rval.add(item.trim());
            }
        }
        return rval;
    }

}
