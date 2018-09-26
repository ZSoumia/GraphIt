package MainPackage.API.Composants.Autres;

import MainPackage.API.Composants.Liaison.Relation;
import MainPackage.API.Drawers.Vertex2D;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class Vertex implements Comparable{

    private SimpleObjectProperty<ArrayList<Relation>> listRelations = new SimpleObjectProperty<ArrayList<Relation>>(new ArrayList<Relation>());
    private SimpleObjectProperty<Vertex2D> vertex2D = new SimpleObjectProperty<Vertex2D>(null);
    private SimpleStringProperty label = new SimpleStringProperty("No Label");
    
    public Vertex(String label){
        this.label.set(label);
    }
    
    public Vertex(String label,Relation ... relations){
        this.label.set(label);
        this.listRelations.getValue().addAll(Arrays.asList(relations));
    }
    
    public SimpleStringProperty labelProperty(){
        return this.label;
    }
    
    public SimpleObjectProperty<ArrayList<Relation>> listRelationsProperty(){
        return this.listRelations;
    }
    
    public SimpleObjectProperty<Vertex2D> vertex2DProperty(){
        return this.vertex2D;
    }
    
    public String getLabel() {
        return label.getValue();
    }

    public void setLabel(String label) {
        this.label.setValue(label);
    }

    public ArrayList<Relation> getListRelations() {
        return listRelations.getValue();
    }

    public void setListRelations(ArrayList<Relation> listRelations) {
        this.listRelations.setValue(listRelations);
    }
    
    public Vertex2D getVertex2D(){
        return this.vertex2D.get();
    }
    
    public void setVertex2D(Vertex2D vertex2D){
        this.vertex2D.setValue(vertex2D);
    }
    
    @Override 
    public String toString(){
        return "Description of Vertex: " + this.label.getValue() + "\n";
    }
    
    @Override
    public boolean equals(Object o){
        if(o instanceof Vertex)
            return this.hashCode() == o.hashCode();
        return false;
    }

    @Override
    public int compareTo(Object o) {
        if(o instanceof Vertex)
            return (this.label.getValue().compareTo(((Vertex) o).label.getValue()));
        return 0;
    }

    public boolean isIncidentTo(Relation relation){
        for(Relation r: this.listRelations.getValue())
            if(relation.equals(r)) return true;
        return false;
    }
}
