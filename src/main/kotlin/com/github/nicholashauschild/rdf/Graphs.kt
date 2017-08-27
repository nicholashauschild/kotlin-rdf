package com.github.nicholashauschild.rdf

import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.rdf.model.impl.PropertyImpl
import java.net.URI

/**
 * Author: nicholas.hauschild
 */

/**
 * DSL starting point for creating an rdf graph.  Uses the default
 * Model according to Apache Jena.
 */
fun rdfGraph(populatingFunction: ModelPopulator.() -> Unit): Model {
    val model = ModelFactory.createDefaultModel()
    return rdfGraphFrom(model, populatingFunction)
}

/**
 * Alias of rdfGraph
 */
fun rdfModel(populatingFunction: ModelPopulator.() -> Unit): Model {
    return rdfGraph(populatingFunction)
}

/**
 * DSL starting point for creating an rdf graph.  Uses the provided
 * Model to populate.
 */
fun rdfGraphFrom(model: Model, populatingFunction: ModelPopulator.() -> Unit): Model {
    val modelPopulator = ModelPopulator()
    populatingFunction(modelPopulator);
    return modelPopulator.populate(model)
}

/**
 * Alias of rdfGraphFrom
 */
fun rdfModelFrom(model: Model, populatingFunction: ModelPopulator.() -> Unit): Model {
    return rdfGraphFrom(model, populatingFunction)
}

class ModelPopulator {
    val resources: MutableMap<URI, PropertyMappings> = mutableMapOf()

    fun resource(uri: URI, infoGatherer: PropertyMappings.() -> Unit) {
        val propertyMappings = PropertyMappings()
        infoGatherer(propertyMappings)

        resources[uri] = propertyMappings
    }

    operator fun String.not(): URI {
        return URI(this)
    }

    internal fun populate(model: Model): Model {
        resources.forEach {
            val r = model.createResource(it.key.toString())
            it.value.mappings.forEach {
                r.addProperty(PropertyImpl(it.key.toString()), it.value)
            }
        }

        return model
    }
}

class PropertyMappings {
    val mappings: MutableMap<URI, String> = mutableMapOf()
    operator fun PropertySchema.invoke(key: String): UnmappedProperty {
        return UnmappedProperty(this.properties[key], mappings)
    }
}

class UnmappedProperty(val uri: URI?,
                       val mappings: MutableMap<URI, String>) {
    infix fun of(literal: String) {
        mappings[uri ?: throw RuntimeException("Unknown schema property")] = literal
    }
}