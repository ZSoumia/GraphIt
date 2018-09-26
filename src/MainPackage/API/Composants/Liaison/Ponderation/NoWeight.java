
package MainPackage.API.Composants.Liaison.Ponderation;

import javafx.beans.property.SimpleDoubleProperty;

public class NoWeight implements WeightBehaviour{

    @Override
    public double getWeight() {
        throw new UnsupportedOperationException("No weight !");
    }

    @Override
    public void setWeight(double weight) {
        throw new UnsupportedOperationException("No Weight !");
    }

    @Override
    public SimpleDoubleProperty weightProperty() {
        throw new UnsupportedOperationException("No Weight."); //To change body of generated methods, choose Tools | Templates.
    }  
}
