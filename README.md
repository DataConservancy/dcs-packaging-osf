## OSF Client

This is a prototype client for interacting with the OSF v2 JSON-API.  

It provides a interface for making HTTP calls to the OSF API, and a model for interacting with the data returned by the API.  Currently the API is read-only; writes to the OSF API are not supported.

The client is comprised of:

* a [Retrofit-based](http://square.github.io/retrofit/) interface `OsfService` in the `org.dataconservancy.cos.osf.client.service` package
    * This is the entrance point into the OSF API. 
* a Java model representing the types and relationships presented by the OSF JSON-API
    * e.g., a `Node`, `Registration`, `User`, `Contributor`, etc.
* Spring wiring for those who wish to use Spring to configure or inject `OsfService` instances

### Installation

1. Express a dependency on the Maven artifact in your pom, or [download the jar](http://maven.dataconservancy.org/public/snapshots/org/dataconservancy/cos/osf-client/) and place it on your classpath
```xml
<dependency>
  <groupId>org.dataconservancy.cos</groupId>
  <artifactId>osf-client</artifactId>
  <scope>compile</scope>
  <version>1.0.1-SNAPSHOT</version>
</dependency>
```
2. Configure the client and store the configuration.  The default location of the configuration resource is `/org/dataconservancy/cos/osf/client/config/osf-client.json`, but this can be overridden by specifying an alternate location using the `osf.client.conf` system property.
```json
{
  "osf": {
    "v2": {
      "host": "api.osf.io",
      "port": "443",
      "basePath": "/v2/",
      "authHeader": "Basic ZW138fTnZXJAZ21haWwu98wIOmZvb2JuU43heg==",
      "scheme": "https"
    }
  },
  "wb": {
    "v1": {
      "host": "files.osf.io",
      "port": "443",
      "basePath": "/v1/",
      "scheme": "https"
    }
  },
}
```

* Supported schemes for configuration file resource locations are:
    * `file:/`, e.g. `file://path/to/osf-client.json`
    * `classpath:/`, e.g. `classpath:/org/example/osf-client.json`
    * `classpath*:/`, e.g. `classpath*:/org/example/osf-client.json`
    * a resource location with no scheme will be interpreted as a classpath resource.  
     
* Since the most interesting things are revealed by the OSF v2 API while logged in, be sure to configure an `authHeader`, which will be sent on every request to the API.  You can do this by base64 encoding your login id concatenated to your password with a colon (on MacOS):
<pre>
    $ echo 'c3po@tatooine.com:excuseme' | base64
    YzNwb0B0YXRvb2luZS5jb206ZXhjdXNlbWUK
    $
</pre>

### Example Usage

#### Typical usage
```java
    RetrofitOsfServiceFactory factory = new RetrofitOsfServiceFactory();
    // default configuration resolves to classpath resource /org/dataconservancy/cos/osf/client/config/osf-client.json
    OsfService osfService = factory.getOsfService(OsfService.class);
```
#### Custom configuration resource
```java
    RetrofitOsfServiceFactory factory = new RetrofitOsfServiceFactory("file:///path/to/custom-client-config.json");
    OsfService osfService = factory.getOsfService(OsfService.class);
```
#### Usage with Spring

The OSF client comes with with pre-wired beans for use with Spring-based applications.  These beans are defined in the classpath resource [org/dataconservancy/cos/osf/client/config/applicationContext.xml](src/main/resources/org/dataconservancy/cos/osf/client/config/applicationContext.xml).  

Because the OSF client has dependencies on other Spring-enabled libraries, there are a number of XML-based application contexts to configure:
```java
ClassPathXmlApplicationContext cxt =
            new ClassPathXmlApplicationContext("classpath*:applicationContext.xml",
                    "classpath*:org/dataconservancy/config/applicationContext.xml",
                    "classpath*:org/dataconservancy/packaging/tool/ser/config/applicationContext.xml",
                    "classpath*:org/dataconservancy/cos/osf/client/config/applicationContext.xml",
                    "classpath:/org/dataconservancy/cos/packaging/config/applicationContext.xml");
```

Once you have an instance of an `ApplicationContext`, you ought to be able to retrieve an instance of the `OsfService` and make calls to it:
```java
OsfService osfService = cxt.getBean("osfService", OsfService.class);
Registration registration = osfService.registrationByUrl("https://api.osf.io/v2/registrations/0zqbo/").execute().body();
```
### Advanced Usages

#### Custom JSONAPIConverter
```java
    List<Class<?>> domainClasses = new ArrayList<>();
    // Add classes annotated with @Type, indicating their participation in the JSON-API Converter framework
    domainClasses.add(Foo.class);
    domainClasses.add(Bar.class);

    ObjectMapper mapper = new ObjectMapper();
    // Configure the Jackson ObjectMapper if you wish

    // Instantiate the ResourceConverter using the domain classes and ObjectMapper
    ResourceConverter resourceConverter = new ResourceConverter(mapper, domainClasses.toArray(new Class[]{}));

    // If you don't plan on resolving links encountered in JSON documents, you can skip the instantiation
    // and configuration of the global resolver.

    // Instantiate your favorite HTTP client.  It could be OkHttp or any other library.
    OkHttpClient httpClient = new OkHttpClient();

    // Add a global resolver implementation used by the ResourceConverter to resolve URLs encountered in
    // JSON documents
    resourceConverter.setGlobalResolver(relUrl -> {
      com.squareup.okhttp.Call req = httpClient.newCall(new Request.Builder().url(relUrl).build());
        try {
          return req.execute().body().bytes();
        } catch (IOException e) {
          throw new RuntimeException(e.getMessage(), e);
       }
    });

    // Finally instantiate the JSONAPIConverterFactory
    JSONAPIConverterFactory jsonApiConverterFactory = new JSONAPIConverterFactory(resourceConverter);

    RetrofitOsfServiceFactory factory = new RetrofitOsfServiceFactory(jsonApiConverterFactory);
    // default configuration resolves to /org/dataconservancy/cos/osf/client/config/osf-client.json
    OsfService osfService = factory.getOsfService(OsfService.class);
```