package com.github.nicholashauschild.rdf

import org.apache.jena.rdf.model.*
import java.net.URI

/**
 * Author: nicholas.hauschild
 */

/**
 * DSL starting point for creating an rdf graph.  Uses the default
 * Model according to Apache Jena [ModelFactory.createDefaultModel()].
 */
fun rdfGraph(fulfillFunction: ModelFulfiller.() -> Unit): Model {
    val model = ModelFactory.createDefaultModel()
    return rdfGraphFrom(model, fulfillFunction)
}

/**
 * Alias of 'rdfGraph'
 */
fun rdfModel(fulfillFunction: ModelFulfiller.() -> Unit): Model {
    return rdfGraph(fulfillFunction)
}

/**
 * DSL starting point for creating an rdf graph.  Uses the provided
 * Model to populate.
 */
fun rdfGraphFrom(model: Model, fulfillFunction: ModelFulfiller.() -> Unit): Model {
    val modelFulfiller = ModelFulfiller(model)
    fulfillFunction(modelFulfiller)
    return model
}

/**
 * Alias of 'rdfGraphFrom'
 */
fun rdfModelFrom(model: Model, fulfillFunction: ModelFulfiller.() -> Unit): Model {
    return rdfGraphFrom(model, fulfillFunction)
}

/**
 * Receiver object for the fillFunction provided to the rdfGraph DSL functions.
 * This object defines further DSL methods that can be utilized to 'fill' an
 * RdfModel or RdfGraph.
 */
class ModelFulfiller(val model: Model) {
    private var resourceMapping: MutableMap<String, Resource> = mutableMapOf()

    fun resources(gatherFunction: ResourcesGatherer.() -> Unit) {
        val resourcesGatherer = ResourcesGatherer()
        gatherFunction(resourcesGatherer)
        resourceMapping.putAll(resourcesGatherer.resources)
    }

    fun statements(gatherFunction: StatementGatherer.() -> Unit) {
        val statementGatherer = StatementGatherer(resourceMapping)
        gatherFunction(statementGatherer)
        model.add(statementGatherer.statements)
    }
}

class ResourcesGatherer(val resources: MutableMap<String, Resource> = mutableMapOf()) {
    operator fun String.invoke(uri: String, builderFunction: ResourceBuilder.() -> Unit = {}) {
        invokeAndBuild(this, builderFunction, { it.uri = uri })
    }

    private fun invokeAndBuild(name: String,
                               builderFunction: ResourceBuilder.() -> Unit,
                               doAfter: (ResourceBuilder) -> Unit = {}) {
        val resourceBuilder = ResourceBuilder()
        builderFunction(resourceBuilder)
        doAfter(resourceBuilder)
        resources[name] = resourceBuilder.build()
    }
}

class ResourceBuilder() {
    var uri: String? = null
    fun build() = ResourceFactory.createResource(uri
            ?: throw RuntimeException("No URI provided for resource"))
}

class StatementGatherer(val resourceMappings: Map<String, Resource>,
                        val statements: MutableList<Statement> = mutableListOf()) {
    operator fun String.not(): Resource {
        return resourceMappings[this]
                ?: throw IllegalArgumentException("Unknown resource: $this")
    }

//    operator fun Resource.invoke(builderFunction: StatementsBuilder.() -> Unit) {
//        val statementsBuilder = StatementsBuilder(this)
//        builderFunction(statementsBuilder)
//        statements.addAll(statementsBuilder.statements)
//    }

    operator fun String.invoke(builderFunction: StatementsBuilder.() -> Unit) {
        val resource = resourceMappings[this]
                ?: throw IllegalArgumentException("Unknown resource: $this")
        val statementsBuilder = StatementsBuilder(resource)
        builderFunction(statementsBuilder)
        statements.addAll(statementsBuilder.statements)
    }
}

class StatementsBuilder(val subject: Resource,
                        val statements: MutableList<Statement> = mutableListOf()) {
    infix fun Property.of(tripleObject: Any) {
        val obj = ResourceFactory.createTypedLiteral(tripleObject)
        val statement = ResourceFactory.createStatement(subject, this, obj)
        statements.add(statement)
    }
}