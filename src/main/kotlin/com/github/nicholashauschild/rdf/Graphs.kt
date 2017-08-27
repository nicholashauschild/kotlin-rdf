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
 * Model according to Apache Jena [ModelFactory.createDefaultModel()].
 */
fun rdfGraph(fillFunction: ModelFiller.() -> Unit): Model {
    val model = ModelFactory.createDefaultModel()
    return rdfGraphFrom(model, fillFunction)
}

/**
 * Alias of rdfGraph
 */
fun rdfModel(fillFunction: ModelFiller.() -> Unit): Model {
    return rdfGraph(fillFunction)
}

/**
 * DSL starting point for creating an rdf graph.  Uses the provided
 * Model to populate.
 */
fun rdfGraphFrom(model: Model, fillFunction: ModelFiller.() -> Unit): Model {
    val modelFiller = ModelFiller()
    fillFunction(modelFiller);
    return modelFiller.fill(model)
}

/**
 * Alias of rdfGraphFrom
 */
fun rdfModelFrom(model: Model, fillFunction: ModelFiller.() -> Unit): Model {
    return rdfGraphFrom(model, fillFunction)
}

class ModelFiller(val resourceFillers: MutableMap<URI, ResourceFiller> = mutableMapOf()) {
    fun resource(uri: URI, fillFunction: ResourceFiller.() -> Unit) {
        val resourceFiller = ResourceFiller()
        fillFunction(resourceFiller)

        resourceFillers[uri] = resourceFiller
    }

    operator fun String.not(): URI {
        return URI(this)
    }

    internal fun fill(model: Model): Model {
        resourceFillers.forEach {
            val r = model.createResource(it.key.toString())
            it.value.propertyMapping.forEach {
                r.addProperty(PropertyImpl(it.key.toString()), it.value)
            }
        }

        return model
    }
}

class ResourceFiller(val propertyMapping: MutableMap<URI, String> = mutableMapOf()) {
    operator fun PropertySchema.invoke(key: String): _UnmappedPropertyMapper {
        return _UnmappedPropertyMapper(this.properties, key, propertyMapping)
    }
}

class _UnmappedPropertyMapper(private val schemaProperties: Map<String, URI>,
                              private val key: String,
                              private val propertyMapping: MutableMap<URI, String>) {
    infix fun of(literal: String) {
        val uri = schemaProperties[key] ?: throw UnknownPropertyException(key)
        propertyMapping[uri] = literal
    }
}