# OSF Java Client

This is a prototype client for interacting with the OSF v2 JSON-API.  It provides an API in the form of a Java interface for making HTTP calls to the OSF API.  Currently the client is read-only; writes to the OSF API are not supported.

The client is comprised of:

* a [Retrofit-based](http://square.github.io/retrofit/) interface `OsfService` in the `org.dataconservancy.cos.osf.client.service` package
    * This is the entrance point into the OSF API. 
* Spring wiring for those who wish to use Spring to configure or inject `OsfService` instances

It depends on the [core Java model](../osf-core/) which provides:
* a representation of the types and relationships presented by the OSF JSON-API
    * e.g., a `Node`, `Registration`, `User`, `Contributor`, etc.

# Installation

1) Express dependencies on the Maven artifacts in your pom:

```xml
<dependency>
  <groupId>org.dataconservancy.cos</groupId>
  <artifactId>osf-client-api</artifactId>
  <scope>compile</scope>
  <version>1.1.0-SNAPSHOT</version>
</dependency>

<dependency>
  <groupId>org.dataconservancy.cos</groupId>
  <artifactId>osf-client-impl</artifactId>
  <scope>runtime</scope>
  <version>1.1.0-SNAPSHOT</version>
</dependency>
``` 

Because our artifacts are not deployed to Maven Central, you will need to add a `repositories` element to your POM, telling Maven where to get the `osf-client-api` and `osf-client-impl` artifacts:
```xml
<repositories>
    <repository>
        <id>dc.maven.releases</id>
        <name>Data Conservancy Public Maven 2 Repository (releases)</name>
        <layout>default</layout>
        <url>http://maven.dataconservancy.org/public/releases/</url>
        <releases>
            <enabled>true</enabled>
        </releases>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
    </repository>

    <repository>
        <id>dc.maven.snapshots</id>
        <name>Data Conservancy Public Maven 2 Repository (snapshots)</name>
        <layout>default</layout>
        <url>http://maven.dataconservancy.org/public/snapshots/</url>
        <releases>
            <enabled>false</enabled>
        </releases>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>
```

2) Configure the client and store the configuration.  The default location of the configuration resource is `/org/dataconservancy/cos/osf/client/config/osf-client.json`, but this can be overridden by specifying an alternate location using the `osf.client.conf` system property.
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

# Example Usage

## Typical usage
```java
    RetrofitOsfServiceFactory factory = new RetrofitOsfServiceFactory();
    // default configuration resolves to classpath resource /org/dataconservancy/cos/osf/client/config/osf-client.json
    OsfService osfService = factory.getOsfService(OsfService.class);
```
## Custom configuration resource
```java
    RetrofitOsfServiceFactory factory = new RetrofitOsfServiceFactory("file:///path/to/custom-client-config.json");
    OsfService osfService = factory.getOsfService(OsfService.class);
```
## Usage with Spring

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
# Advanced Usages

## Custom JSONAPIConverter
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
# Known Issues

## Polymorphic relationships
Polymorphic relationships are not supported.  What do we mean by polymorphic relationships?  This is the case where we want a relationship (expressed as a `relationships` object in the JSON) to deserialize as a concrete Java subclass of an abstract type. 

Given a class hierarchy where two concrete classes extend a common base class:

    Bar -- extends --> AbstractFoo    
    Baz -- extends --> AbstractFoo
    
And a class that that wishes to express a relationship to `AbstractFoo`:

    @Type("Panda")
    public class Panda {
      @Id
      private String id;
      
      @Relationship(value = "foorel", resolve = "true", strategy = ResolutionStrategy.OBJECT)
      private AbstractFoo foo;
    }
    
And a JSON object with a relationship named `foorel` that may point to `Bar` or `Baz`:
    
    {
      "data": {
        "relationships": {
          "foorel": {
            "links": {
              "self": {
                "href": "http://example.com/foo/",
              }
            }
          }
        },
        "type": "Panda",
        "id": "1"
      }
    }

In our contrived example, the JSON object will be deserialized into an instance of `Panda`.  In our object model  `Panda` has a relationship to an object with a super type of `AbstractFoo`, but we do not know which class will be used, _a priori_ (e.g. prior to de-referencing the `foorel href` from JSON), therefore, the `foo` field is typed as `AbstractFoo`.
 
 However, the `jsonapi-converter` cannot dereference the `foorel` relationship (the content retrieved from `http://example.com/foo/`) into an abstract type.  It must have a concrete type, such as `Bar` or `Baz`.  It might be reasonable for the `jsonapi-converter` to introspect on the content retrieved from the `foorel` relationship, and determine the proper type, but at this juncture the `osf-client` does not support this (possibly because we use an older version of the `jsonapi-converter`).
  
The workaround is to _not_ deference the `foorel` relationship, but store the reference as a `String`, and have higer layers in the stack dereference the URL and handle any polymorphic requirements.  Our contrived `Panda` class would be updated to:

    @Type("Panda")
    public class Panda {
      @Id
      private String id;
      
      @Relationship(value = "foorel", resolve = "true", strategy = ResolutionStrategy.REF)
      private String foo;
    }
  
This is a concrete issue when dealing with OSF relationships that may point to `Registrations` or `Nodes`:

    Registration -- extends --> NodeBase
    Node ---------- extends --> NodeBase
    
And the JSON contains a relationship that may resolve to a `Node` or a `Registration` depending on the context.  For example, a Wiki pointing to it's node:

    {
        "data": [
            {
                "relationships": {
                    "node": {
                        "links": {
                            "related": {
                                "href": "http://localhost:8000/v2/registrations/sb4ec/",
                                "meta": {}
                            }
                        }
                    },
                    "user": {
                        "links": {
                            "related": {
                                "href": "http://localhost:8000/v2/users/3rty2/",
                                "meta": {}
                            }
                        }
                    },
                    "comments": {
                        "links": {
                            "related": {
                                "href": "http://localhost:8000/v2/registrations/sb4ec/comments/?filter=%5Btarget%5D=cz58v",
                                "meta": {}
                            }
                        }
                    }
                },
                "links": {
                    "info": "http://localhost:8000/v2/wikis/cz58v/",
                    "download": "http://localhost:8000/v2/wikis/cz58v/content/",
                    "self": "http://localhost:8000/v2/wikis/cz58v/"
                },
                "attributes": {
                    "kind": "file",
                    "name": "home",
                    ...
                    "size": 184
                },
                "type": "wikis",
                "id": "cz58v"
            }
        ],
        "links": {
            ...
            }
        }
    }
    
If this wiki JSON is part of a OSF Registration, then the `node` relationship will resolve to a Registration.  Likewise, if this is not a registered wiki, then the `node` relationship will resolve to a Node.  This forces us to use a `String` reference for the `node` relationship in the `Wiki` class.

This problem also appears when handling the `parent` relationship.  The `parent` of a child Registration will resolve to a Registration, but the `parent` of a child node will resolve to a Node.  This forces us to use a `String` reference for the `parent` relationship in the `NodeBase` class.