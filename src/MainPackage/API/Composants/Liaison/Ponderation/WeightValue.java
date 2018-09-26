package MainPackage.API.Composants.Liaison.Ponderation;

import javafx.beans.property.SimpleDoubleProperty;

public class WeightValue implements WeightBehaviour{

    private SimpleDoubleProperty weightValue = new SimpleDoubleProperty(0.0);
    
    public WeightValue(double value){
        this.setWeight(value);
    }
    
    @Override
    public boolean equals(Object o){
        if(o instanceof WeightValue)
            return this.getWeight() == ((WeightValue) o).getWeight();
        return false;
    }
    
    @Override
    public double getWeight() {
        return this.weightValue.getValue();
    }

    @Override
    public void setWeight(double weight) {
        this.weightValue.setValue(weight);
    }   

    @Override
    public SimpleDoubleProperty weightProperty() {
        return this.weightValue;
    }
}
