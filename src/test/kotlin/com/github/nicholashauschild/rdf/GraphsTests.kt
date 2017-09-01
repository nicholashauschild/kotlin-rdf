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
    // shared property schema for all tests
    val pSchema =

            pSchema("http://something/{{property}}") {
                +"enemies_with"
                +"hair_color"
                +"leg_count"
            }

    describe("the empty RdfGraph") {
        val graph =

                rdfGraph {  }

        it("has no statements") {
            assertTrue(graph.isEmpty)
        }
    }

    describe("the RdfGraph with statements") {
        val graph =

                rdfGraph {
                    resources {
                        "dog"("http://example/dog")
                        "cat"("http://example/cat")
                        "parrot"("http://example/parrot")
                    }

                    statements {
                        "dog" {
                            pSchema["enemies_with"] of !"cat"
                            pSchema["hair_color"] of "golden"
                            pSchema["leg_count"] of 4
                        }

                        "cat" {
                            pSchema["enemies_with"] of !"parrot"
                            pSchema["hair_color"] of "black"
                            pSchema["leg_count"] of 4
                        }

                        "parrot" {
                            pSchema["leg_count"] of 2
                        }
                    }
                }

        it("has 7 statements") {
            assertEquals(7, graph.size())
        }

        on("accessing resources with 'hair_color' property") {
            val hairColorResources
                    = graph.listSubjectsWithProperty(pSchema["hair_color"]).toSet()

            it("contains 2 resources") {
                assertEquals(2, hairColorResources.size)
            }

            it("contains dog") {
                assertTrue(hairColorResources.map { it.uri }.contains("http://example/dog"))
            }

            it("contains cat") {
                assertTrue(hairColorResources.map { it.uri }.contains("http://example/cat"))
            }

            it("does not contain parrot") {
                assertFalse(hairColorResources.map { it.uri }.contains("http://example/parrot"))
            }
        }

        on("accessing objects with 'enemies_with' property") {
            val enemiesWithObjects
                    = graph.listObjectsOfProperty(pSchema["enemies_with"]).toSet()

            it("contains 2 resources") {
                assertEquals(2, enemiesWithObjects.size)
            }

            it("contains parrot") {
                assertTrue(enemiesWithObjects.map { it.asResource().uri }.contains("http://example/parrot"))
            }

            it("contains cat") {
                assertTrue(enemiesWithObjects.map { it.asResource().uri }.contains("http://example/cat"))
            }

            it("does not contain dog") {
                assertFalse(enemiesWithObjects.map { it.asResource().uri }.contains("http://example/dog"))
            }
        }
    }
})