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
package org.dataconservancy.cos.osf.client.retrofit;

import java.util.List;

/**
 * Represents a response from the {@link OsfService} that may be paginated.  Provides for forward sequential traversal
 * of a collection, transparently retrieving subsequent pages from the API as needed.
 * <p>
 * Example usage:
 * </p>
 * <pre>
 *     OsfService osf = ...;
 *
 *     // Stream the results starting at the first page, automatically retrieving all remaining pages
 *     List&lt;Comment&gt; comments = osf.comments("https://api.osf.io/v2/nodes/y9jdt/comments/")
 *                                             .execute()
 *                                             .body();
 *     comments.stream().forEach(...);
 *
 *     // Or, only process the first two pages
 *     comments = osf.comments("https://api.osf.io/v2/nodes/y9jdt/comments/")
 *                                             .execute()
 *                                             .body();
 *     comments.stream().limit(perPage()*2).forEach(...);
 *
 *     // Or, only process results matching some criteria (e.g. "less than 24 hours old", since the default sort order
 *     // for the OSF JSON API is by date descending)
 *     comments = osf.comments("https://api.osf.io/v2/nodes/y9jdt/comments/")
 *                                             .execute()
 *                                             .body();
 *     // This is better than a simple filter, because iterator() does not eagerly fetch all the elements of the stream
 *     Iterator&lt;Comment&gt; itr = comments.stream().iterator();
 *     Comment c = null;
 *     while(itr.hasNext() && lessThan24((c = itr.next()))) {
 *         // process c
 *     }
 *
 *     // Example anti-pattern - retrieving a specific page
 *     PaginatedList&lt;Comment&gt; pageTwo = (PaginatedList)
 *                                                osf.comments("https://api.osf.io/v2/nodes/y9jdt/comments/?page=2")
 *                                            .execute()
 *                                            .body();
 * </pre>
 * <p>
 * <em>Client considerations:</em>
 * </p>
 * <p>
 * The {@code PaginatedList} interface exposes concepts that should not be used by clients of {@code OsfService}, which
 * is why the methods of {@code OsfService} return {@code java.util.List} and not {@code PaginatedList}.  This may be
 * viewed as a constraint, but the current state of the OSF API is aimed at forward sequential access.  It would be
 * difficult, for example, to implement {@link java.util.ListIterator} without additional pagination metadata in OSF API
 * responses. ({@code PaginatedList} exists to decouple pagination from the JSONAPI-converter, a core dependency of the
 * implementation.)
 * </p>
 * <p>
 * As abstractions are just that, abstractions, it would behoove clients to be wary of certain concrete implementation
 * details. {@code PaginatedList} implementations will be required to request additional pages of results as clients
 * stream responses, which will involve network request overhead, and potential request failures.  Clients should be
 * coded in a defensive manner, noting that some operations (e.g. {@link List#size()}) may depend on the presence of
 * pagination metadata in the OSF API response.  If multiple traversals of the same results are required (e.g. a sort
 * followed by a filter), consider streaming the result into a new data structure (e.g. a new {@code Collection} or
 * {@code Map}) before executing those operations.  Alternately, retrieve the stream once, and perform all the
 * operations in one traversal of the stream if possible.  Finally, as implementing the full {@code List} interface can
 * be challenging and in some cases, not possible, clients should generally assume that instances of
 * {@code PaginatedList} are immutable.  Copy the {@code PaginatedList} instance into an {@code ArrayList}, for example,
 * before proceeding with any mutating operations.
 * </p>
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
