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

import org.dataconservancy.cos.osf.client.model.MetaSchema;
import org.dataconservancy.cos.osf.client.model.Schema;
import org.dataconservancy.cos.osf.client.model.SchemaPage;
import org.dataconservancy.cos.osf.client.model.SchemaQuestion;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class MetaSchemaObjectMappingTest extends BaseGraphMappingTest {

    @Test
    public void testMetaSchemaObjectMapping() throws Exception {
        final MetaSchema metaSchema =
                osfService.metaschema("http://localhost:8000/v2/metaschemas/564d31db8c5e4a7c9694b2be/")
                        .execute().body();
        assertNotNull(metaSchema);

        assertEquals("Open-Ended Registration", metaSchema.getName());
        assertEquals(2, metaSchema.getSchema_version());
        assertEquals("564d31db8c5e4a7c9694b2be", metaSchema.getId());
        assertEquals("http://localhost:8000/v2/metaschemas/564d31db8c5e4a7c9694b2be/",
                metaSchema.getLinks().get("self"));

        final Schema schema = metaSchema.getSchema();

        assertTrue(schema.getDescription().startsWith("You will be asked"));
        assertEquals("Open-Ended Registration", schema.getName());
        assertEquals("Open-Ended Registration", schema.getTitle());
        assertEquals(2, schema.getVersion());
        assertEquals(1, schema.getPages().size());

        final SchemaPage page = schema.getPages().get(0);

        assertEquals("page1", page.getId());
        assertEquals("Summary", page.getTitle());
        assertEquals(1, page.getQuestions().size());

        final SchemaQuestion q = page.getQuestions().get(0);

        assertTrue(q.getDescription().startsWith("Provide a narrative"));
        assertEquals("textarea", q.getFormat());
        assertEquals("Summary", q.getNav());
        assertEquals("summary", q.getQid());
        assertEquals("Summary", q.getTitle());
        assertEquals("string", q.getType());
    }
}
