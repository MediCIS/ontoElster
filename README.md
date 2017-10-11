## (Sub)Ontology Extractor

OntoElster extract classes of an ontology including hierachies and properties.
Is suited to get a set of classes from external ontologies and include them
as import in a new ontology. OntoElster is inspired in [Ontofox](ontofox.hegroup.org)
but it can be used with ontologies that are not part of the OBO foundry.

#### Use

Build a jar an execute it as follows:

``` shell

> java -jar ontoElster-<version>.jar <ontology> <list of classes> [<reasoner>]

```

where:
- `<ontology>` is the URL or path of the target ontology
- `<list of classes>` is a text file containing the IRIs of selected classes (one per line)
- `<reasoner>` is an optional parameter to use a Ontology reasoner. Possible values are: `JFACT`, `HERMIT`, or `OPENLLET`

By default dependencies are not included in the `CLASSPATH`.

##### TODO

- Add a proper CLI
- Create self-executable JAR with dependencies
