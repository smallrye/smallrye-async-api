/*
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

import io.apicurio.datamodels.asyncapi.v2.models.Aai20Document;
import io.apicurio.datamodels.core.models.Document;
import io.smallrye.asyncapi.api.util.FilterUtil;
import io.smallrye.asyncapi.api.util.MergeUtil;
import io.smallrye.asyncapi.api.util.ServersUtil;
import io.smallrye.asyncapi.spec.AAIFilter;

/**
 * Holds the final AsyncAPI document produced during the startup of the app.
 *
 * <p>
 * Note that the model must be initialized first!
 * </p>
 *
 * @author Martin Kouba
 * @author Eric Wittmann
 */
public class AsyncApiDocument {

    public static final AsyncApiDocument INSTANCE = new AsyncApiDocument();

    // These are used during init only
    private transient AsyncApiConfig config;
    private transient Document readerModel;
    private transient Document staticFileModel;
    private transient Document annotationsModel;
    private transient AAIFilter filter;
    private transient String archiveName;

    private transient Document model;

    private AsyncApiDocument() {
    }

    /**
     *
     * @return the final AsyncAPI document produced during the startup of the app
     * @throws IllegalStateException If the final model is not initialized yet
     */
    public Document get() {
        synchronized (INSTANCE) {
            if (model == null) {
                throw new IllegalStateException("Model not initialized yet");
            }
            return model;
        }
    }

    /**
     * Set the final AsyncAPI document. This method should only be used for testing.
     *
     * @param model AsyncAPI model instance
     */
    public void set(Document model) {
        synchronized (INSTANCE) {
            this.model = model;
        }
    }

    /**
     * Reset the holder.
     */
    public void reset() {
        synchronized (INSTANCE) {
            model = null;
            clear();
        }
    }

    /**
     * @return {@code true} if model initialized
     */
    public boolean isSet() {
        synchronized (INSTANCE) {
            return model != null;
        }
    }

    public synchronized void config(AsyncApiConfig config) {
        set(() -> this.config = config);
    }

    public void modelFromAnnotations(Document model) {
        set(() -> this.annotationsModel = model);
    }

    public void modelFromReader(Document model) {
        set(() -> this.readerModel = model);
    }

    public void modelFromStaticFile(Document model) {
        set(() -> this.staticFileModel = model);
    }

    public void filter(AAIFilter filter) {
        set(() -> this.filter = filter);
    }

    public void archiveName(String archiveName) {
        set(() -> this.archiveName = archiveName);
    }

    public void initialize() {
        synchronized (INSTANCE) {
            if (model != null) {
                modelAlreadyInitialized();
            }
            // Check all the required parts are set
            if (config == null) {
                throw new IllegalStateException("AsyncApiConfig must be set before init");
            }

            // Phase 1: Use AAIModelReader
            Document merged = readerModel;

            // Phase 2: Merge any static AsyncAPI file packaged in the app
            merged = MergeUtil.mergeObjects(merged, staticFileModel);

            // Phase 3: Merge annotations
            merged = MergeUtil.mergeObjects(merged, annotationsModel);

            // Phase 4: Filter model via AAIFilter
            merged = filterModel(merged);

            // Phase 5: Default empty document if model == null
            if (merged == null) {
                merged = new Aai20Document();
                ((Aai20Document) merged).asyncapi = AsyncApiConstants.ASYNC_API_VERSION;
            }

            // Phase 6: Provide missing required elements
            Aai20Document mergedAai = (Aai20Document) merged;
            if (mergedAai.info == null) {
                mergedAai.info = mergedAai.createInfo();
            }
            if (mergedAai.info.title == null) {
                mergedAai.info.title = (archiveName == null ? "Generated" : archiveName) + " API";
            }
            if (mergedAai.info.version == null) {
                mergedAai.info.version = "1.0";
            }

            // Phase 7: Use Config values to add Servers (global, pathItem, operation)
            ServersUtil.configureServers(config, merged);

            model = merged;
            clear();
        }
    }

    /**
     * Filter the final model using a {@link AAIFilter} configured by the app. If no filter has been configured, this will
     * simply return the model unchanged.
     *
     * @param model
     */
    private Document filterModel(Document model) {
        if (model == null || filter == null) {
            return model;
        }
        return FilterUtil.applyFilter(filter, model);
    }

    private void set(Runnable action) {
        synchronized (INSTANCE) {
            if (model != null) {
                modelAlreadyInitialized();
            }
            action.run();
        }
    }

    private void modelAlreadyInitialized() {
        throw new IllegalStateException("Model already initialized");
    }

    private void clear() {
        config = null;
        annotationsModel = null;
        readerModel = null;
        staticFileModel = null;
        filter = null;
        archiveName = null;
    }

}
