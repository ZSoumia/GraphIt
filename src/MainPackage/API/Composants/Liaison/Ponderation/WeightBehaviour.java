package MainPackage.API.Composants.Liaison.Ponderation;

import javafx.beans.property.SimpleDoubleProperty;

public interface WeightBehaviour {
    public abstract double getWeight();
    public abstract void setWeight(double weight);
    public abstract SimpleDoubleProperty weightProperty();
}
