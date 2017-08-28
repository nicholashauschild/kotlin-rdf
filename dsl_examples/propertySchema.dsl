pSchema("namespace") {
    property("name") { !"http://something/property/name" }
    prop("age") { !"http://something/property/age" }
    +"height" from !"http://something/property/height"
}