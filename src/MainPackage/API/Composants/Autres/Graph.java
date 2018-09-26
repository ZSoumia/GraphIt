package MainPackage.API.Composants.Autres;

import MainPackage.API.Composants.Autres.Graph.TypeGraph;
import MainPackage.API.Composants.Liaison.Relation;
import MainPackage.API.Exceptions.RelationMismatchException;
import MainPackage.API.Representation.AdjacencyMatrix;
import MainPackage.API.Representation.WeightMatrix;
import MainPackage.API.Representation.IncidenceMatrix;
import javafx.beans.property.SimpleObjectProperty;
import MainPackage.API.Composants.Liaison.Ponderation.WeightValue;
import MainPackage.API.Drawers.Graph2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;
import javafx.beans.property.SimpleStringProperty;

public class Graph implements Comparable{
 
	private SimpleObjectProperty<IncidenceMatrix> incidenceMatrix = new SimpleObjectProperty<IncidenceMatrix>(null);
    private SimpleObjectProperty<WeightMatrix> weightMatrix = new SimpleObjectProperty<WeightMatrix>(null);
    private SimpleObjectProperty<AdjacencyMatrix> adjacencyMatrix = new SimpleObjectProperty<AdjacencyMatrix>(null);
    private SimpleStringProperty nameGraphProperty = new SimpleStringProperty();
    private SimpleObjectProperty<Graph2D> graph2DProperty = new SimpleObjectProperty<Graph2D>(null);
    private SimpleObjectProperty<TypeGraph> typeGraphProperty = new SimpleObjectProperty<TypeGraph>(TypeGraph.NOT_PONDERATED_NOT_ORIENTED);
    
    public Graph(String nameGraph, IncidenceMatrix incidenceMatrix) throws RelationMismatchException{
        this.incidenceMatrix.setValue(incidenceMatrix);
        this.weightMatrix.setValue(new WeightMatrix((Vertex[]) (this.getIncidenceMatrix()).getVerticesList().toArray(new Vertex[]{})));
        this.adjacencyMatrix.setValue(new AdjacencyMatrix((Vertex[]) this.getIncidenceMatrix().getVerticesList().toArray(new Vertex[]{})));
        this.nameGraphProperty.setValue(nameGraph);
    }
    
    public Graph(String nameGraph, WeightMatrix weightMatrix) throws RelationMismatchException{
        this.weightMatrix.setValue(weightMatrix);
        this.incidenceMatrix.setValue(new IncidenceMatrix((Vertex[]) this.getWeightMatrix().getVerticesList().toArray(new Vertex[]{})));
        this.adjacencyMatrix.setValue(new AdjacencyMatrix((Vertex[]) this.getWeightMatrix().getVerticesList().toArray(new Vertex[]{})));
        this.nameGraphProperty.setValue(nameGraph);
    }
    
    public Graph(String nameGraph, AdjacencyMatrix adjacencyMatrix) throws RelationMismatchException{
        this.adjacencyMatrix.setValue(adjacencyMatrix);
        this.incidenceMatrix.setValue(new IncidenceMatrix((Vertex[]) this.getAdjacencyMatrix().getVertices().toArray(new Vertex[]{})));
        this.weightMatrix.setValue(new WeightMatrix((Vertex[]) this.getAdjacencyMatrix().getVertices().toArray(new Vertex[]{})));
        this.nameGraphProperty.setValue(nameGraph);
    }
    
    public Graph(String nameGraph, Vertex ... vertexs) throws RelationMismatchException{
        this.weightMatrix.setValue(new WeightMatrix(vertexs));
        this.incidenceMatrix.setValue(new IncidenceMatrix(vertexs));
        this.adjacencyMatrix.setValue(new AdjacencyMatrix(vertexs));
        this.nameGraphProperty.setValue(nameGraph);
    }
    
    public Graph(String nameGraph, Relation ... relations) throws RelationMismatchException{
        this.adjacencyMatrix.setValue(new AdjacencyMatrix(relations));
        this.incidenceMatrix.setValue(new IncidenceMatrix(relations));
        if(relations[0].getWeightValue().getValue() instanceof WeightValue)
            this.weightMatrix.setValue(new WeightMatrix(relations));
        this.nameGraphProperty.setValue(nameGraph);
    }
   
    @Override
    public boolean equals(Object o){
        if(o instanceof Graph)
            return ((Graph) o).getNameGraph().equals(this.getNameGraph());
        return false;
    }
    
    @Override
    public String toString(){
        return this.getNameGraph();
    }

    @Override
    public int compareTo(Object o) {
        if(o instanceof Graph)
            return this.getNameGraph().compareTo(((Graph) o).getNameGraph());
        return -1;
    }
    
    public enum TypeGraph {
        NOT_PONDERATED_NOT_ORIENTED,
        PONDERATED_NOT_ORIENTED,
        NOT_PONDERATED_ORIENTED,
        PONDERATED_ORIENTED,
    }
    
    public TypeGraph getTypeGraph(){
        return this.typeGraphProperty.getValue();
    }
    
    public void setTypeGraph(TypeGraph tg){
        this.typeGraphProperty.setValue(tg);
    }
    
    public TreeSet<Relation> getRelations(){
        return this.getAdjacencyMatrix().getRelations();
    }
     public CopyOnWriteArrayList<Vertex> getNodesSortedByDegree(){
        
        TreeSet<Vertex> listVertexs = this.getAdjacencyMatrix().getVertices();
        Vertex[] list = new Vertex[listVertexs.size()];
        CopyOnWriteArrayList<Vertex> resultat = new CopyOnWriteArrayList<Vertex>();
        
        int k = 0;
        // for sorting 
        for(Vertex s:listVertexs){
            list[k] = s;
            k++;
        }
   
        for(int i = 0; i< list.length;i++){
            for(int j = 0; j< list.length;j++){
                if(list[i].getListRelations().size() > list[j].getListRelations().size()){
                    Vertex a = list[i];
                    list[i] = list[j];
                    list[j] = a;
                }
            }
        }
        Collections.addAll(resultat,list);
        return resultat;   
    }
    
    // all vertices that are adjacent to s
    public ArrayList<Vertex> getListAdjacents(Vertex s){
        ArrayList<Vertex> list = new ArrayList<Vertex>();
        
        for (Relation g : this.getAdjacencyMatrix().getRelations())
            if(g.getRightVertex().equals(s)) 
                list.add(g.getLeftVertex());
            else if(g.getLeftVertex().equals(s))
                list.add(g.getRightVertex());
        return list;    
    }
    
   
    public boolean isConnexe(){
        boolean is = true;
        ArrayList<Vertex> atteints = new ArrayList<Vertex>();
        for(Vertex s1 : this.getAdjacencyMatrix().getVertices()){

            this.partialDepthSearch(s1, atteints);
            is = is && 
                    (atteints.containsAll(this.getAdjacencyMatrix().getVertices())) && 
                    this.getAdjacencyMatrix().getVertices().containsAll(atteints);
        }
        return is;
    }
    
    // the successors of a node 
    public TreeSet<Vertex> getSuccessors(Vertex s){
        
        TreeSet<Vertex> successors = new TreeSet<Vertex>();
        for(Relation relation : this.getAdjacencyMatrix().getRelations())
            if((relation.getLeftVertex().equals(s)) &&
                    s.equals(relation.getLeftVertex()))
                successors.add(relation.getRightVertex());
    
        return successors;
    }
      
    public TreeSet<Relation> getArretesSousGraph(CopyOnWriteArrayList<Vertex> vertexs){
       TreeSet<Relation> resu = new TreeSet<Relation>();
        for(Vertex s : vertexs)
            resu.addAll(s.getListRelations()); 
        return resu;
    }
 
    public ArrayList<Relation> getSuccessorsRelations(Vertex s){
        ArrayList<Relation> relations = new ArrayList<Relation>();
        for(Relation r : this.getAdjacencyMatrix().getRelations())
            if(r.getLeftVertex().equals(s))  
                 relations.add(r);
        return relations;
    }
    //----------------------- cycle detection in GNO ---------------------------------
    
    public boolean isCyclicGNO(){
        
        TreeSet<Vertex> visited = new TreeSet<Vertex>();
        for(Vertex s : this.getAdjacencyMatrix().getVertices()){
            if(visited.contains(s)){
                continue;
            }
            if(hasCycleGNO(s, visited, null)){
                return true;
            }
        }
        return false;
    }
    
    private boolean hasCycleGNO(Vertex vertex, TreeSet<Vertex> visited,Vertex parent){
        visited.add(vertex);
        for(Vertex adj : this.getListAdjacents(vertex)){
            if(adj.equals(parent)){
                continue;
            }
            if(visited.contains(adj)){
                return true;
            }
            if(hasCycleGNO(adj,visited,vertex)){
                return true;
            }
        }
        return false;
    }
    
     public void partialDepthSearch(Vertex origine,ArrayList<Vertex> reachedVertices){
        reachedVertices.add(origine);
        Iterator it = this.getListAdjacents(origine).iterator();
        while (it.hasNext()) { 
            Vertex following = (Vertex)it.next();
            if (!reachedVertices.contains(following)) 
                this.partialDepthSearch(following, reachedVertices);        
        }
    }
    
    //-------  spanning tree ---------------------------------------
     private TreeSet<Relation> getCocycle(CopyOnWriteArrayList<Vertex> vertexs){
        TreeSet<Relation> resu =  new TreeSet<Relation>();
        TreeSet<Relation> entree = this.getArretesSousGraph(vertexs);

        for(Relation r : entree)
            if(!(vertexs.contains(r.getRightVertex())&&(vertexs.contains(r.getLeftVertex()))))
                                                                        
            	resu.add(r);
        return resu;
    }
    //-----------  ACM algos (prim & kruskal )for GNO only ----------
    
    public Graph  prim () throws RelationMismatchException{
        
       boolean empty = false;
        TreeSet<Relation> f = new TreeSet<Relation>();
        TreeSet<Relation> k = new TreeSet<Relation>();
        CopyOnWriteArrayList<Vertex> u = new CopyOnWriteArrayList<Vertex>();
        // start Vertex always chosen as the first one 
        Vertex depart = this.getAdjacencyMatrix().getVertices().first();
        u.add(depart);
        
        while(!u.containsAll(this.getAdjacencyMatrix().getVertices()) && ! empty){
            
            k = this.getCocycle(u);
            empty = k.isEmpty();
            if(!empty){
               f.add(k.first());
            if(u.contains(k.first().getRightVertex()))
                u.add(k.first().getLeftVertex());
            else 
                u.add(k.first().getRightVertex()); 
            }
        }
        return new Graph("ACM got by using  prim on : "+this.getNameGraph(),f.toArray(new Relation[f.size()]));
    }
    
    public Graph kruskal()throws RelationMismatchException{
        ArrayList<Relation> resultat = new ArrayList<Relation>();
        Graph partial = null;
        Iterator<Relation> it = this.getAdjacencyMatrix().getRelations().iterator();
        
        while(it.hasNext()){
            Relation r = it.next();
            resultat.add(r);
            partial = new Graph( " ",resultat.toArray(new Relation[resultat.size()]));
            // check if partial is cyclic then take off r 
            if(partial.isCyclicGNO()) 
                resultat.remove(r);
        }  
     return new Graph( "ACM got  by using  kruskal on  : "+this.getNameGraph() ,resultat.toArray(new Relation[resultat.size()])) ;   
    }

	public IncidenceMatrix getIncidenceMatrix() {
		return incidenceMatrix.get();
	}

	public void setIncidenceMatrix(SimpleObjectProperty<IncidenceMatrix> incidenceMatrix) {
		this.incidenceMatrix = incidenceMatrix;
	}

	public WeightMatrix getWeightMatrix() {
		return weightMatrix.get();
	}

	public void setWeightMatrix(SimpleObjectProperty<WeightMatrix> weightMatrix) {
		this.weightMatrix = weightMatrix;
	}

	public AdjacencyMatrix getAdjacencyMatrix() {
		return adjacencyMatrix.get();
	}

	public void setAdjacencyMatrix(SimpleObjectProperty<AdjacencyMatrix> adjacencyMatrix) {
		this.adjacencyMatrix = adjacencyMatrix;
	}

	public String getNameGraph() {
		return nameGraphProperty.get();
	}

	public void setNameGraph(SimpleStringProperty nameGraphProperty) {
		this.nameGraphProperty = nameGraphProperty;
	}

	 public Graph2D getGraph2D(){
	        return this.graph2DProperty.getValue();
	    }
	    
	    public void setGraph2D(Graph2D g2d){
	        this.graph2DProperty.setValue(g2d);
	    }

	public SimpleObjectProperty<TypeGraph> getTypeGraphProperty() {
		return typeGraphProperty;
	}

	public void setTypeGraphProperty(SimpleObjectProperty<TypeGraph> typeGraph) {
		this.typeGraphProperty = typeGraph;
	}
	   
    public String getNameGraphProperty() {
		return nameGraphProperty.getValue();
	}

	public void setNameGraphProperty(SimpleStringProperty nameGraphProperty) {
		this.nameGraphProperty = nameGraphProperty;
	}
	
	public TreeSet<Vertex> getVertices(){
		return this.getAdjacencyMatrix().getVertices();
	}
	
}


