package MainPackage.API.Representation;

import MainPackage.API.Composants.Liaison.Arc;
import MainPackage.API.Composants.Liaison.Edge;
import MainPackage.API.Composants.Liaison.Relation;
import MainPackage.API.Composants.Autres.Vertex;
import MainPackage.API.Composants.Liaison.WeightedEdge;
import MainPackage.API.Exceptions.RelationMismatchException;
import java.util.LinkedList;

public interface AdjacencyMatrixBehaviour {
    public abstract void chargerVertexs(Vertex ... vertexs);
    public abstract void chargerRelations(Relation ... relations);
    
    public default boolean checkMismatch(Relation ... relations) throws RelationMismatchException {
        for(Relation r1: relations)
            for(Relation r2: relations)
                if((r1 instanceof Edge && r2 instanceof Arc) || 
                        (r1 instanceof Arc && r2 instanceof Edge))
                        return true;
        return false;
    }
    
    public default boolean checkMismatch(Vertex ... vertexs) throws RelationMismatchException {
        LinkedList<Relation> relations = new LinkedList<Relation>();
        
        for(Vertex vertex: vertexs) 
            relations.addAll(vertex.getListRelations());
        
        for(Relation r1: relations)
            for(Relation r2: relations)
                if((r1 instanceof Edge && r2 instanceof Arc) || 
                   (r1 instanceof Arc && r2 instanceof Edge))
                        return true;
        return false;
    }
}
