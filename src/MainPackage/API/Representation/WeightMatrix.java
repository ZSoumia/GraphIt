package MainPackage.API.Representation;

import MainPackage.API.Composants.Liaison.Relation;
import MainPackage.API.Composants.Autres.Vertex;
import MainPackage.API.Composants.Liaison.WeightedArc;
import MainPackage.API.Composants.Liaison.WeightedEdge;
import MainPackage.API.Exceptions.RelationMismatchException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;
import javafx.beans.property.SimpleObjectProperty;

public class WeightMatrix implements AdjacencyMatrixBehaviour{
    
    private SimpleObjectProperty<TreeSet<Vertex>> vertices = new SimpleObjectProperty<TreeSet<Vertex>>(null);
    private SimpleObjectProperty<TreeSet<Relation>> relations = new SimpleObjectProperty<TreeSet<Relation>>(null);
    private SimpleObjectProperty<Double[][]> relationsMatrix = new SimpleObjectProperty<Double[][]>(null);
    
    public WeightMatrix(Vertex ... vertexs) throws RelationMismatchException {
        if(this.checkMismatch(vertexs)) 
            throw new RelationMismatchException();
        this.chargerVertexs(vertexs);
    }
    
    public WeightMatrix(Relation ... relations) throws RelationMismatchException{
        if(this.checkMismatch(relations))
            throw new RelationMismatchException();
        this.chargerRelations(relations);
    }

    @Override
    public void chargerVertexs(Vertex ... vertexs) {
        this.vertices.setValue(new TreeSet<Vertex>());
        this.relations.setValue(new TreeSet<Relation>());
        
        // load vertices without duplicates 
        for(Vertex vertex: vertexs)
            this.vertices.getValue().add(vertex);
                             
        this.relationsMatrix.setValue(new Double[this.vertices.getValue().size()][this.vertices.getValue().size()]);
        
        //suppose we don't have any relation and we start construction
        for(int i = 0; i < this.relationsMatrix.getValue().length; i++) 
            for(int j = 0; j < this.relationsMatrix.getValue()[i].length; j++)
                this.relationsMatrix.getValue()[i][j] = null; 
                
        ArrayList<Vertex> vertexsWithoutDoubles = new ArrayList<Vertex>();
        
        for(Vertex vertex: this.vertices.getValue()) 
            vertexsWithoutDoubles.add(vertex);
            
        for(int i = 0; i < vertexsWithoutDoubles.size(); i++){
            Vertex vertex = vertexsWithoutDoubles.get(i);
            
            // for any relation i know that one of its positions 
            // is the i'th line (because i only collect the relations list 
            // of the i'th  vertex !) => find the j position                    
            for(Relation relation: (vertex.getListRelations())){
                
                // j position is  the index of the other side (the other vertex of the relation )
                //<=> the left vertex 
                // or the right on ,but not the  i-th  !!! 
                int j = vertexsWithoutDoubles.indexOf((vertex.equals(relation.getRightVertex()) ? 
                                                       relation.getLeftVertex() : 
                                                       relation.getRightVertex()));
                try{
                    /*
                        -Imagine the relation  (A,B): left: A; right: B;
                        the algo will insert 2 times  : once when we interact for 
                         vertex A , and the other one for vertex B;
                        
                        -and this is not permitted when we have an oriented graph
                        
                        - Solution =>  to insert  2 times:
                           
                            1)- we must have a  GNO (so Edges )
                            2)- or have an  Arc from  A -> B to insert in the line A  
                                    or an  Arc B -> A to insert in the line  B,
                                    so we test the left  vertex to know  !
                    */
                    if((relation instanceof WeightedEdge)){
                        this.relationsMatrix.getValue()[i][j] = ((WeightedEdge) relation).getWeight();
                        this.relationsMatrix.getValue()[j][i] = ((WeightedEdge) relation).getWeight();
                        this.relations.getValue().add(relation);
                    }
                    else if((relation instanceof WeightedArc) && relation.getLeftVertex().equals(vertex)){
                        this.relationsMatrix.getValue()[i][j] = ((WeightedArc)relation).getWeight();
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
        
        // On forme notre list de vertexs (sans doublant)
        for(Relation relation: relations){
            this.vertices.getValue().add(relation.getRightVertex());
            this.vertices.getValue().add(relation.getLeftVertex());
        }
        
        this.relationsMatrix.setValue(new Double[this.vertices.getValue().size()][this.vertices.getValue().size()]);
        
        // we suppose there's no relations and we start constructing 
        for(int i = 0; i < this.relationsMatrix.getValue().length; i++) 
            for(int j = 0; j < this.relationsMatrix.getValue()[i].length; j++)
                this.relationsMatrix.getValue()[i][j] = null; 
       
        ArrayList<Vertex> vertexsWithoutDoubles = new ArrayList<Vertex>();
        
        for(Vertex vertex: this.vertices.getValue()) 
            vertexsWithoutDoubles.add(vertex);
        
        for(Relation relation: relations){
            Vertex vertexGauche = relation.getLeftVertex();
            Vertex vertexDroit = relation.getRightVertex();
            
            int i = vertexsWithoutDoubles.indexOf(vertexGauche);
            int j = vertexsWithoutDoubles.indexOf(vertexDroit);
            
            this.relationsMatrix.getValue()[i][j] = relation.getWeight();
              
            if(relation instanceof WeightedEdge) 
                this.relationsMatrix.getValue()[j][i] = ((WeightedEdge) relation).getWeight();
        }
    }


    public Double[][] getMatriceRelations() {
        return relationsMatrix.getValue();
    }

    public void setMatriceRelations(Double[][] relationsMatrix) {
        this.relationsMatrix.setValue(relationsMatrix);
    }

    public TreeSet<Vertex> getVerticesList() {
        return vertices.get();
    }

    public void setVerticesList(TreeSet<Vertex> vertices) {
        this.vertices.setValue(vertices);
    }
    
    @Override
    public String toString(){
        String MatrixDisplay = "";
        
        Vertex[] labelColumns = new Vertex[this.vertices.getValue().size()];
        Iterator<Vertex> iterator = this.vertices.get().iterator();
        
        for(int i = 0; iterator.hasNext(); i++){
            Vertex vertex = iterator.next();
            MatrixDisplay += "\t " + vertex.getLabel();
            labelColumns[i] = vertex;
        }
        
        for(int a = 0; a < this.relationsMatrix.getValue().length; a++){
            
            MatrixDisplay += "\n" + labelColumns[a].getLabel();
            
            for(int b = 0; b < this.relationsMatrix.getValue()[a].length; b++)
                if(this.relationsMatrix.getValue()[a][b] == null)
                    MatrixDisplay += "\t0.0";
                else MatrixDisplay += "\t" + this.getMatriceRelations()[a][b];
        }
            
        return MatrixDisplay + "\n";
    }
}