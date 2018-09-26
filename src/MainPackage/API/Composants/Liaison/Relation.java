package MainPackage.API.Composants.Liaison;

import MainPackage.API.Composants.Autres.Vertex;
import MainPackage.API.Composants.Liaison.Ponderation.WeightBehaviour;
import MainPackage.API.Composants.Liaison.Ponderation.NoWeight;
import MainPackage.API.Drawers.Relation2D;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public abstract class Relation implements Comparable {
    
    private SimpleStringProperty label = new SimpleStringProperty("No Label");
    private SimpleObjectProperty<Vertex> leftVertex = new SimpleObjectProperty<Vertex>(null),
                                         rightVertex = new SimpleObjectProperty<Vertex>(null);
    private SimpleObjectProperty<Relation2D> relation2D = new SimpleObjectProperty<Relation2D>(null);
    protected SimpleObjectProperty<WeightBehaviour> weightValue = new SimpleObjectProperty<WeightBehaviour>(new NoWeight());
    
    public Relation(String label, Vertex vertexGauche, Vertex vertexDroit){
        this.label.set(label);
        this.leftVertex.setValue(vertexGauche);
        this.rightVertex.setValue(vertexDroit);
        
        this.rightVertex.getValue().getListRelations().add(this);
        this.leftVertex.getValue().getListRelations().add(this);
    }
   
    public String getLabel() {
        return label.getValue();
    }

    public void setLabel(String label) {
        this.label.setValue(label);
    }
   
    public Vertex getLeftVertex() {
        return this.leftVertex.getValue();
    }
    
    public void setLeftVertex(Vertex vertexGauche) {
        this.leftVertex.setValue(vertexGauche);
    }
    
    public Vertex getRightVertex() {
        return this.rightVertex.getValue();
    }

    public void setRightVertex(Vertex rightVertex) {
        this.rightVertex.setValue(rightVertex);
    }
    
    public void setRelation2D(Relation2D relation2D){
        this.relation2D.setValue(relation2D);
    }
    
    public Relation2D getRelation2D(){
        return this.relation2D.getValue();
    }    
    @Override
    public int compareTo(Object o) {
        return (int)(this.label.getValue().compareTo(((Relation)o).getLabel())); 
    }
    
    @Override
    public boolean equals(Object o){
        if(o instanceof Relation)
            return this.getLeftVertex().equals(((Relation) o).getLeftVertex()) && this.getRightVertex().equals(((Relation) o).getRightVertex());
        return false;
    }
    
    public SimpleObjectProperty<WeightBehaviour> getWeightValue() {
		return weightValue;
	}


	public void setWeightValue(SimpleObjectProperty<WeightBehaviour> weightValue) {
		this.weightValue = weightValue;
	}


	public SimpleObjectProperty<Vertex> vertexGaucheProperty(){
        return this.leftVertex;
    }
    
    public SimpleObjectProperty<Vertex> vertexDroitProperty(){
        return this.rightVertex;
    }
    
    public SimpleStringProperty labelProperty(){
        return this.label;
    }
    
    public SimpleObjectProperty<Relation2D> relation2DProperty(){
        return this.relation2D;
    }
    
    public boolean isIncidentTo(Vertex vertex){
        return (this.getRightVertex().equals(vertex) || this.getLeftVertex().equals(vertex));
    }    
    
    @Override
    public String toString(){
        return "Edge description : " + this.label.getValue() + "\n"
                + "Left vertex: " + this.getLeftVertex().getLabel() + "\n" 
                + "Right vertex: " + this.getRightVertex().getLabel() + "\n";
    }

    public double getWeight(){
        return this.weightValue.getValue().getWeight();
    }
}
