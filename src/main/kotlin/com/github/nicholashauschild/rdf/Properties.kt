package com.github.nicholashauschild.rdf

import java.net.URI

/**
 * Author: nicholas.hauschild
 */

fun propertySchema(ns: String, populatingFunction: SchemaPopulator.() -> Unit): PropertySchema {
    val schemaPopulator = SchemaPopulator()
    populatingFunction(schemaPopulator)
    return PropertySchema(ns, schemaPopulator.properties);
}

fun pSchema(ns: String, populatingFunction: SchemaPopulator.() -> Unit): PropertySchema {
    return propertySchema(ns, populatingFunction)
}

data class PropertySchema(val ns: String,
                          val properties: Map<String, URI>)

class SchemaPopulator {
    val properties: MutableMap<String, URI> = mutableMapOf()

    fun property(key: String, uriRetriever: () -> URI) {
        properties[key] = uriRetriever()
    }

    fun prop(key: String, uriRetriever: () -> URI) {
        property(key, uriRetriever)
    }

    operator fun String.unaryPlus(): PropertyIntermediate {
        return PropertyIntermediate(this, properties)
    }

    operator fun String.not(): URI {
        return URI(this)
    }
}

class PropertyIntermediate(private val name: String,
                           private val properties: MutableMap<String, URI>) {
    infix fun from(uri: URI) {
        properties[name] = uri
    }
}