package com.github.nicholashauschild.rdf

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Author: nicholas.hauschild
 */
object RdfSpec : Spek({
    describe("the empty RdfGraph") {
        val graph =

                rdfGraph {  }

        on("accessing the iterator") {
            val iterator = graph.listObjects()

            it("has no elements") {
                assertFalse(iterator.hasNext())
            }
        }

        it("has no statements") {
            assertTrue(graph.isEmpty)
        }
    }

    describe("the populated RdfGraph") {
        val pSchema =

                pSchema("something") {
                    +"name" from !"http://something/name"
                    +"age" from !"http://something/age"
                }

        val graph =

                rdfGraph {
                    resource(!"http://something/person/nick") {
                        pSchema("name") of "Nick"
                        pSchema("age") of "100"
                    }
                }

        it("has 2 statements") {
            assertEquals(2, graph.size())
        }

        on("accessing the iterator") {
            val iterator = graph.listObjects()

            it("has next element") {
                assertTrue(iterator.hasNext())
            }
        }
    }
})