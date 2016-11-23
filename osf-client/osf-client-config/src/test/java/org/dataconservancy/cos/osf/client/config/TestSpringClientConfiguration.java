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

package org.dataconservancy.cos.osf.client.config;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.net.URL;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * A series of tests insuring that configuring the location of the OSF client using a System property will work, and that
 * the use of Spring resource urls are supported (file://, classpath://, classpath*://, and bare classpath resources with no prefix).
 */
public class TestSpringClientConfiguration {

    // Use unique configuration resources in order to test that Spring is loading a specific config file
    private static final String TEST_CLASSPATH_CONFIGURATION_RESOURCE = "/org/dataconservancy/cos/osf/client/config/TestSpringClientConfiguration-classpath.json";
    private static final String TEST_FILE_CONFIGURATION_RESOURCE = "/org/dataconservancy/cos/osf/client/config/TestSpringClientConfiguration-fileurl.json";

    // Expected configuration values from the configurations above.
    private static final String EXPECTED_WB_BASEPATH = "/waterbutlerv1basepath/";
    private static final String EXPECTED_TEST_HOST = "file-test-host";
    private static final int EXPECTED_TEST_PORT = 8888;

    @Test
    public void testClasspathResource() throws Exception {
        System.setProperty("osf.client.conf", "classpath:" + TEST_CLASSPATH_CONFIGURATION_RESOURCE);
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
                "classpath:/org/dataconservancy/cos/osf/client/config/applicationContext.xml",
                "classpath:/org/dataconservancy/cos/osf/client/config/applicationContext-test.xml");
        assertNotNull(ctx.getBean("wbConfigurationSvc"));
        assertEquals(EXPECTED_WB_BASEPATH, ctx.getBean("wbConfigurationSvc", WbConfigurationService.class).getConfiguration().getBasePath());
    }

    @Test
    public void testWildcardClasspathResource() throws Exception {
        System.setProperty("osf.client.conf", "classpath*:" + TEST_CLASSPATH_CONFIGURATION_RESOURCE);
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
                "classpath:/org/dataconservancy/cos/osf/client/config/applicationContext.xml",
                "classpath:/org/dataconservancy/cos/osf/client/config/applicationContext-test.xml");
        assertNotNull(ctx.getBean("wbConfigurationSvc"));
        assertEquals(EXPECTED_WB_BASEPATH, ctx.getBean("wbConfigurationSvc", WbConfigurationService.class).getConfiguration().getBasePath());
    }

    @Test
    public void testBareClasspathResource() throws Exception {
        System.setProperty("osf.client.conf", TEST_CLASSPATH_CONFIGURATION_RESOURCE);
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
                "classpath:/org/dataconservancy/cos/osf/client/config/applicationContext.xml",
                "classpath:/org/dataconservancy/cos/osf/client/config/applicationContext-test.xml");
        assertNotNull(ctx.getBean("wbConfigurationSvc"));
        assertEquals(EXPECTED_WB_BASEPATH, ctx.getBean("wbConfigurationSvc", WbConfigurationService.class).getConfiguration().getBasePath());
    }

    @Test
    public void testFileUrlResource() throws Exception {
        URL fileUrlResource = this.getClass().getResource(TEST_FILE_CONFIGURATION_RESOURCE);
        assertTrue(Paths.get(fileUrlResource.getPath()).toFile().exists());
        System.setProperty("osf.client.conf", fileUrlResource.toString());
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
                "classpath:/org/dataconservancy/cos/osf/client/config/applicationContext.xml",
                "classpath:/org/dataconservancy/cos/osf/client/config/applicationContext-test.xml");
        assertNotNull(ctx.getBean("wbConfigurationSvc"));
        assertEquals(EXPECTED_TEST_HOST, ctx.getBean("wbConfigurationSvc", WbConfigurationService.class).getConfiguration().getHost());
    }

    /**
     * This tests insures that the default resource specified in applicationContext-test.xml is used when there is no system property
     * defined for 'osf.client.conf'.
     *
     * @throws Exception
     */
    @Test
    public void testAbsentProperty() throws Exception {
        assertNull(System.getProperty("osf.client.conf"));
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
                "classpath:/org/dataconservancy/cos/osf/client/config/applicationContext.xml",
                "classpath:/org/dataconservancy/cos/osf/client/config/applicationContext-test.xml");
        assertNotNull(ctx.getBean("wbConfigurationSvc"));
        assertEquals(EXPECTED_TEST_PORT, ctx.getBean("wbConfigurationSvc", WbConfigurationService.class).getConfiguration().getPort());
    }
}
