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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Insures proper behavior of the ContributorIdSplitter
 *
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class ContributorIdSplitterTest {

    @Test
    public void testSplitIdNoDash() throws Exception {
        assertEquals("abcde", new ContributorIdSplitter().apply("abcde"));
    }

    @Test
    public void testSplitIdWithDash() throws Exception {
        assertEquals("fghij", new ContributorIdSplitter().apply("abcde-fghij"));
    }

    @Test
    public void testSplitIdWithTwoDashs() throws Exception {
        assertEquals("fghij", new ContributorIdSplitter().apply("abcde-fghij-klmno"));
    }

    @Test(expected = RuntimeException.class)
    public void testSplitIdWithTrailingDash() throws Exception {
        // The original id is malformed, so best to not accept it.
        new ContributorIdSplitter().apply("abcde-");
    }

    @Test(expected = RuntimeException.class)
    public void testSplitIdWithLeadingDash() throws Exception {
        // The original id is malformed, so best to not accept it.
        new ContributorIdSplitter().apply("-fghij");
    }

    @Test
    public void testSplitIdWithColon() throws Exception {
        assertEquals("abcde:fghij", new ContributorIdSplitter().apply("abcde:fghij"));
    }
}