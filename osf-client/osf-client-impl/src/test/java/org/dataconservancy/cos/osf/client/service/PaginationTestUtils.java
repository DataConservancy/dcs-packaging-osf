/*
 * Copyright 2017 Johns Hopkins University
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
package org.dataconservancy.cos.osf.client.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class PaginationTestUtils {

    private PaginationTestUtils() {
        // Dis-allow construction
    }

    /**
     * Returns a List of {@code TestResource} using the supplied ids.  Each id will be used to create one {@code
     * TestResource} object.  The returned List will be ordered as {@code ids}.
     *
     * @param ids the ids used for TestResource instances.
     * @return a List of TestResource objects
     */
    @SuppressWarnings("unchecked")
    static List ofIds(final String... ids) {
        final ArrayList resources = new ArrayList();
        for (String id : ids) {
            resources.add(new TestResource(id));
        }

        return resources;
    }

    /**
     * A simple object carrying a string identifier, and a hashCode() and equals() implementation.
     */
    static class TestResource {
        private final String id;

        public TestResource(final String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }

            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            final TestResource that = (TestResource) o;

            return id != null ? id.equals(that.id) : that.id == null;
        }

        @Override
        public int hashCode() {
            return id != null ? id.hashCode() : 0;
        }
    }

    /**
     * A Meta object used to supply pagination information.
     *
     * @param <T>
     */
    static class Meta<T> extends HashMap<String, T> {

        private final int total;

        private final int perPage;

        @SuppressWarnings("unchecked")
        public Meta(final Integer total, final Integer perPage) {
            this.total = total;
            this.perPage = perPage;

            put("total", (T) total);
            put("per_page", (T) perPage);
        }

        public int getTotal() {
            return total;
        }

        public int getPerPage() {
            return perPage;
        }
    }
}
