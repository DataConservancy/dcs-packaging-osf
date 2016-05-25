## OSF Client

This is a prototype client for interating with the OSF v2 JSON-API.  It is very much a work in progress.

Right now the "client" is comprised of:
* a [Retrofit-based](http://square.github.io/retrofit/) interface `OsfService` in the `service` package
* a Java model
* various support classes
* a `TestClient` class which exercises the `OsfService`

Run the `TestClient` from Maven or from within the IDE.

Obtain an instance of `OsfService` and use the client:
<pre>
  RetrofitOsfServiceFactory factory = new RetrofitOsfServiceFactory();
  // default configuration resolves to /org/dataconservancy/cos/osf/client/config/osf-client.json
  OsfService osfService = factory.getOsfService(OsfService.class);
</pre>

### Installation

* Clone this repository
* Install the custom dependency, documented below
* Configure, documented below

### Things you can do

* Hack/run the `TestClient`
* Hack/run `NodeTest`
* Hack `OsfService` or create your own service interface
* Use the `RetrofitOsfServiceFactory` to instantiate an `OsfService` and begin to use the client

### Example Usage

#### Typical usage
<pre>
    RetrofitOsfServiceFactory factory = new RetrofitOsfServiceFactory();
    // default configuration resolves to /org/dataconservancy/cos/osf/client/config/osf-client.json
    OsfService osfService = factory.getOsfService(OsfService.class);
</pre>
#### Custom configuration resource
<pre>
    RetrofitOsfServiceFactory factory = new RetrofitOsfServiceFactory("custom-client-config.json");
    // custom-client-config.json resolved to /org/dataconservancy/cos/osf/client/config/custom-client-config.json
    OsfService osfService = factory.getOsfService(OsfService.class);
</pre>
#### Custom JSONAPIConverter
<pre>
    List&lt;Class&lt;?&gt;&gt; domainClasses = new ArrayList&lt;&gt;();
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
    resourceConverter.setGlobalResolver(relUrl -&gt; {
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
</pre>
Other configuration exercises are left to the reader.

### Configuration

* Create a file named `osf-client.json` and put it on your classpath under `/org/dataconservancy/cos/osf/client/config`
* Since the most interesting things are revealed by the OSF v2 API while logged in, be sure to configure an `authHeader`, which will be sent on every request to the API.  You can do this by base64 encoding your login id contatenated to your password with a colon (on MacOS):

> $ echo 'c3po@tatooine.com:excuseme' | base64
> YzNwb0B0YXRvb2luZS5jb206ZXhjdXNlbWUK
> $

```json
{
  "osf": {
    "v2": {
      "host": "192.168.99.100",
      "port": "8000",
      "basePath": "/v2/",
      "authHeader": "Basic ZW138fTnZXJAZ21haWwu98wIOmZvb2JuU43heg==",
      "scheme": "http"
    }
  },
  "wb": {
    "v1": {
      "host": "192.168.99.100",
      "port": "7777",
      "basePath": "/v1/",
      "scheme": "http"
    }
  },
}
```

### Dependencies

* [Custom build](https://github.com/emetsger/jsonapi-converter/tree/develop-wip) of the [JSON API Converter](https://github.com/jasminb/jsonapi-converter)
  * You'll need to checkout the custom branch https://github.com/emetsger/jsonapi-converter/tree/develop-wip and run `mvn install`
