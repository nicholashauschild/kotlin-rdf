package com.github.nicholashauschild.rdf

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Author: nicholas.hauschild
 */
object RdfSpec : Spek({
    describe("the empty RdfGraph") {
        val graph =


                rdfGraph {  }


        on("getting the model") {
            val model = graph.getModel()

            it("contains no statements") {
                assertTrue(model.isEmpty)
            }
        }
    }

    describe("the one-resource RdfGraph") {
        val graph =


                rdfGraph {
                    +resource { !"http://person/nick" }
                }


        on("getting the model") {
            val model = graph.getModel()

            it("contains no statements") {
                assertTrue(model.isEmpty)
            }

            it("should contain a resource for 'http://person/nick'") {
                assertNotNull(model.getResource("http://person/nick"))
            }
        }
    }
})