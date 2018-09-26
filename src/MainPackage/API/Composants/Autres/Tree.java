package MainPackage.API.Composants.Autres;

import MainPackage.API.Composants.Liaison.Relation;
import MainPackage.API.Exceptions.RelationMismatchException;

public class Tree extends Graph{
    
    public Tree(String nameTree, Vertex ... vertexs) throws RelationMismatchException{
        super(nameTree, vertexs);
    }
    
    public Tree(String nameTree, Relation ... relations) throws RelationMismatchException{
        super(nameTree, relations);
    } 
}
