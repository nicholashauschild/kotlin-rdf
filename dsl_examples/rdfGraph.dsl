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
                    props["enemies_with"] of !"cat"
                    props["hair_color"] of "golden"
                    props["leg_count"] of 4
                }

                "cat" {
                    props["enemies_with"] of !"parrot"
                    props["hair_color"] of "black"
                    props["leg_count"] of 4
                }

                "parrot" {
                    props["leg_count"] of 2
                }
            }
        }