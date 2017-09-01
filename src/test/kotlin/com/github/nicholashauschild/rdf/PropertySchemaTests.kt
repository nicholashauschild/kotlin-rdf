package com.github.nicholashauschild.rdf

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import kotlin.test.*

/**
 * Author: nicholas.hauschild
 */
object PropertySchemaSpec : Spek({
    describe("the empty Schema with a bad namespace") {
        it("fails namespace validation") {
            assertFailsWith<IllegalArgumentException> {
                pSchema("namespace") { }
            }
        }
    }

    describe("the empty schema with a valid namespace") {
        it("passes namespace validation") {
            pSchema("http://example.com/schema/{{property}}") { }
            assertTrue(true)
        }
    }

    describe("the empty schema") {
        val schema =

                pSchema("http://example.com/schema/{{property}}") { }

        it("has no properties") {
            assertEquals(0, schema.size())
        }
    }



    describe("the schema with properties") {
        val schema =

                pSchema("http://example.com/schema/{{property}}") {
                    "name" { }
                    "number" { uri = "http://sample.com/props/number" }
                    +"title"
                }

        it("has 3 properties") {
            assertEquals(3, schema.size())
        }

        it("contains a 'name' property") {
            assertNotNull(schema["name"])
        }

        on("accessing the name property") {
            val nameProperty = schema["name"]

            it("has the expected uri") {
                assertEquals("http://example.com/schema/name", nameProperty.uri)
            }
        }

        it("contains a 'number' property") {
            assertNotNull(schema["number"])
        }

        on("accessing the number property") {
            val numberProperty = schema["number"]

            it("has the expected uri") {
                assertEquals("http://sample.com/props/number", numberProperty.uri)
            }
        }

        it("contains a 'title' property") {
            assertNotNull(schema["title"])
        }

        on("accessing the name property") {
            val titleProperty = schema["title"]

            it("has the expected uri") {
                assertEquals("http://example.com/schema/title", titleProperty.uri)
            }
        }
    }

    describe("the schema with aliased properties") {
        val schema =

                pSchema("http://example.com/schema/{{property}}") {
                    "number" { uri = "http://sample.com/props/number" } alias "n"
                    +"title" alias "t"
                }

        on("accessing the number property") {
            val number = schema["number"]

            it("has the same reference as the 'n' property") {
                assertTrue(number === schema["n"])
            }
        }

        on("accessing the title property") {
            val title = schema["title"]

            it("has the same reference as the 't' property") {
                assertTrue(title === schema["t"])
            }
        }
    }
})
