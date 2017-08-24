package com.github.nicholashauschild.rdf

import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.ModelFactory

/**
 * Author: nicholas.hauschild
 */
fun rdfGraph(buildingFunction: GraphBuilder.() -> Unit): Graph {
    val graphBuilder = GraphBuilder()
    buildingFunction(graphBuilder);
    return graphBuilder.build()
}

class Graph(val resources: List<Resource>) {
    private val model: Model = ModelFactory.createDefaultModel()

    init {
        resources.forEach {
            model.createResource(it.uri)
        }
    }

    fun getModel(): Model {
        return model
    }
}

class GraphBuilder {
    val resources: MutableList<Resource> = mutableListOf()

    operator fun Resource.unaryPlus() {
        resources.add(this)
    }

    fun resource(buildingFunction: ResourceBuilder.() -> Unit): Resource {
        val resourceBuilder = ResourceBuilder()
        buildingFunction(resourceBuilder);
        return resourceBuilder.build()
    }

    fun build() = Graph(resources)
}

class Resource(val uri: String)

class ResourceBuilder {
    var uri: String? = null

    operator fun String.not(): ResourceBuilder {
        uri = this
        return this@ResourceBuilder
    }

    fun build() = Resource(uri ?: throw IllegalArgumentException())
}

fun test() {
    val graph =
        rdfGraph {
            +resource {!"http://person/Doug"}
            +resource {!"http://animal/Dog"}
            +resource {!"http://book/Dug"}
        }
}