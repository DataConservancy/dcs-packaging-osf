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

import org.dataconservancy.cos.osf.client.model.License;
import org.dataconservancy.cos.osf.client.model.NodeBase;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;

/**
 * Generates a unique has URI given the License's enclosing Node (or Registration).
 *
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class LicenseHashUriGenerator implements BiFunction<NodeBase, License, String> {

    private static final AtomicInteger ID = new AtomicInteger(0);

    private static final String uriTemplate = "%s#license%s";

    @Override
    public String apply(final NodeBase nodeBase, final License license) {
        return String.format(uriTemplate, nodeBase.getId(), String.valueOf(ID.getAndIncrement()));
    }

}
