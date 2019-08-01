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

package io.smallrye.asyncapi.apps.modelReader;

import io.apicurio.datamodels.Library;
import io.apicurio.datamodels.asyncapi.v2.models.Aai20Document;
import io.apicurio.datamodels.core.models.Document;
import io.apicurio.datamodels.core.models.DocumentType;
import io.smallrye.asyncapi.spec.AAIModelReader;

/**
 * A model reader implementation used for TCK testing.
 * 
 * @author eric.wittmann@gmail.com
 */
public class ModelReaderImpl implements AAIModelReader {

    /**
     * @see io.smallrye.asyncapi.spec.AAIModelReader#buildModel()
     */
    @Override
    public Document buildModel() {
        Aai20Document document = (Aai20Document) Library.createDocument(DocumentType.asyncapi2);
        document.asyncapi = "2.0.0";
        document.info = document.createInfo();
        document.info.title = "Model Reader API";
        document.info.description = "An API definition created by a model reader.";
        document.info.version = "3.0.7";

        return document;
    }

}
