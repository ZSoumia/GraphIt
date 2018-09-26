package MainPackage.API.Composants.Liaison;

import MainPackage.API.Composants.Autres.Vertex;
import javafx.beans.property.SimpleDoubleProperty;
import MainPackage.API.Composants.Liaison.Ponderation.WeightValue;

public class WeightedArc extends Arc {     
    public WeightedArc(String label, double weight, Vertex leftVertex, Vertex rightvVertex) {
        super(label, leftVertex, rightvVertex);
        this.weightValue.setValue(new WeightValue(weight));
    }
   
    public double getWeight(){
        return this.getWeightValue().getValue().getWeight();
    }

    public void setWeight(double weight) {
        this.weightValue.getValue().setWeight(weight);
    }
    
    public SimpleDoubleProperty weightProperty(){
        return this.weightValue.getValue().weightProperty();
    }
    
    @Override
    public boolean equals(Object o){
        if(o instanceof WeightedArc)
            return super.equals(o) && this.weightValue.getValue().getWeight() == ((WeightedArc) o).weightValue
                    .getValue().getWeight();
        return false;
    }
    
    @Override
    public int compareTo(Object o){
       if(o instanceof WeightedArc)
            if(((int)(this.getWeight() - ((WeightedArc) o).getWeight()) == 0)) 
                    return this.getLabel().compareTo(((WeightedArc) o).getLabel());
            else return (int)(this.getWeight() - ((WeightedArc) o).getWeight());
        return 0;
    }
}
