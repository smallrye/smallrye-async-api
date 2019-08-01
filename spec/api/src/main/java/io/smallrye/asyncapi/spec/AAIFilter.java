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

import io.apicurio.datamodels.core.models.Document;

/**
 * This interface allows the application developer to filter the AsyncAPI model tree. It is invoked
 * after all document generation phases are complete (model reader, static file, annotation scanning)
 * and is typically used to suppress parts of the document that should not be included. Usually this
 * is either a result of security concerns or anachronistic annotation scanning.
 * 
 * @author eric.wittmann@gmail.com
 */
public interface AAIFilter {

    /**
     * Allows filtering of the entire document. This will be called after all other phases are complete and
     * combined (model reader, static file, annotation scanning).
     * 
     * @param document
     */
    public void filterDocument(Document document);

}
