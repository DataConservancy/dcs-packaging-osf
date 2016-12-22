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

import java.util.function.BiFunction;

/**
 * File objects that represent an OSF storage provider will have identifiers in the form: {@code nodeId:providerId}.
 * This transform replaces the {@code :} with a {@code _} so that the id can be properly encoded as a URI.
 *
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class ProviderIdTransform implements BiFunction<Object, File, String> {

    @Override
    public String apply(final Object o, final File file) {
        if (file.getId().contains(":")) {
            return file.getId().replace(":", "_");
        }

        return file.getId();
    }

}
