package MainPackage.API.Composants.Liaison;

import MainPackage.API.Composants.Autres.Vertex;
import javafx.beans.property.SimpleDoubleProperty;
import MainPackage.API.Composants.Liaison.Ponderation.WeightValue;

public class WeightedEdge extends Edge {
    
    public WeightedEdge(String label, double weight, Vertex leftVertex, Vertex rightVertex) {
        super(label, leftVertex, rightVertex);
        this.weightValue.setValue(new WeightValue(weight));
    }
    
    public double getWeight() {
        return this.weightValue.getValue().getWeight();
    }

    public void setWeight(double weight) {
        this.weightValue.getValue().setWeight(weight);
    }
    
    public SimpleDoubleProperty weightProperty(){
        return this.weightValue.getValue().weightProperty();
    }
    
    @Override
    public boolean equals(Object o){
        if(o instanceof WeightedEdge)
            return super.equals(o) && this.weightValue.getValue().getWeight() == ((WeightedEdge) o).weightValue
                    .getValue().getWeight();
        return false;
    }
    
    @Override
    public int compareTo(Object o){
        if(o instanceof WeightedEdge)
            return (int)(this.getWeight() - ((WeightedEdge) o).getWeight()) == 0 ? super.compareTo(o) : (int)(this.getWeight() - ((WeightedEdge) o).getWeight());
        return 0;
    }
}
