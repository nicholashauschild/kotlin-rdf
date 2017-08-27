val s = pSchema (ns) {
    ...
}

val r = pSchema (ns) {
    ...
}

rdfGraph {
    resource (!"http://something/person/me") {
        s(name) of "nick"
        r(name) of "nicholas"
    }
}