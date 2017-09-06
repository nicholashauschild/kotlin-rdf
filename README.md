# kotlin-rdf
[![Build Status](https://img.shields.io/travis/nicholashauschild/kotlin-rdf/master.svg?style=flat-square)](https://travis-ci.org/nicholashauschild/kotlin-rdf)

> RDF DSL's written in Kotlin

## What is it?
A series of DSL's to support creating and querying RDF Graphs.
This specific library is backed by Apache Jena.

## Usage

### Add dependency
Release dependencies: Not yet released.  Still experimental.

Snapshot dependencies:
```
repositories {
    maven {
        url uri('https://oss.jfrog.org/artifactory/libs-snapshot')
    }
}
dependencies {
    compile "com.github.nicholashauschild:kotlin-rdf:0.1.0-SNAPSHOT"
}
```

### DSL's

#### propertySchema
The `propertySchema` DSL is used to setup a property or predicate 'namespace'.

Here is an example that showcases the complete set of options available for creating a PropertySchema:
```
propertySchema("http://example/schema/{{property}}") {
    "price" {
        uri = "http://example/schema/price"
    }
}
```

...and here is a breakdown, mostly line-by-line, of what is happening...

`propertySchema("http://example/schema/{{property}}") {`

This line is doing two things.
1. It is establishing the start of the propertySchema DSL construct
2. It is providing a value for the propertySchema's namespace.

The namespace is useful for providing a default uri template, which will
allow us to remove some superfluous configuration.

***

`    "price" {`

This line is doing two things.
1. It is providing a common name for a new property.
2. It is establishing the start of the definition for the new property.

***

`        uri = "http://example/schema/price"`

This line is defining the URI for the enclosing property.

***

The last two lines are closing their respective constructs.

##### Property Configuration
Here is a table with the configuration options available for a property:

| Field | Required | Description           | Default Value |
| ----- | -------- | --------------------- | ------------- |
| uri   | false    | URI for this property | 'common name' merged into namespace template

##### Aliasing properties
If a property name is too long, or you would like to be allowed to refer
to it with additional names, then you can utilize
the `alias` keyword to create aliases for properties.

Example:
```
propertySchema("http://example/schema/{{property}}") {
    "color" { uri = "http://example/schema/color" } alias "pigment"
}
```

In the above example, 'color' and 'pigment' are two different names that refer to
the same property.

##### propertySchema return type
The `propertySchema` DSL returns an object of type `PropertySchema`.  This object
has a function with signature `operator fun get(name: String): Property` which can be used to access the
underlying property objects, which are implementations of the Property interface of the Apache Jena API.

```
val schema =
    propertySchema("http://example/schema/{{property}}") {
        "height" { uri = "http://example/schema/height" }
    }
    
val aProperty: org.apache.jena.rdf.model.Property = schema["height"]
assertEquals("http://example/schema/height", aProperty.getURI())
```

##### Reducing ceremonious syntax
The propertySchema definition shown at the beginning of this section can be written up
a bit more succinctly.

In general, the idea behind the namespace template is to be able to use a common 'base' URI
and derive the actual URI for each property from this template based on its name.  This is the
default behavior of the property definition.  With this information, we can rewrite our initial
propertySchema DSL definition like this, and we would get an equivalent result.

```
propertySchema("http://example/schema/{{property}}") {
    "price" {} // uri is the value of the merged namespace template and property name
}
```

Pairing this with the `alias` keyword, and you utilize whatever 'common name' for a property
while still keeping the definition concise.  For example

```
propertySchema("http://example/schema/{{property}}") {
    "some_silly_uri_prefix#price" {} alias "friendlyName"
}
```

Going even further, you can use the unary plus operator to add a property that will provide no
configuration outside of default values.

```
propertySchema("http://example/schema/{{property}}") {
    +"price"
}
```

*It is worth noting that these two variations may look the same now, but future versions of
this library will likely utilize further customization of a property.  The unary plus operator
will be creating a property with NO CUSTOMIZATION whatsoever, where the former syntax will
allow for a pick/choose type of customization*


#### rdfGraph
The `rdfGraph` DSL is meant to create an RDF graph or model
that can then be queried against.

Example:

*Note* this example uses the `propertySchema` DSL to illustrate
its usefulness.

*Note* this example uses shortcut mechanism's for the `propertySchema` DSL.  See 'Reducing the ceremonious syntax' for more details.

```
val props =

        pSchema("http://example/props/{{property}}") {
            +"enemies_with"
            +"hair_color"
            +"leg_count"
        }

val model =

        rdfGraph {
            resources {
                //resources are created and able to be referenced by name at a later time.
                "dog"("http://example/dog")
                "cat"("http://example/cat")
                "parrot"("http://example/parrot")
            }

            statements {
                //referring to resources by shorthand name, and creating property
                //mappings for this name.
                "dog" {
                    props["enemies_with"] of !"cat"
                    props["hair_color"] of "golden"
                    props["leg_count"] of 4
                }

                "cat" {
                    props["enemies_with"] of !"parrot"
                    props["hair_color"] of "black"
                    props["leg_count"] of 4
                }

                "parrot" {
                    props["leg_count"] of 2
                }
            }
        }
```

##### rdfGraph return type
The `rdfGraph` DSL will return an object of type `org.apache.jena.rdf.model.Model` of the Apache Jena API.

```
val model =
    rdfGraph {
        // ...
    }
    
val numStatements = model.size()
```

## Questions
1. Why are you doing this? To learn how to make a DSL in Kotlin and to learn more about RDF.
2. Why doesn't this support feature x/y/z?  I am new to RDF, and so my understanding of it is limited.  If you have any requests, please let me know via email, or via the github issue system.  Please note that a feature request is NOT a guarantee that I will implement something.