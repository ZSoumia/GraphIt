package MainPackage.API.Composants.Liaison;

import MainPackage.API.Composants.Autres.Vertex;

public class Edge extends Relation {
    public Edge(String label, Vertex leftVertex, Vertex rightVertex) {
        super(label, leftVertex, rightVertex);
    }
}
