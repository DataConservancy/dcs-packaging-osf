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
package org.dataconservancy.cos.osf.client.model.mapping.v2_2.graphs;

import org.dataconservancy.cos.osf.client.model.Comment;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class CommentGraphMappingTest extends BaseGraphMappingTest {

    @Test
    public void testCommentRelationships() throws Exception {

        final List<Comment> comments = osfService
                .comments("http://localhost:8000/v2/nodes/y9jdt/comments/P2ZpbHRlciU1QnRhcmdldCU1RD0ycmt5YWhzcHR3Nzg=")
                .execute().body();

        assertEquals(1, comments.size());
        final Comment comment = comments.get(0);

        // node
        assertEquals("y9jdt", comment.getNode().getId());

        // replies
        assertEquals("http://localhost:8000/v2/nodes/y9jdt/comments/P2ZpbHRlciU1QnRhcmdldCU1RD00ZHRwZ2M4Zm51aGs=",
                comment.getReplies());

        // target
        assertEquals("http://localhost:8000/v2/comments/2rkyahsptw78/", comment.getTarget());

        // reports
        assertEquals("http://localhost:8000/v2/comments/4dtpgc8fnuhk/reports/", comment.getReports());

        // user
        assertEquals("5fukm", comment.getUser().getId());

    }
}
