package MainPackage.API.Composants.Liaison;

import MainPackage.API.Composants.Autres.Vertex;

public class Arc extends Relation {
    public Arc(String label, Vertex leftVertex, Vertex rightVertex) {
        super(label, leftVertex, rightVertex);
    }
}
