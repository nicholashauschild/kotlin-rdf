val props =

        pSchema("http://example/props/{{property}}") {
            +"enemies_with"
            +"hair_color"
            +"leg_count"
        }

val model =

        rdfGraph {
            resources {
                "dog"("http://example/dog")
                "cat"("http://example/cat")
                "parrot"("http://example/parrot")
            }

            statements {
                "dog" {
                    schema["enemies_with"] of !"cat"
                    schema["hair_color"] of "golden"
                    schema["leg_count"] of 4
                }

                "cat" {
                    schema["enemies_with"] of !"parrot"
                    schema["hair_color"] of "black"
                    schema["leg_count"] of 4
                }

                "parrot" {
                    schema["leg_count"] of 2
                }
            }
        }