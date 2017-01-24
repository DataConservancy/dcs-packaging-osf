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

import java.util.List;

/**
 * Represents a response from the {@link OsfService} that may be paginated.
 * <p>
 * Example usage:
 * </p>
 * <pre>
 *     OsfService osf = ...;
 *
 *     // retrieves the first page of results
 *     PaginatedList&lt;Comment&gt; comments = osf.comments("https://api.osf.io/v2/nodes/y9jdt/comments/")
 *                                             .execute()
 *                                             .body();
 *
 *     // stream the results, automatically retrieving all remaining pages
 *     comments.stream().forEach(...);
 *
 *     // only get the next page
 *     PaginatedList&lt;Comment&gt; pageTwo = osf.comments(comments.getNext())
 *                                            .execute()
 *                                            .body();
 * </pre>
 *
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public interface PaginatedList<T> extends List<T> {

    /**
     * Returns a URL which may used to retrieve the next page of results.  May be {@code null} if there is no more
     * pages.
     *
     * @return a URL to the next page of results
     */
    public String getNext();

    /**
     * Returns a URL which may be used to retrieve the previous page of results.  May be {@code null} if there are no
     * previous pages.
     *
     * @return a URL to the previous page of results
     */
    public String getPrevious();

    /**
     * Returns a URL which may be used to retrieve the first page of results.  May be {@code null} if underlying
     * implementations do not supply this information.
     *
     * @return a URL to the first page of results
     */
    public String getFirst();

    /**
     * Returns a URL which may be used to retrieve the last page of results.  May be {@code null} if underlying
     * implementations do not supply this information.
     *
     * @return a URL to the last page of results
     */
    public String getLast();

    /**
     * Returns the total count of results across all pages, if known.
     *
     * @return the total count of results across all pages, or {@code -1} if unknown
     */
    public int total();

    /**
     * Returns the maximum number of results per page, if known.
     *
     * @return the maximum number of results per page, or {@code -1} if unknown
     */
    public int perPage();

}
