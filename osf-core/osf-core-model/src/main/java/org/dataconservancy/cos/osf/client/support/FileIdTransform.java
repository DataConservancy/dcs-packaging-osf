/*
 * Copyright 2016 Johns Hopkins University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dataconservancy.cos.osf.client.support;


import org.dataconservancy.cos.osf.client.model.File;

import java.util.function.Function;

/**
 * Creates an id for a File in the form {@code providerName:fileId}.
 * <p>
 * TODO: To calculate a file id, its node is required.  Unfortunately, only the first level of files
 * in a file hierarchy (the providers) carry a Node instance.  So this class does not work as intended.
 * </p>
 *
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class FileIdTransform implements Function<File, String> {
    @Override
    public String apply(final File file) {
//        if (file.getNode() != null) {
//            return file.getId();
//        }
//        return file.getProvider() + ":" + file.getId();
        return file.getId();
    }
}
