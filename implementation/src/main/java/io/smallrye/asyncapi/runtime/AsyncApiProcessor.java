/*
 * Copyright 2019 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.smallrye.asyncapi.runtime;

import org.jboss.jandex.IndexView;

import io.apicurio.datamodels.Library;
import io.apicurio.datamodels.core.models.Document;
import io.smallrye.asyncapi.api.AsyncApiConfig;
import io.smallrye.asyncapi.api.util.IOUtil;
import io.smallrye.asyncapi.spec.AAIFilter;
import io.smallrye.asyncapi.spec.AAIModelReader;

/**
 * Provides some core archive processing functionality.
 *
 * @author eric.wittmann@gmail.com
 */
public class AsyncApiProcessor {

    /**
     * Parse the static file content and return the resulting model. Note that this
     * method does NOT close the resources in the static file. The caller is
     * responsible for that.
     *
     * @param staticFile AsyncApiStaticFile to be parsed
     * @return Document
     */
    public static Document modelFromStaticFile(AsyncApiStaticFile staticFile) {
        if (staticFile == null) {
            return null;
        }
        String jsonContent = IOUtil.toString(staticFile.getContent());
        if (staticFile.getFormat() == AsyncApiFormat.YAML) {
            // TODO support YAML for static file reading
            throw new RuntimeException("YAML not yet supported");
        }
        return Library.readDocumentFromJSONString(jsonContent);
    }

    /**
     * Create an {@link Document} model by scanning the deployment for relevant annotations. If scanning is
     * disabled, this method returns null. If scanning is enabled but no relevant annotations are found, an
     * empty AsyncAPI model is returned.
     *
     * @param config AsyncApiConfig
     * @param index IndexView of Archive
     * @return Document generated from annotations
     */
    public static Document modelFromAnnotations(AsyncApiConfig config, IndexView index) {
        if (config.scanDisable()) {
            return null;
        }

        // TODO implement annotation scanning!
        //AsyncApiAnnotationScanner scanner = new AsyncApiAnnotationScanner(config, index);
        //return scanner.scan();
        return null;
    }

    /**
     * Instantiate the configured {@link AAIModelReader} and invoke it. If no reader is configured,
     * then return null. If a class is configured but there is an error either instantiating or invoking
     * it, a {@link RuntimeException} is thrown.
     *
     * @param config AsyncApiConfig
     * @param loader ClassLoader
     * @return Document created from AAIModelReader
     */
    public static Document modelFromReader(AsyncApiConfig config, ClassLoader loader) {
        String readerClassName = config.modelReader();
        if (readerClassName == null) {
            return null;
        }
        try {
            Class<?> c = loader.loadClass(readerClassName);
            AAIModelReader reader = (AAIModelReader) c.newInstance();
            return (Document) reader.buildModel();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Instantiate the {@link AAIFilter} configured by the app.
     *
     * @param config AsyncApiConfig
     * @param loader ClassLoader
     * @return AAIFilter instance retrieved from loader
     */
    public static AAIFilter getFilter(AsyncApiConfig config, ClassLoader loader) {
        String filterClassName = config.filter();
        if (filterClassName == null) {
            return null;
        }
        try {
            Class<?> c = loader.loadClass(filterClassName);
            return (AAIFilter) c.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
