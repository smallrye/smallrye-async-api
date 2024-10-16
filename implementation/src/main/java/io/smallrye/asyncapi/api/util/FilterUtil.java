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

package io.smallrye.asyncapi.api.util;

import io.apicurio.datamodels.core.models.Document;
import io.smallrye.asyncapi.spec.AAIFilter;

/**
 * @author eric.wittmann@gmail.com
 */
public class FilterUtil {

    /**
     * Constructor.
     */
    private FilterUtil() {
    }

    /**
     * Apply the given filter to the given model.
     *
     * @param filter
     * @param document
     */
    public static final Document applyFilter(AAIFilter filter, Document document) {
        filter.filterDocument(document);
        return document;
    }

}
