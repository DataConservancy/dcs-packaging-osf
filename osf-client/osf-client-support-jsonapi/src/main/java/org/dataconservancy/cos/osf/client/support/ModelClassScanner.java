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

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * Scans the classpath for classes matching the specified package and annotation type.
 *
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class ModelClassScanner {

    private String packageName;

    private Class<? extends Annotation> annotation;

    /**
     *
     * @param packageName
     * @param annotationType
     */
    public ModelClassScanner(final String packageName, final Class<? extends Annotation> annotationType) {
        this.packageName = packageName;
        this.annotation = annotationType;
    }

    /**
     * Scans the package supplied on construction for classes that are annotated with the annotations supplied on
     * construction.  Useful for supplying the list of classes to the JSON API resource converter.
     *
     * @return a list of classes that meet the detection critera
     */
    public List<Class<?>> getDetectedClasses() {
        final List<Class<?>> domainClasses = new ArrayList<>();
        new FastClasspathScanner(packageName)
                .matchClassesWithAnnotation(annotation, domainClasses::add)
                .scan();

        return domainClasses;
    }
}
