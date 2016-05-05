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

### Dependencies

* [Custom build](https://github.com/emetsger/jsonapi-converter/tree/develop-wip) of the [JSON API Converter](https://github.com/jasminb/jsonapi-converter)
** You'll need to checkout the custom branch https://github.com/emetsger/jsonapi-converter/tree/develop-wip and run `mvn install`
