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
    /**
     * Get operator that will throw IllegalArgumentException rather than
     * return a Nullable type.
     */
    operator fun get(key: String): Property {
        return properties[key] ?: throw UnknownPropertyException("Unknown property: $key")
    }

    /**
     * return number of properties in the schema
     */
    fun size(): Int {
        return properties.size
    }

    fun containsKey(key: String): Boolean {
        return properties.containsKey(key)
    }
}

/**
 * Receiver object for builderFunction provided to the propertySchema DSL functions.
 * This object defines further DSL functions that are useful for defining a property
 * schema.
 */
class PropertySchemaBuilder(val namespace: String,
                            val propertyMap: MutableMap<String, Property> = mutableMapOf()) {
    /**
     * Shorthand mechanism for adding a property and mapping it in the schema.
     * No customization of the property is allowed with this setup.
     */
    operator fun String.unaryPlus(): Property {
        val property = PropertyBuilder(this, namespace).build()
        propertyMap[this] = property
        return property;
    }

    /**
     * Add an alias to a property, making the property available via the
     * schema under the alias text.  Returns property, so it can be chained.
     */
    infix fun Property.alias(alias: String): Property {
        propertyMap[alias] = this
        return this;
    }

    /**
     * 'Traditional' approach of using the builderFunction and the PropertyBuilder
     * receiver object to allow for full customization of properties.
     */
    operator fun String.invoke(builderFunction: PropertyBuilder.() -> Unit): Property {
        val propertyBuilder = PropertyBuilder(this, namespace)
        builderFunction(propertyBuilder)
        val property = propertyBuilder.build()
        propertyMap[this] = property
        return property
    }

    /**
     * Build a PropertySchema object with the properties of this instance.
     */
    internal fun build() = PropertySchema(namespace, propertyMap)
}

/**
 * Receiver object that is provided to the builderFunction that is capable
 * of customizing a Property prior to building it.
 */
class PropertyBuilder(propName: String,
                      namespace: String) {
    var uri = namespace.replace("{{property}}", propName)
    var alias: String? = null

    /**
     * Build a Property object with the properties of this instance.
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
 * Ensures that provided namespace contains the '{{property}}' placeholder string
 * to allow for proper URI generation.
 */
internal fun validateNamespace(namespace: String) {
    if(!namespace.contains("{{property}}")) {
        throw IllegalArgumentException(
                "Invalid namespace: $namespace; missing'{{property}}' placeholder")
    }
}