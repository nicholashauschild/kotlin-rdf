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

            pSchema("something") {
                +"name" from !"http://something/name"
                +"age" from !"http://something/age"
            }

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

    describe("the RdfGraph with statements") {
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

        on("accessing the statement iterator") {
            val iterator = graph.listStatements()

            val statement0 = iterator.nextStatement()

            it("has a 0th subject") {
                assertEquals("http://something/person/nick", statement0.subject.uri)
            }

            it("has a 0th predicate") {
                assertEquals("http://something/age", statement0.predicate.uri)
            }

            it("has a 0th object (literal)") {
                assertEquals("100", statement0.`object`.asLiteral().string)
            }

            val statement1 = iterator.nextStatement()

            it("has a 1st subject") {
                assertEquals("http://something/person/nick", statement1.subject.uri)
            }

            it("has a 1st predicate") {
                assertEquals("http://something/name", statement1.predicate.uri)
            }

            it("has a 1st object (literal)") {
                assertEquals("Nick", statement1.`object`.asLiteral().string)
            }
        }
    }

    describe("the RdfGraph with split resource definition") {
        val graph =

                rdfGraph {
                    val nick = !"http://something/person/nick"

                    resource(nick) {
                        pSchema("age") of "100"
                    }

                    resource(nick) {
                        pSchema("name") of "Nick"
                    }
                }

        it("has 2 statements") {
            assertEquals(2, graph.size())
        }

        on("accessing the statement iterator") {
            val iterator = graph.listStatements()

            val statement0 = iterator.nextStatement()

            it("has a 0th subject") {
                assertEquals("http://something/person/nick", statement0.subject.uri)
            }

            it("has a 0th predicate") {
                assertEquals("http://something/name", statement0.predicate.uri)
            }

            it("has a 0th object (literal)") {
                assertEquals("Nick", statement0.`object`.asLiteral().string)
            }

            val statement1 = iterator.nextStatement()

            it("has a 1st subject") {
                assertEquals("http://something/person/nick", statement1.subject.uri)
            }

            it("has a 1st predicate") {
                assertEquals("http://something/age", statement1.predicate.uri)
            }

            it("has a 1st object (literal)") {
                assertEquals("100", statement1.`object`.asLiteral().string)
            }
        }
    }
})