package com.github.nicholashauschild.rdf

import java.net.URI

/**
 * Author: nicholas.hauschild
 */

/**
 * DSL starting point for creating a PropertySchema
 */
fun propertySchema(ns: String, populatingFunction: SchemaPopulator.() -> Unit): PropertySchema {
    val schemaPopulator = SchemaPopulator()
    populatingFunction(schemaPopulator)
    return PropertySchema(ns, schemaPopulator.properties)
}

/**
 * Alias for propertySchema
 */
fun pSchema(ns: String, populatingFunction: SchemaPopulator.() -> Unit): PropertySchema {
    return propertySchema(ns, populatingFunction)
}

/**
 * Class representing a property schema.  the output of the DSL starting
 * point of propertySchema
 */
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

/**
 * Base level PropertySchema exception
 */
open class PropertySchemaException(override val message: String): RuntimeException(message)

/**
 * To be used when a property that doesn't exist for a given Property Schema is requested.
 */
class UnknownPropertyException(override val message: String): PropertySchemaException(message)