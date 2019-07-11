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

package io.smallrye.asyncapi.runtime.scanner;

import org.jboss.jandex.IndexView;
import org.jboss.logging.Logger;

import io.apicurio.datamodels.core.models.Document;
import io.smallrye.asyncapi.api.AsyncApiConfig;

/**
 * Scans a deployment (using the archive and jandex annotation index) for relevant annotations. These 
 * annotations, if found, are used to generate a valid AsyncAPI model.
 * 
 * @author eric.wittmann@gmail.com
 */
public class AsyncApiAnnotationScanner {

    private static Logger LOG = Logger.getLogger(AsyncApiAnnotationScanner.class);

    private final AsyncApiConfig config;
    private final IndexView index;

    private Document document;

    /**
     * Constructor.
     * 
     * @param config AsyncApiConfig instance
     * @param index IndexView of deployment
     */
    public AsyncApiAnnotationScanner(AsyncApiConfig config, IndexView index) {
        this.config = config;
        this.index = index;
    }

    /**
     * Scan the deployment for relevant annotations. Returns an AsyncAPI data model that was
     * built from those found annotations.
     * 
     * @return Document generated from scanning annotations
     */
    public Document scan() {
        LOG.debug("Scanning deployment for Async Annotations.");
        // TODO implement this method
        return document;
    }
}
