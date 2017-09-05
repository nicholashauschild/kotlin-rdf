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
propertySchema("http://example/schema/{{property}}") {
    // add mapped property without any customization.
    // equivalent to the shorthand method mentioned below.
    "price" {}
    
    // add mapped property with customizations.
    "color" { uri = "http://sample/catalog/color" }
    
    // same behavior as the first method.  This one just
    // does not allow you to customize it yourself.
    +"count"
}
```

###### Aliasing properties
If a property name is too long, or you would like to have more options
regarding how it is referred within your graphs, then you can utilize
the alias keyword to create aliases for property names.

Example:
```
propertySchema("http://example/schema/{{property}}") {
    "price" {} alias "cost"
    "color" { uri = "http://sample/catalog/color" } alias "pigment?"
    +"count" alias "number"
}
```

In the above example, 'price' and 'cost' are two different names that refer to
the same property.


##### rdfGraph
The `rdfGraph` DSL is meant to create an RDF graph or model
that can then be queried against.

Example:
Please note this example uses the propertySchema DSL to illustrate
its usefulness.
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

## Questions
1. Why are you doing this? To learn how to make a DSL in Kotlin and to learn more about RDF.