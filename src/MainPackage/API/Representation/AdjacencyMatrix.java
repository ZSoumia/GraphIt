package MainPackage.API.Representation;

import MainPackage.API.Composants.Liaison.Arc;
import MainPackage.API.Composants.Liaison.Edge;
import MainPackage.API.Composants.Liaison.Relation;
import MainPackage.API.Composants.Autres.Vertex;
import MainPackage.API.Exceptions.RelationMismatchException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;
import javafx.beans.property.SimpleObjectProperty;

public class AdjacencyMatrix implements AdjacencyMatrixBehaviour{

    private SimpleObjectProperty<TreeSet<Vertex>> vertices = new SimpleObjectProperty<TreeSet<Vertex>>(null);
    private SimpleObjectProperty<TreeSet<Relation>> relations= new SimpleObjectProperty<TreeSet<Relation>>(null);
    private SimpleObjectProperty<boolean[][]> relationsMatrix= new SimpleObjectProperty<boolean[][]>(null);
    
    public AdjacencyMatrix(Vertex ... vertexs) throws RelationMismatchException {
        if(this.checkMismatch(vertexs)) 
            throw new RelationMismatchException();
        this.chargerVertexs(vertexs);
    }
    
    public AdjacencyMatrix(Relation ... relations) throws RelationMismatchException{
        if(this.checkMismatch(relations))
            throw new RelationMismatchException();
        this.chargerRelations(relations);
    }
    
    @Override
    public void chargerVertexs(Vertex... vertexs){
        this.vertices.setValue(new TreeSet<Vertex>());
        this.relations.setValue(new TreeSet<Relation>());
        
        // load all vertexes without duplicates
        for(Vertex vertex: vertexs)
            this.vertices.getValue().add(vertex);
                             
        this.relationsMatrix.setValue(new boolean[this.vertices.getValue().size()][this.vertices.getValue().size()]);
        
        // suppose we don't have any relation 
        for(int i = 0; i < this.relationsMatrix.getValue().length; i++) 
            for(int j = 0; j < this.relationsMatrix.getValue()[i].length; j++)
                this.relationsMatrix.getValue()[i][j] = false; 
       
        ArrayList<Vertex> vertexsWithoutDoubles = new ArrayList<Vertex>();
        
        for(Vertex vertex: this.vertices.getValue()) 
            vertexsWithoutDoubles.add(vertex);
        
        for(int i = 0; i < vertexsWithoutDoubles.size(); i++){
            Vertex vertex = vertexsWithoutDoubles.get(i);
            
           
            for(Relation relation: vertex.getListRelations()){
                
                
                int j = vertexsWithoutDoubles.indexOf((vertex.equals(relation.getRightVertex()) ? 
                                                       relation.getLeftVertex() : 
                                                       relation.getRightVertex()));
                try{
                   
                    if((relation instanceof Edge) || ((relation instanceof Arc) && relation.getLeftVertex().equals(vertex))){
                        this.relationsMatrix.getValue()[i][j] = true;
                        this.relations.getValue().add(relation);
                    }
                }
                catch(IndexOutOfBoundsException exp){}
            }
        }
    }

    @Override
    public void chargerRelations(Relation... relations) {
        this.vertices.setValue(new TreeSet<Vertex>());
        this.relations.set(new TreeSet<Relation>());
        
        
        for(Relation relation: relations)
            this.relations.getValue().add(relation);
        
        for(Relation relation: this.relations.getValue()){
            this.vertices.getValue().add(relation.getRightVertex());
            this.vertices.getValue().add(relation.getLeftVertex());
        }
        
        this.relationsMatrix.setValue(new boolean[this.vertices.getValue().size()][this.vertices.getValue().size()]);
        
       
        for(int i = 0; i < this.relationsMatrix.getValue().length; i++) 
            for(int j = 0; j < this.relationsMatrix.getValue()[i].length; j++)
                this.relationsMatrix.getValue()[i][j] = false;
                
        
        ArrayList<Vertex> vertexsWithoutDoubles = new ArrayList<Vertex>();
        
        for(Vertex vertex: this.vertices.getValue()) 
            vertexsWithoutDoubles.add(vertex);
        
        for(Relation relation: relations){
            Vertex leftVertex = relation.getLeftVertex();
            Vertex rightVertex = relation.getRightVertex();
            
            
            int i = vertexsWithoutDoubles.indexOf(leftVertex);
            int j = vertexsWithoutDoubles.indexOf(rightVertex);
            
            this.relationsMatrix.getValue()[i][j] = true;
            
            
            if(relation instanceof Edge) 
                this.relationsMatrix.getValue()[j][i] = true;
        }
    } 
    
    @Override
    public String toString(){
        String MatrixDisplay = "";
        
        Vertex[] labelColumns = new Vertex[this.vertices.getValue().size()];
        Iterator<Vertex> iterator = this.vertices.get().iterator();
        
        for(int i = 0; iterator.hasNext(); i++){
            Vertex vertex = iterator.next();
            MatrixDisplay += "\t" + vertex.getLabel();
            labelColumns[i] = vertex;
        }
        
        for(int a = 0; a < this.relationsMatrix.getValue().length; a++){
            
            MatrixDisplay += "\n" + labelColumns[a].getLabel();
            
            for(int b = 0; b < this.relationsMatrix.getValue()[a].length; b++)
                if(!this.relationsMatrix.getValue()[a][b])
                    MatrixDisplay += "\t0";
                else MatrixDisplay += "\t1";
        }
            
        return MatrixDisplay + "\n";
    }

	public TreeSet<Vertex> getVertices() {
		return vertices.get();
	}

	public void setVertices(SimpleObjectProperty<TreeSet<Vertex>> vertices) {
		this.vertices = vertices;
	}

	public TreeSet<Relation> getRelations() {
		return relations.get();
	}

	public void setRelations(SimpleObjectProperty<TreeSet<Relation>> relations) {
		this.relations = relations;
	}

	public boolean[][] getRelationsMatrix() {
		return relationsMatrix.get();
	}

	public void setRelationsMatrix(SimpleObjectProperty<boolean[][]> relationsMatrix) {
		this.relationsMatrix = relationsMatrix;
	}    
}
