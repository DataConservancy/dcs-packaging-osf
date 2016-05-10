## OSF Client

This is a prototype client for interating with the OSF v2 JSON-API.  It is very much a work in progress.

Right now the "client" is comprised of:
* a [Retrofit-based](http://square.github.io/retrofit/) interface `OsfService` in the `service` package
* a Java model
* various support classes
* a `TestClient` class which exercises the `OsfService`

Run the `TestClient` from Maven or from within the IDE.

### Installation

* Clone this repository
* Install the custom dependency, documented below
* Configure, documented below
* Hack/run the `TestClient`
* Hack `OsfService` or create your own service interface

### Configuration

* Create a file named `osf-client.json` and put it on your classpath

```json
{
  "v2": {
    "host": "192.168.99.100",
    "port": "8000",
    "basePath": "/v2/",
    "authHeader": "Basic ZW138fTnZXJAZ21haWwu98wIOmZvb2JuU43heg==",
    "scheme": "http"
  }
}
```

### Dependencies

* [Custom build](https://github.com/emetsger/jsonapi-converter/tree/develop-wip) of the [JSON API Converter](https://github.com/jasminb/jsonapi-converter)

** You'll need to checkout the custom branch https://github.com/emetsger/jsonapi-converter/tree/develop-wip and run `mvn install`
