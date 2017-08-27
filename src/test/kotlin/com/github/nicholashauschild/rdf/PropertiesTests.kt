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
object PropertySchemaSpec : Spek({
    describe("the empty Schema") {
        val schema =

                pSchema("namespace") { }

        on("accessing the properties") {
            val properties = schema.properties

            it("is empty") {
                assertTrue(properties.isEmpty())
            }
        }

        it("has the expected namespace") {
            assertEquals("namespace", schema.ns)
        }
    }

    describe("the schema with a property") {
        val schema =

                pSchema("namespace") {
                    property("name") { !"http://something/property/name" }
                }

        on("accessing the properties") {
            val properties = schema.properties

            it("is not empty") {
                assertFalse(properties.isEmpty())
            }
        }
    }

    describe("the schema with a prop") {
        val schema =

                pSchema("namespace") {
                    prop("name") { !"http://something/property/name" }
                }

        on("accessing the properties") {
            val properties = schema.properties

            it("is not empty") {
                assertFalse(properties.isEmpty())
            }
        }
    }

    describe("the schema with a shorthand prop") {
        val schema =

                pSchema("namespace"){
                    +"name" from !"http://something/property/name"
                }

        on("accessing the properties") {
            val properties = schema.properties

            it("is not empty") {
                assertFalse(properties.isEmpty())
            }
        }
    }
})
