val s = pSchema (ns) {
    ...
}

val r = pSchema (ns) {
    ...
}

rdfGraph {
    resource(!"http://something/person/nick") {
        s("name") of "Nick"
        r("age") of "100"
    }
}