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
                +"leg_count" alias "lc" alias "l" alias "c"
                "tail_count" {
                    uri = "http://something_else/tail_count"
                } alias "tc"
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

    describe("the RdfGraph with statements using aliased predicates") {
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
                            pSchema["lc"] of 4
                            pSchema["tail_count"] of 1
                        }

                        "cat" {
                            pSchema["enemies_with"] of !"parrot"
                            pSchema["hair_color"] of "black"
                            pSchema["l"] of 4
                            pSchema["tc"] of 1
                        }

                        "parrot" {
                            pSchema["c"] of 2
                            pSchema["tc"] of 2
                        }
                    }
                }

        it("has 9 statements") {
            assertEquals(10, graph.size())
        }

        on("accessing resources with 'leg_count' property") {
            val legCountResources
                    = graph.listSubjectsWithProperty(pSchema["leg_count"]).toSet()

            it("contains 3 resources") {
                assertEquals(3, legCountResources.size)
            }

            it("contains dog") {
                assertTrue(legCountResources.map { it.uri }.contains("http://example/dog"))
            }

            it("contains cat") {
                assertTrue(legCountResources.map { it.uri }.contains("http://example/cat"))
            }

            it("contains parrot") {
                assertTrue(legCountResources.map { it.uri }.contains("http://example/parrot"))
            }
        }

        on("accessing objects with 'tail_count' property via 'tc' alias") {
            val tailCountWithObjects
                    = graph.listObjectsOfProperty(pSchema["tc"]).toSet()

            it("contains 2 resources") {
                assertEquals(2, tailCountWithObjects.size)
            }

            it("contains 1") {
                assertTrue(tailCountWithObjects.map { it.asLiteral().int }.contains(1))
            }

            it("contains 2") {
                assertTrue(tailCountWithObjects.map { it.asLiteral().int }.contains(2))
            }
        }
    }
})