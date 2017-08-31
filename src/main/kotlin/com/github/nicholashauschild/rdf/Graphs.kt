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
 * Receiver object for the fulfillFunction provided to the rdfGraph DSL functions.
 * This object defines further DSL methods that can be utilized to 'fulfill' an
 * RdfModel or RdfGraph.
 */
class ModelFulfiller(val model: Model) {
    private var resourceMapping: MutableMap<String, Resource> = mutableMapOf()

    /**
     * This DSL style function allows for populating the resourceMapping
     * of the enclosing ModelFulfiller class.  The resourceMappings are
     * pulled from Receiver object ResourcesGatherer that is provided to
     * the parameter 'gatherFunction'.
     */
    fun resources(gatherFunction: ResourcesGatherer.() -> Unit) {
        val resourcesGatherer = ResourcesGatherer()
        gatherFunction(resourcesGatherer)
        resourceMapping.putAll(resourcesGatherer.resources)
    }

    /**
     * This DSL style function allows for populating the Model with statment
     * objects.  The statements are pulled from Receiver object
     * StatementGatherer that is provided to the parameter 'gatherFunction'.
     */
    fun statements(gatherFunction: StatementGatherer.() -> Unit) {
        val statementGatherer = StatementGatherer(resourceMapping)
        gatherFunction(statementGatherer)
        model.add(statementGatherer.statements)
    }
}

/**
 * Receiver object for the gatherFunction provided to the resources DSL functions.
 * This object defines further DSL methods that can be utilized to 'gather' resources
 * to be made available for later statement creation.
 */
class ResourcesGatherer(val resources: MutableMap<String, Resource> = mutableMapOf()) {
    /**
     * DSL Syntax function to map Receiver String to a Resource built by the ResourceBuilder
     * receiver object that is provided to the builderFunction parameter.
     */
    operator fun String.invoke(uri: String, builderFunction: ResourceBuilder.() -> Unit = {}) {
        val resourceBuilder = ResourceBuilder()
        builderFunction(resourceBuilder)
        resourceBuilder.uri = uri
        resources[this] = resourceBuilder.build()
    }
}

/**
 * Receiver object for the builderFunction provided to the String.invoke DSL Syntax
 * function.  Instances of this object can produce Jena Resource Objects.
 */
class ResourceBuilder() {
    var uri: String? = null

    /**
     * Build a Jena Resource object with the properties of this instance.
     */
    fun build(): Resource = ResourceFactory.createResource(uri
            ?: throw RuntimeException("No URI provided for resource"))
}

/**
 * Receiver object for the gatherFunction provided to the statements DSL functions.
 * This object defines further DSL methods that can be utilized to 'gather' statements
 * to be put into the Model.
 */
class StatementGatherer(val resourceMappings: Map<String, Resource>,
                        val statements: MutableList<Statement> = mutableListOf()) {
    /**
     * Shorthand mechanism for referring to a Resource by mapped name.
     */
    operator fun String.not(): Resource {
        return resourceMappings[this]
                ?: throw IllegalArgumentException("Unknown resource: $this")
    }

//    operator fun Resource.invoke(builderFunction: StatementsBuilder.() -> Unit) {
//        val statementsBuilder = StatementsBuilder(this)
//        builderFunction(statementsBuilder)
//        statements.addAll(statementsBuilder.statements)
//    }

    /**
     * DSL Syntax function to add Statements for a Resource.  Statements are built using
     * the Resource identified by the receiver object as the Subject, and the Predicate
     * and Object determined by the StatementsBuilder receiver object provided to the
     * builderFunction parameter.
     */
    operator fun String.invoke(builderFunction: StatementsBuilder.() -> Unit) {
        val resource = resourceMappings[this]
                ?: throw IllegalArgumentException("Unknown resource: $this")
        val statementsBuilder = StatementsBuilder(resource)
        builderFunction(statementsBuilder)
        statements.addAll(statementsBuilder.statements)
    }
}

/**
 * Receiver object for the builderFunction provided to the String.invoke DSL Syntax
 * function.  Instances of this object can produce multiple Jena Statement Objects.
 */
class StatementsBuilder(val subject: Resource,
                        val statements: MutableList<Statement> = mutableListOf()) {
    /**
     * DSL Syntax function to give a 'natural language' touch to mapping a predicate/object
     * with the provided subject (Resource).
     */
    infix fun Property.of(tripleObject: Any) {
        val obj = ResourceFactory.createTypedLiteral(tripleObject)
        val statement = ResourceFactory.createStatement(subject, this, obj)
        statements.add(statement)
    }
}