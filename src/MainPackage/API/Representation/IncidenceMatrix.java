package MainPackage.API.Representation;

import MainPackage.API.Composants.Liaison.Arc;
import MainPackage.API.Composants.Liaison.Edge;
import MainPackage.API.Composants.Liaison.Relation;
import MainPackage.API.Composants.Autres.Vertex;
import MainPackage.API.Composants.Liaison.WeightedArc;
import MainPackage.API.Composants.Liaison.WeightedEdge;
import MainPackage.API.Exceptions.RelationMismatchException;
import java.util.Iterator;
import java.util.TreeSet;
import javafx.beans.property.SimpleObjectProperty;

public class IncidenceMatrix implements AdjacencyMatrixBehaviour{
    
    private SimpleObjectProperty<TreeSet<Vertex>> verticesList = new SimpleObjectProperty<TreeSet<Vertex>>(null);
    private SimpleObjectProperty<TreeSet<Relation>> relationsList = new SimpleObjectProperty<TreeSet<Relation>>(null);
    private SimpleObjectProperty<Integer[][]> relationsMatrix = new SimpleObjectProperty<Integer[][]>(null);

    public IncidenceMatrix(Vertex ... vertexs) throws RelationMismatchException {
        if(this.checkMismatch(vertexs)) 
            throw new RelationMismatchException();
        this.chargerVertexs(vertexs);
    }
    
    public IncidenceMatrix(Relation ... relations) throws RelationMismatchException {
        if(this.checkMismatch(relations)) 
            throw new RelationMismatchException();
        this.chargerRelations(relations);
    }

    @Override
    public void chargerVertexs(Vertex... vertexs) {
        this.verticesList.setValue(new TreeSet<Vertex>());
        this.relationsList.setValue(new TreeSet<Relation>());
        
        // without duplicates
        for(Vertex vertex: vertexs){
            this.verticesList.getValue().add(vertex);
            
            for(Relation relation: vertex.getListRelations())
                this.relationsList.getValue().add(relation);
        }
        
        this.relationsMatrix.setValue(new Integer[this.verticesList.getValue().size()][this.relationsList.getValue().size()]);
        
        // we suppose that we don't have any relation initially and then we start constructing the matrix 
        for(int i = 0; i < this.relationsMatrix.getValue().length; i++) 
            for(int j = 0; j < this.relationsMatrix.getValue()[i].length; j++)
                this.relationsMatrix.getValue()[i][j] = new Integer(0); 
        
        int i = 0;
        
        // Algorithm consist of :browsing vertices 
        // and for each vertex ,find all the incident edges.
        // if incident => {1/-1}
        for(Vertex vertex: this.verticesList.getValue()){

            int j = 0;
            
            for(Relation relation: this.relationsList.getValue()){
                
                // check if the vertex is incident of the relation
                if(relation.getRightVertex().equals(vertex) || relation.getLeftVertex()
                        .equals(vertex)){
                    if(relation instanceof Edge || relation instanceof WeightedEdge) this.relationsMatrix.getValue()[i][j] = 1;
                    else if(relation instanceof Arc){
                        if(((Arc) relation).getLeftVertex().equals(vertex)) 
                            this.relationsMatrix.getValue()[i][j] = 1;
                        else this.relationsMatrix.getValue()[i][j] = -1;
                    }
                    else if(relation instanceof WeightedArc){
                        if(((WeightedArc) relation).getLeftVertex().equals(vertex)) 
                            this.relationsMatrix.getValue()[i][j] = 1;
                        else this.relationsMatrix.getValue()[i][j] = -1;
                    }
                }
                j++;
            }
            i++;
        }
    }

    @Override
    public void chargerRelations(Relation... relations) {
        this.verticesList.setValue(new TreeSet<Vertex>());
        this.relationsList.set(new TreeSet<Relation>());
        
        // relation & vertices without duplicates 
        for(Relation relation: relations)
            this.relationsList.getValue().add(relation);
        
        for(Relation relation: this.relationsList.getValue()){
            this.verticesList.getValue().add(relation.getRightVertex());
            this.verticesList.getValue().add(relation.getLeftVertex());
        }
        
        this.relationsMatrix.setValue(new Integer[this.verticesList.getValue().size()][this.verticesList.getValue().size()]);
         
        for(int i = 0; i < this.relationsMatrix.getValue().length; i++) 
            for(int j = 0; j < this.relationsMatrix.getValue()[i].length; j++)
                this.relationsMatrix.getValue()[i][j] = new Integer(0);
        
        int i = 0;

        for(Vertex vertex: this.verticesList.getValue()){
            
            int j = 0;
                        
            for(Relation relation: this.relationsList.getValue()){
                // check if a vertex is incident of a relation 
                if(relation.getRightVertex().equals(vertex) || relation.getLeftVertex().equals(vertex)) {
                    if(relation instanceof Edge || relation instanceof WeightedEdge) 
                        this.relationsMatrix.getValue()[i][j] = 1;
                    else if(relation instanceof Arc || relation instanceof WeightedArc){
                        if(relation.getLeftVertex().equals(vertex)) 
                            this.relationsMatrix.getValue()[i][j] = 1;
                        else this.relationsMatrix.getValue()[i][j] = -1;
                    }
                }
                j++;
            }
            i++;
        }
    }

   
	@Override
    public String toString(){
        String matrixDisplay = "";
        
        Vertex[] labelLines = new Vertex[this.verticesList.getValue().size()];
        
        Iterator<Relation> relationIterator = this.relationsList.getValue().iterator();
        Iterator<Vertex> vertexIterator = this.verticesList.getValue().iterator();
        
        for(int i = 0; relationIterator.hasNext() || vertexIterator.hasNext() ; i++){
            Relation relation = (relationIterator.hasNext() ? relationIterator.next() : null);
            Vertex vertex = (vertexIterator.hasNext() ? vertexIterator.next() : null);
            
            if(vertex != null) labelLines[i] = vertex;
            if(relation != null) matrixDisplay += "\t" + relation.getLabel();
        }
                
        for(int a = 0; a < this.relationsMatrix.getValue().length; a++){
            
            matrixDisplay += "\n" + labelLines[a].getLabel();
            
            for(int b = 0; b < this.relationsMatrix.getValue()[a].length; b++)
                matrixDisplay += "\t" + this.relationsMatrix.getValue()[a][b];
        }
        
        return matrixDisplay + "\n";
    }

	public TreeSet<Vertex> getVerticesList() {
		return verticesList.get();
	}

	public void setVerticesList(SimpleObjectProperty<TreeSet<Vertex>> verticesList) {
		this.verticesList = verticesList;
	}
	
	public TreeSet<Relation> getRelationsList() {
		return relationsList.get();
	}

	public void setRelationsList(SimpleObjectProperty<TreeSet<Relation>> relationsList) {
		this.relationsList = relationsList;
	}

	public Integer[][] getRelationsMatrix() {
		return relationsMatrix.get();
	}

	public void setRelationsMatrix(SimpleObjectProperty<Integer[][]> relationsMatrix) {
		this.relationsMatrix = relationsMatrix;
	}
}
