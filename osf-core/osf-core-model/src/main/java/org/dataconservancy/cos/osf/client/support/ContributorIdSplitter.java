/*
 *
 *  * Copyright 2016 Johns Hopkins University
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.dataconservancy.cos.osf.client.support;

import java.util.function.Function;

/**
 * The latest versions of the OSF API concat the node or registration id with the user id for use as contributor id.
 * For example, a contributor id may be {@code 0zqbo-vni4p} where {@code 0zqbo} is a registration id, and {@code vni4p}
 * is a user id.  This transformation splits the id on the {@code -} character, and returns the user id portion of the
 * identifier.  Older versions of the OSF JSON API did not provide a relationship between a contributor
 * and a user, so the relationship was derived by parsing the contributor identifier.
 *
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class
ContributorIdSplitter implements Function<String, String> {

    @Override
    public String apply(final String contributorId) {
        try {
            if (contributorId.contains("-")) {
                if (contributorId.startsWith("-")) {
                    // Since the incoming id is malformed, do we really trust that the characters after the dash can
                    // be trusted?  To be consistent with the behavior when receiving an id with a trailing dash, lets
                    // throw a RuntimeException.
                    throw new RuntimeException(
                            "Unable to parse malformed contributor identifier: '" + contributorId + "'");
                }
                return contributorId.split("-")[1];
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            // If this happens, there's probably an error in the way the contributorId is
            // represented, e.g. with a trailing dash like "abcde-"
            //
            // Returning a malformed ID is not what we want to do, so err on the side of
            // throwing an exception
            throw new RuntimeException("Unable to parse malformed contributor identifier: '" + contributorId + "'");
        }

        return contributorId;
    }

}
