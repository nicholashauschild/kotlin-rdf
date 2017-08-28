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
 * Alias of 'rdfGraph'
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
    fillFunction(modelFiller)
    return modelFiller.fill(model)
}

/**
 * Alias of 'rdfGraphFrom'
 */
fun rdfModelFrom(model: Model, fillFunction: ModelFiller.() -> Unit): Model {
    return rdfGraphFrom(model, fillFunction)
}

/**
 * Receiver object for the fillFunction provided to the rdfGraph DSL functions.
 * This object defines further DSL methods that can be utilized to 'fill' an
 * RdfModel or RdfGraph.
 */
class ModelFiller(val resourceFillers: MutableMap<URI, ResourceFiller> = mutableMapOf()) {
    /**
     * DSL function that supports creation of resources for the wrapping model.
     */
    fun resource(resourceUri: URI, fillFunction: ResourceFiller.() -> Unit) {
        val resourceFiller = ResourceFiller()
        fillFunction(resourceFiller)
        resourceFillers[resourceUri] = resourceFiller
    }

    /**
     * Operator overloading [!] -- Shorthand way to convert a String to a URI
     */
    operator fun String.not(): URI {
        return URI(this)
    }

    /**
     * Function that will 'fill' the provided Model, once the DSL has been processed.
     */
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

/**
 * Receiver object for the fillFunction provided to the resource DSL functions.
 * This object defines further DSL methods that can be utilized to 'fill' a
 * Resource with properties.
 */
class ResourceFiller(val propertyMapping: MutableMap<URI, String> = mutableMapOf()) {
    /**
     * Operator overloading [invoke] -- Create supporting builder class, providing it with the
     * predicate to use for mapping a resource/literal to, as well as the propertyMappings
     * to be 'filled'
     */
    operator fun PropertySchema.invoke(key: String): _UnmappedPropertyMapper {
        val predicateUri = this.properties[key] ?: throw UnknownPropertyException(key)
        return _UnmappedPropertyMapper(predicateUri, propertyMapping)
    }
}

/**
 * Class to support a 'builder' style in the DSL.  Provides infix function
 * to give a natural language way to describe property mappings for a resource.
 */
class _UnmappedPropertyMapper(private val predicateUri: URI,
                              private val propertyMapping: MutableMap<URI, String>) {
    /**
     * Infix 'of' -- assign provided literal value into the propertyMapping for
     * given predicate URI.
     */
    infix fun of(literal: String) {
        propertyMapping[predicateUri] = literal
    }
}