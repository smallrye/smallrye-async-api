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

package io.smallrye.asyncapi.api.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * @author eric.wittmann@gmail.com
 */
public class IOUtil {

    /**
     * Converts the given input stream to a string. Assumes UTF8 as the encoding.
     * 
     * @param content
     */
    public static String toString(InputStream content) {
        if (content == null) {
            return null;
        }
        try {
            Charset charset = Charset.forName("UTF-8");
            StringBuilder builder = new StringBuilder();
            byte[] buff = new byte[4096];
            int size = content.read(buff);
            while (size != -1) {
                String data = new String(buff, 0, size, charset);
                builder.append(data);
                size = content.read(buff);
            }
            return builder.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
