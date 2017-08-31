propertySchema("http://example/schema/{{property}}") {
                // namespace uri for property schema
                // requires {{property}} string for building
                // proper uri

    "price" {}
                // generate resource, typical additive mechanism,
                // this will be more useful if more properties are necessary.

    "color" { uri = "http://sample/catalog/color" }
                // generate resource, typical addiitive mechanism,
                // using var override for 'uri'.

    +"count"
                // generate resource, shorthand additive mechanism,
                // this is for adding a resource with no (or few) non-default
                // properties.
}