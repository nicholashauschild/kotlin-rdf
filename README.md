# kotlin-rdf
[![Build Status](https://img.shields.io/travis/nicholashauschild/kotlin-rdf/master.svg?style=flat-square)](https://travis-ci.org/nicholashauschild/kotlin-rdf)

> RDF DSL's written in Kotlin

## What is it?
A series of DSL's to support creating and querying RDF Graphs.
This specific library is backed by Apache Jena.

## Usage

#### Add dependency
Release dependencies: Not yet released.  Still experimental.

Snapshot dependencies: Not yet released.  They will be at the following location soon though...
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

#### DSL's

##### propertySchema
The `propertySchema` DSL is used to setup a property or predicate 'namespace'.

Example:
```
pSchema {
    // the property 'function' is used to map a logical name to a URI.
    property("name") { !"http://something/property/name" }
    
    // the prop 'function' is an alias for the 'property' function.
    prop("age") { !"http://something/property/age" }
    
    // a short-hand means to map a logical name to a URI.
    +"height" from !"http://something/property/height"
}
```

##### rdfGraph
The `rdfGraph` DSL is meant to create an RDF graph or model
that can then be queried against.

Example:
Please note this example uses the propertySchema DSL to illustrate
its usefulness.
```
val schemaA = pdfSchema {
    ...
}

val schemaB = pdfSchema {
    ...
}

rdfGraph {
    // defines a resource, using a URI.
    resource(!"http://something/person/nick") {
        // uses a predicate of schemaA to map a predicate
        // to an object (literal this time), creating a triple for the model.
        schemaA("name") of "Nick"
        // uses a predicate of schemaB to map a predicate
        // to an object (literal this time), creating a triple for the model.
        schemaB("age") of "100"
    }
}
```

## Questions
1. Why are you doing this? To learn how to make a DSL in Kotlin and to learn more about RDF.
2. Why can't I create a triple with the Object being a Resource (or even a non-String literal)?  Because I haven't written that functionality yet.