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
package org.dataconservancy.cos.osf.client.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jasminb.jsonapi.ResourceConverter;
import com.github.jasminb.jsonapi.annotations.Type;
import com.github.jasminb.jsonapi.retrofit.JSONAPIConverterFactory;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import org.dataconservancy.cos.osf.client.config.JacksonOsfConfigurationService;
import org.dataconservancy.cos.osf.client.config.JacksonWbConfigurationService;
import org.dataconservancy.cos.osf.client.config.OsfConfigurationService;
import org.dataconservancy.cos.osf.client.config.WbConfigurationService;
import org.dataconservancy.cos.osf.client.support.ApiVersionInterceptor;
import org.dataconservancy.cos.osf.client.support.AuthInterceptor;
import retrofit.Retrofit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Factory which wires collaborating objects and produces a Retrofit service interface.
 * <p>
 * Defaults for this factory, when not supplied on construction, are:
 * </p>
 * <ul>
 * <li>OsfConfigurationService: JacksonOsfConfigurationService</li>
 * <li>WbConfigurationService: JacksonWbConfigurationService</li>
 * <li>OkHttpClient: OkHttpClient (no abstraction)</li>
 * <li>JSONAPIConverterFactory: JSONAPIConverterFactory (no abstraction)</li>
 * <li>Default configuration location: /org/dataconservancy/osf/client/config/osf-client.json</li>
 * </ul>
 * <p>
 * This facade hides a lot of complex boilerplate used to configure a Retrofit instance of {@link OsfService}.  Retrofit
 * requires three things:
 * </p>
 * <ul>
 * <li>The base URL of the JSON api</li>
 * <li>An implementation of {@link retrofit.Converter.Factory}</li>
 * <li>An HTTP client</li>
 * </ul>
 * <p>
 * The base URL of the JSON api is obtained from the {@link OsfConfigurationService}, the {@code Converter.Factory}
 * implementation is supplied by an instance of {@link JSONAPIConverterFactory}, and the HTTP client is an instance
 * of the {@link OkHttpClient}.  The different constructors all result in building these collaborators in different
 * ways.  They are either supplied by the caller on construction (in which case they are expected to be fully
 * configured), or default implementations are instantiated and configured within the constructor.
 * </p>
 * <h3>Example usage</h3>
 * <h4>Typical usage</h4>
 * <pre>
 *     RetrofitOsfServiceFactory factory = new RetrofitOsfServiceFactory();
 *     // default configuration resolves to /org/dataconservancy/cos/osf/client/config/osf-client.json
 *     OsfService osfService = factory.getOsfService(OsfService.class);
 * </pre>
 * <h4>Custom configuration resource</h4>
 * <pre>
 *     RetrofitOsfServiceFactory factory = new RetrofitOsfServiceFactory("custom-client-config.json");
 *     // custom-client-config.json resolved to /org/dataconservancy/cos/osf/client/config/custom-client-config.json
 *     OsfService osfService = factory.getOsfService(OsfService.class);
 * </pre>
 * <h4>Custom JSONAPIConverter</h4>
 * <pre>
 *     List&lt;Class&lt;?&gt;&gt; domainClasses = new ArrayList&lt;&gt;();
 *     // Add classes annotated with @Type, indicating their participation in the JSON-API Converter framework
 *     domainClasses.add(Foo.class);
 *     domainClasses.add(Bar.class);
 *
 *     ObjectMapper mapper = new ObjectMapper();
 *     // Configure the Jackson ObjectMapper if you wish
 *
 *     // Instantiate the ResourceConverter using the domain classes and ObjectMapper
 *     ResourceConverter resourceConverter = new ResourceConverter(mapper, domainClasses.toArray(new Class[]{}));
 *
 *     // If you don't plan on resolving links encountered in JSON documents, you can skip the instantiation
 *     // and configuration of the global resolver.
 *
 *     // Instantiate your favorite HTTP client.  It could be OkHttp or any other library.
 *     OkHttpClient httpClient = new OkHttpClient();
 *
 *     // Add a global resolver implementation used by the ResourceConverter to resolve URLs encountered in
 *     // JSON documents
 *     resourceConverter.setGlobalResolver(relUrl -&gt; {
 *       com.squareup.okhttp.Call req = httpClient.newCall(new Request.Builder().url(relUrl).build());
 *         try {
 *           return req.execute().body().bytes();
 *         } catch (IOException e) {
 *           throw new RuntimeException(e.getMessage(), e);
 *        }
 *     });
 *
 *     // Finally instantiate the JSONAPIConverterFactory
 *     JSONAPIConverterFactory jsonApiConverterFactory = new JSONAPIConverterFactory(resourceConverter);
 *
 *     RetrofitOsfServiceFactory factory = new RetrofitOsfServiceFactory(jsonApiConverterFactory);
 *     // default configuration resolves to /org/dataconservancy/cos/osf/client/config/osf-client.json
 *     OsfService osfService = factory.getOsfService(OsfService.class);
 * </pre>
 * Other configuration exercises are left to the reader.
 *
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class RetrofitOsfServiceFactory {

    /**
     * Parameters are: class name, error message
     */
    private static final String ERR_CONFIGURING_CLASS = "Unable to configure the %s: %s";

    private static final String DEFAULT_CONFIGURATION_RESOURCE = "osf-client.json";

    private static final String NOT_NULL_IAE = "%s must not be null.";

    /**
     * Configured OSF configuration service, provides access to the base URL of the OSF v2 API
     */
    private final OsfConfigurationService osfConfigSvc;

    /**
     * Configured Waterbutler configuration service, provides access to the base URL of the v1 Waterbutler API
     */
    private final WbConfigurationService wbConfigSvc;

    /**
     * The OK HTTP client used by Retrofit for HTTP requests
     */
    private final OkHttpClient httpClient;

    /**
     * The JSON-API converter factory used to map JSON documents to Java objects.
     */
    private final JSONAPIConverterFactory jsonApiConverterFactory;


    /**
     * Constructs a new RetrofitOsfServiceFactory with the default JSON configuration classpath resource.
     * Default implementations of the OSF and Waterbutler configuration services, Jackson {@code ObjectMapper}, and
     * {@code OkHttpClient} will be used.  By default this constructor will look for a classpath resource at
     * {@code /org/dataconservancy/cos/osf/client/config/osf-client.json}.
     */
    public RetrofitOsfServiceFactory() {
        this(DEFAULT_CONFIGURATION_RESOURCE);
    }

    /**
     * Constructs a new RetrofitOsfServiceFactory with the supplied JSON configuration classpath resource.
     * Default implementations of the OSF and Waterbutler configuration services, Jackson {@code ObjectMapper}, and
     * {@code OkHttpClient} will be used.  If the classpath resource is <em>not</em> absolute (beginning with a
     * '{@code /}'), then this constructor will resolve the resource under
     * {@code /org/dataconservancy/cos/osf/client/config/}.  This constructor adds the {@link AuthInterceptor} to the
     * {@code OkHttpClient} if an {@code authHeader} is found in the configuration for the OSF v2 API.  It will
     * scan the classpath under {@code org.dataconservancy.cos.osf.client.model} for classes with the {@link Type}
     * annotation, and add them to the {@link com.github.jasminb.jsonapi.ResourceConverter} used to convert JSON
     * documents to Java objects.  The {@code ResourceConverter} is also configured to resolve urls using the
     * {@code OkHttpClient}.
     *
     * @param jsonConfigurationResource classpath resource containing the JSON configuration for the OSF and Waterbutler
     *                                  HTTP endpoints
     */
    public RetrofitOsfServiceFactory(final String jsonConfigurationResource) {
        try {
            this.osfConfigSvc = new JacksonOsfConfigurationService(jsonConfigurationResource);
        } catch (Exception e) {
            throw new IllegalStateException(
                    String.format(ERR_CONFIGURING_CLASS,
                            JacksonOsfConfigurationService.class.getName(), e.getMessage()), e);
        }
        try {
            this.wbConfigSvc = new JacksonWbConfigurationService(jsonConfigurationResource);
        } catch (Exception e) {
            throw new IllegalStateException(
                    String.format(ERR_CONFIGURING_CLASS,
                            JacksonWbConfigurationService.class.getName(), e.getMessage()), e);
        }
        this.httpClient = new OkHttpClient();
        if (osfConfigSvc.getConfiguration().getAuthHeader() != null) {
            httpClient.interceptors().add(new AuthInterceptor(osfConfigSvc.getConfiguration().getAuthHeader()));
        }
        if (osfConfigSvc.getConfiguration().getApiVersion() != null) {
            httpClient.interceptors().add(new ApiVersionInterceptor(osfConfigSvc.getConfiguration().getApiVersion()));
        }

        // ... the JSON-API converter used by Retrofit to map JSON documents to Java objects
        final List<Class<?>> domainClasses = new ArrayList<>();

        new FastClasspathScanner("org.dataconservancy.cos.osf.client.model")
                .matchClassesWithAnnotation(Type.class, domainClasses::add)
                .scan();

        final ResourceConverter resourceConverter = new ResourceConverter(new ObjectMapper(),
                domainClasses.toArray(new Class[]{}));

        resourceConverter.setGlobalResolver(relUrl -> {
            final com.squareup.okhttp.Call req = httpClient.newCall(new Request.Builder().url(relUrl).build());
            try {
                return req.execute().body().bytes();
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        });


        try {
            this.jsonApiConverterFactory = new JSONAPIConverterFactory(resourceConverter);
        } catch (Exception e) {
            throw new IllegalStateException(
                    String.format(ERR_CONFIGURING_CLASS, JSONAPIConverterFactory.class.getName(), e.getMessage()), e);
        }
    }

    /**
     * Constructs a new RetrofitOsfServiceFactory with the supplied JSON API converter factory.
     * Default implementations of the OSF and Waterbutler configuration services and {@code OkHttpClient} will be used.
     * By default this constructor will look for a classpath resource at
     * {@code /org/dataconservancy/cos/osf/client/config/osf-client.json}.  This constructor adds the
     * {@link AuthInterceptor} to the {@code OkHttpClient} if an {@code authHeader} is found in the configuration for
     * the OSF v2 API.
     *
     * @param jsonApiConverterFactory the configured Retrofit {@code retrofit.Converter.Factory} to use
     */
    public RetrofitOsfServiceFactory(final JSONAPIConverterFactory jsonApiConverterFactory) {
        if (jsonApiConverterFactory == null) {
            throw new IllegalArgumentException(String.format(NOT_NULL_IAE, JSONAPIConverterFactory.class.getName()));
        }

        this.jsonApiConverterFactory = jsonApiConverterFactory;

        try {
            this.osfConfigSvc = new JacksonOsfConfigurationService(DEFAULT_CONFIGURATION_RESOURCE);
        } catch (Exception e) {
            throw new IllegalStateException(
                    String.format(ERR_CONFIGURING_CLASS,
                            JacksonOsfConfigurationService.class.getName(), e.getMessage()), e);
        }
        try {
            this.wbConfigSvc = new JacksonWbConfigurationService(DEFAULT_CONFIGURATION_RESOURCE);
        } catch (Exception e) {
            throw new IllegalStateException(
                    String.format(ERR_CONFIGURING_CLASS,
                            JacksonWbConfigurationService.class.getName(), e.getMessage()), e);
        }
        this.httpClient = new OkHttpClient();
        if (osfConfigSvc.getConfiguration().getAuthHeader() != null) {
            httpClient.interceptors().add(new AuthInterceptor(osfConfigSvc.getConfiguration().getAuthHeader()));
        }
        if (osfConfigSvc.getConfiguration().getApiVersion() != null) {
            httpClient.interceptors().add(new ApiVersionInterceptor(osfConfigSvc.getConfiguration().getApiVersion()));
        }
    }

    /**
     * Constructs a new RetrofitOsfServiceFactory with the supplied JSON API converter factory and configuration
     * resource. Default implementations of the OSF and Waterbutler configuration services and {@code OkHttpClient} will
     * be used. If the classpath resource is <em>not</em> absolute (beginning with a '{@code /}'), then this constructor
     * will resolve the resource under {@code /org/dataconservancy/cos/osf/client/config/}.  This constructor adds the
     * {@link AuthInterceptor} to the {@code OkHttpClient} if an {@code authHeader} is found in the configuration for
     * the OSF v2 API.
     *
     * @param jsonConfigurationResource classpath resource containing the JSON configuration for the OSF and Waterbutler
     *                                  HTTP endpoints
     * @param jsonApiConverterFactory   the configured Retrofit {@code retrofit.Converter.Factory} to use
     */
    public RetrofitOsfServiceFactory(final String jsonConfigurationResource,
                                     final JSONAPIConverterFactory jsonApiConverterFactory) {
        if (jsonApiConverterFactory == null) {
            throw new IllegalArgumentException(String.format(NOT_NULL_IAE, JSONAPIConverterFactory.class.getName()));
        }

        this.jsonApiConverterFactory = jsonApiConverterFactory;

        try {
            this.osfConfigSvc = new JacksonOsfConfigurationService(jsonConfigurationResource);
        } catch (Exception e) {
            throw new IllegalStateException(
                    String.format(ERR_CONFIGURING_CLASS,
                            JacksonOsfConfigurationService.class.getName(), e.getMessage()), e);
        }
        try {
            this.wbConfigSvc = new JacksonWbConfigurationService(jsonConfigurationResource);
        } catch (Exception e) {
            throw new IllegalStateException(
                    String.format(ERR_CONFIGURING_CLASS,
                            JacksonWbConfigurationService.class.getName(), e.getMessage()), e);
        }
        this.httpClient = new OkHttpClient();
        if (osfConfigSvc.getConfiguration().getAuthHeader() != null) {
            httpClient.interceptors().add(new AuthInterceptor(osfConfigSvc.getConfiguration().getAuthHeader()));
        }
        if (osfConfigSvc.getConfiguration().getApiVersion() != null) {
            httpClient.interceptors().add(new ApiVersionInterceptor(osfConfigSvc.getConfiguration().getApiVersion()));
        }
    }

    /**
     * Constructs a new RetrofitOsfServiceFactory with the default OSF and Waterbutler configuration services.  By
     * default this constructor will look for a classpath resource at
     * {@code /org/dataconservancy/cos/osf/client/config/osf-client.json} in order to configure the
     * {@code WbConfigurationService} and {@code OsfConfigurationService}.
     *
     * @param httpClient              the OK HTTP Client
     * @param jsonApiConverterFactory the JSON API converter factory
     */
    public RetrofitOsfServiceFactory(final JSONAPIConverterFactory jsonApiConverterFactory,
                                     final OkHttpClient httpClient) {
        if (httpClient == null) {
            throw new IllegalArgumentException(String.format(NOT_NULL_IAE, OkHttpClient.class.getName()));
        }

        if (jsonApiConverterFactory == null) {
            throw new IllegalArgumentException(String.format(NOT_NULL_IAE, JSONAPIConverterFactory.class.getName()));
        }

        try {
            this.osfConfigSvc = new JacksonOsfConfigurationService(DEFAULT_CONFIGURATION_RESOURCE);
        } catch (Exception e) {
            throw new IllegalStateException(
                    String.format(ERR_CONFIGURING_CLASS,
                            JacksonOsfConfigurationService.class.getName(), e.getMessage()), e);
        }
        try {
            this.wbConfigSvc = new JacksonWbConfigurationService(DEFAULT_CONFIGURATION_RESOURCE);
        } catch (Exception e) {
            throw new IllegalStateException(
                    String.format(ERR_CONFIGURING_CLASS,
                            JacksonWbConfigurationService.class.getName(), e.getMessage()), e);
        }
        this.jsonApiConverterFactory = jsonApiConverterFactory;
        this.httpClient = httpClient;
    }

    /**
     * Constructs a new RetrofitOsfServiceFactory with the default Waterbutler configuration service.  By
     * default this constructor will look for a classpath resource at
     * {@code /org/dataconservancy/cos/osf/client/config/osf-client.json} in order to configure the
     * {@code JacksonWbConfigurationService}.
     *
     * @param osfConfigSvc            the OSF configuration service
     * @param httpClient              the OK HTTP Client
     * @param jsonApiConverterFactory the JSON API converter factory
     */
    public RetrofitOsfServiceFactory(final OsfConfigurationService osfConfigSvc, final OkHttpClient httpClient,
                                     final JSONAPIConverterFactory jsonApiConverterFactory) {
        if (osfConfigSvc == null) {
            throw new IllegalArgumentException(String.format(NOT_NULL_IAE, OsfConfigurationService.class.getName()));
        }

        if (httpClient == null) {
            throw new IllegalArgumentException(String.format(NOT_NULL_IAE, OkHttpClient.class.getName()));
        }

        if (jsonApiConverterFactory == null) {
            throw new IllegalArgumentException(String.format(NOT_NULL_IAE, JSONAPIConverterFactory.class.getName()));
        }

        this.osfConfigSvc = osfConfigSvc;
        try {
            this.wbConfigSvc = new JacksonWbConfigurationService(DEFAULT_CONFIGURATION_RESOURCE);
        } catch (Exception e) {
            throw new IllegalStateException(
                    String.format(ERR_CONFIGURING_CLASS, WbConfigurationService.class.getName(), e.getMessage()), e);
        }
        this.httpClient = httpClient;
        this.jsonApiConverterFactory = jsonApiConverterFactory;
    }

    /**
     * Constructs a new RetrofitOsfServiceFactory with the default OSF configuration service.  By
     * default this constructor will look for a classpath resource at
     * {@code /org/dataconservancy/cos/osf/client/config/osf-client.json} in order to configure the
     * {@code JacksonOsfConfigurationService}.
     *
     * @param wbConfigSvc             the Waterbutler configuration service
     * @param httpClient              the OK HTTP Client
     * @param jsonApiConverterFactory the JSON API converter factory
     */
    public RetrofitOsfServiceFactory(final WbConfigurationService wbConfigSvc, final OkHttpClient httpClient,
                                     final JSONAPIConverterFactory jsonApiConverterFactory) {
        if (wbConfigSvc == null) {
            throw new IllegalArgumentException(String.format(NOT_NULL_IAE, WbConfigurationService.class.getName()));
        }

        if (httpClient == null) {
            throw new IllegalArgumentException(String.format(NOT_NULL_IAE, OkHttpClient.class.getName()));
        }

        if (jsonApiConverterFactory == null) {
            throw new IllegalArgumentException(String.format(NOT_NULL_IAE, JSONAPIConverterFactory.class.getName()));
        }

        try {
            this.osfConfigSvc = new JacksonOsfConfigurationService(DEFAULT_CONFIGURATION_RESOURCE);
        } catch (Exception e) {
            throw new IllegalStateException(
                    String.format(ERR_CONFIGURING_CLASS,
                            JacksonOsfConfigurationService.class.getName(), e.getMessage()), e);
        }

        this.wbConfigSvc = wbConfigSvc;
        this.httpClient = httpClient;
        this.jsonApiConverterFactory = jsonApiConverterFactory;
    }

    /**
     * Constructs a new RetrofitOsfServiceFactory with all the required collaborators.
     *
     * @param osfConfigSvc            the OSF configuration service
     * @param wbConfigSvc             the Waterbutler configuration service
     * @param httpClient              the OK HTTP Client
     * @param jsonApiConverterFactory the JSON API converter factory
     *
     */
    public RetrofitOsfServiceFactory(final OsfConfigurationService osfConfigSvc,
                                     final WbConfigurationService wbConfigSvc, final OkHttpClient httpClient,
                                     final JSONAPIConverterFactory jsonApiConverterFactory) {
        if (osfConfigSvc == null) {
            throw new IllegalArgumentException(String.format(NOT_NULL_IAE, OsfConfigurationService.class.getName()));
        }

        if (wbConfigSvc == null) {
            throw new IllegalArgumentException(String.format(NOT_NULL_IAE, WbConfigurationService.class.getName()));
        }

        if (httpClient == null) {
            throw new IllegalArgumentException(String.format(NOT_NULL_IAE, OkHttpClient.class.getName()));
        }

        if (jsonApiConverterFactory == null) {
            throw new IllegalArgumentException(String.format(NOT_NULL_IAE, JSONAPIConverterFactory.class.getName()));
        }

        this.osfConfigSvc = osfConfigSvc;
        this.wbConfigSvc = wbConfigSvc;
        this.httpClient = httpClient;
        this.jsonApiConverterFactory = jsonApiConverterFactory;
    }


    /**
     * Answers a configured Retrofit-based OSF client that communicates with the OSF V2 API at the base URL obtained
     * from the OSF configuration service.  The JSON responses from the OSF V2 API will be mapped to Java objects
     * according to the JSON API converter factory.  HTTP requests and responses are sent via the OK HTTP client.
     *
     * @param osfService the Retrofit interface that contains the methods used by the developer to communicate with the
     *                   OSF API.
     * @param <T>        the Retrofit interface type
     * @return a configured Retrofit interface, ready to service requests.
     */
    public <T> T getOsfService(final Class<T> osfService) {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(osfConfigSvc.getConfiguration().getBaseUri().toString())
                .addConverterFactory(jsonApiConverterFactory)
                .client(httpClient)
                .build();

        return retrofit.create(osfService);
    }

}
