package com.github.nicholashauschild.rdf

import java.net.URI

/**
 * Author: nicholas.hauschild
 */

/**
 * DSL starting point for creating a PropertySchema
 */
fun propertySchema(ns: String, captureFunction: PropertyCapturer.() -> Unit): PropertySchema {
    val propertyCapturer = PropertyCapturer()
    captureFunction(propertyCapturer)
    return PropertySchema(ns, propertyCapturer.properties)
}

/**
 * Alias for 'propertySchema'
 */
fun pSchema(ns: String, captureFunction: PropertyCapturer.() -> Unit): PropertySchema {
    return propertySchema(ns, captureFunction)
}

/**
 * Class representing a property schema.  the output of the DSL starting
 * point of propertySchema
 */
data class PropertySchema(val ns: String,
                          val properties: Map<String, URI>)

/**
 * Receiver object for captureFunction provided to the propertySchema DSL functions.
 * This object defines further DSL functions that are useful for defining a property
 * schema.
 */
class PropertyCapturer(val properties: MutableMap<String, URI> = mutableMapOf()) {
    /**
     * DSL function that supports creation of properties for the schema.
     */
    fun property(key: String, uriRetriever: () -> URI) {
        properties[key] = uriRetriever()
    }

    /**
     * Alias of 'property'
     */
    fun prop(key: String, uriRetriever: () -> URI) {
        property(key, uriRetriever)
    }

    /**
     * Operator overloading [+] -- Used to start a 'shorthand' builder
     * to define a property without using a function 'keyword'.
     */
    operator fun String.unaryPlus(): _UnmappedProperty {
        return _UnmappedProperty(this, properties)
    }

    /**
     * Operator overloading [!] -- Shorthand way to convert a String to a URI
     */
    operator fun String.not(): URI {
        return URI(this)
    }
}

/**
 * Class to support a 'builder' style in the DSL.  Provides infix function to
 * give a natural language way to describe a schema property
 */
class _UnmappedProperty(private val name: String,
                        private val properties: MutableMap<String, URI>) {
    /**
     * Infix 'from' -- assign provided URI into the propertyMapping for
     * given name.
     */
    infix fun from(uri: URI) {
        properties[name] = uri
    }
}

/**
 * Base level PropertySchema exception
 */
open class PropertySchemaException(override val message: String): RuntimeException(message)

/**
 * To be used when a property that doesn't exist for a given Property Schema is requested.
 */
class UnknownPropertyException(override val message: String): PropertySchemaException(message)