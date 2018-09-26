package MainPackage.API.Drawers;

import MainPackage.API.Composants.Autres.Graph;
import java.util.TreeSet;
import javafx.beans.property.SimpleObjectProperty;

public class Graph2D implements Drawable, Comparable{
    
    private SimpleObjectProperty<Graph> grapheProperty = new SimpleObjectProperty<Graph>();
    private SimpleObjectProperty<TreeSet<Vertex2D>> ensembleVerticesProperty = new SimpleObjectProperty<TreeSet<Vertex2D>>(null);
    private SimpleObjectProperty<TreeSet<Relation2D>> ensembleRelationsProperty = new SimpleObjectProperty<TreeSet<Relation2D>>(null);
    
    public Graph2D(Graph g){
        this.grapheProperty.setValue(g);
        this.grapheProperty.setValue(g);
        this.ensembleRelationsProperty.setValue(new TreeSet<Relation2D>());
        this.ensembleVerticesProperty.setValue(new TreeSet<Vertex2D>());
    }
    
    public SimpleObjectProperty<TreeSet<Vertex2D>> getEnsembleVerticesProperty() {
		return ensembleVerticesProperty;
	}

	public void setEnsembleVerticesProperty(SimpleObjectProperty<TreeSet<Vertex2D>> ensembleVerticesProperty) {
		this.ensembleVerticesProperty = ensembleVerticesProperty;
	}

	public void setGraph(Graph g){
        this.grapheProperty.setValue(g);
    }
    
    public Graph getGraph(){
        return this.grapheProperty.getValue();
    }
    
    @Override
    public boolean equals(Object o){
        if(o instanceof Graph2D)
            return this.getGraph().equals(((Graph2D) o).getGraph());
        return false;
    }
    
    @Override
    public String toString(){
        return this.getGraph().getNameGraph();
    }
    
    public SimpleObjectProperty<Graph> getGrapheProperty() {
		return grapheProperty;
	}

	public void setGrapheProperty(SimpleObjectProperty<Graph> graphProperty) {
		this.grapheProperty = graphProperty;
	}

	public SimpleObjectProperty<TreeSet<Relation2D>> getEnsembleRelationsProperty() {
		return ensembleRelationsProperty;
	}

	public void setEnsembleRelationsProperty(SimpleObjectProperty<TreeSet<Relation2D>> ensembleRelationsProperty) {
		this.ensembleRelationsProperty = ensembleRelationsProperty;
	}

	public TreeSet<Vertex2D> getEnsembleVertex(){
        return this.ensembleVerticesProperty.getValue();
    }
    
    public TreeSet<Relation2D> getEnsembleRelation(){
        return this.ensembleRelationsProperty.getValue();
    }
    
    public void setEnsembleVertex(TreeSet<Vertex2D> vertexs2Ds){
        this.ensembleVerticesProperty.setValue(vertexs2Ds);
    }
    
    public void setEnsembleRelation(TreeSet<Relation2D> relations2D){
        this.ensembleRelationsProperty.setValue(relations2D);
    }

    @Override
    public int compareTo(Object o) {
        if(o instanceof Graph2D)
            return this.getGraph().compareTo(((Graph2D) o).getGraph());
        return -1;
    }
    
    public boolean isThereVertex(String vertexName){
        for(Vertex2D s : this.getEnsembleVertex())
            if(s.getVertex().getLabel().equals(vertexName))
                return true;
        return false;
    }

    @Override
    public String UIBehavior() {
        return "Graph: " + this.getGraph().getNameGraph();
    }
}
