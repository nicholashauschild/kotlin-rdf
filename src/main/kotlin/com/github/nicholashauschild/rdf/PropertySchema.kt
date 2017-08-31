package com.github.nicholashauschild.rdf

import org.apache.jena.rdf.model.Property
import org.apache.jena.rdf.model.ResourceFactory

/**
 * Author: nicholas.hauschild
 */

/**
 * DSL starting point for creating a PropertySchema
 */
fun propertySchema(namespace: String, builderFunction: PropertySchemaBuilder.() -> Unit): PropertySchema {
    // validate namespace contains '{{property}}' placeholder
    validateNamespace(namespace)

    // builder setup
    val schemaBuilder = PropertySchemaBuilder(namespace)
    builderFunction(schemaBuilder)

    // build schema
    return schemaBuilder.build()
}

/**
 * Alias for 'propertySchema'
 */
fun pSchema(ns: String, builderFunction: PropertySchemaBuilder.() -> Unit)
        = propertySchema(ns, builderFunction)

/**
 * Class representing a property schema.  the output of the DSL starting
 * point of propertySchema
 */
class PropertySchema(val namespace: String,
                     private val properties: Map<String, Property>) {
    operator fun get(key: String): Property {
        return properties[key] ?: throw IllegalArgumentException("Unknown property: $key")
    }

    fun size(): Int {
        return properties.size
    }
}

/**
 * Receiver object for captureFunction provided to the propertySchema DSL functions.
 * This object defines further DSL functions that are useful for defining a property
 * schema.
 */
class PropertySchemaBuilder(val namespace: String,
                            val propertyMap: MutableMap<String, Property> = mutableMapOf()) {
    /**
     *
     */
    operator fun String.unaryPlus() {
        propertyMap[this] = PropertyBuilder(this, namespace).build()
    }

    /**
     *
     */
    operator fun String.invoke(builderFunction: PropertyBuilder.() -> Unit) {
        val propertyBuilder = PropertyBuilder(this, namespace)
        builderFunction(propertyBuilder)
        propertyMap[this] = propertyBuilder.build()
    }

    /**
     *
     */
    internal fun build() = PropertySchema(namespace, propertyMap)
}

/**
 *
 */
class PropertyBuilder(propName: String,
                      namespace: String) {
    var uri = namespace.replace("{{property}}", propName)

    /**
     *
     */
    internal fun build() = ResourceFactory.createProperty(uri)
}

/**
 * Base level PropertySchema exception
 */
open class PropertySchemaException(override val message: String): RuntimeException(message)

/**
 * To be used when a property that doesn't exist for a given Property Schema is requested.
 */
class UnknownPropertyException(override val message: String): PropertySchemaException(message)

/**
 *
 */
internal fun validateNamespace(namespace: String) {
    if(!namespace.contains("{{property}}")) {
        throw IllegalArgumentException(
                "Invalid namespace: $namespace; missing'{{property}}' placeholder")
    }
}