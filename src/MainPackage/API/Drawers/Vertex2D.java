package MainPackage.API.Drawers;

import MainPackage.API.Composants.Autres.Vertex;
import com.sun.glass.ui.Screen;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class Vertex2D extends Circle implements Drawable, Comparable{
    
    private SimpleObjectProperty<Vertex> vertex = new SimpleObjectProperty<Vertex>(null);
    private Text label2D = new Text();
    
    public Vertex2D(Vertex vertex){
        this.vertex.setValue(vertex);
        this.vertex.getValue().setVertex2D(this);
        
        this.initGraphicSettings(
                  "-fx-fill: rgb(255,255,255); "
                + "-fx-stroke-width: 2px; "
                + "-fx-stroke: rgba(0,0,0,1);",
                  "-fx-fill: rgb(0,0,0);");
        this.initLook();
        this.initBindings();
        this.initLabel(vertex);
        this.initListners();
    }
    
    private void initLook(){
        this.setCenterX(2 * Screen.getMainScreen().getWidth() / 5);
        this.setCenterY(2 * Screen.getMainScreen().getHeight() / 5);
        this.setRadius(25.0);
    }
    
    private void initBindings(){
        this.parentProperty().addListener(new ChangeListener<Parent>(){
            @Override
            public void changed(ObservableValue<? extends Parent> observable, Parent oldValue, Parent newValue) {
                if(newValue != null){
                    if(label2D.getParent() != null)
                        (((Pane)label2D.getParent()).getChildren()).remove(label2D);
                    (((Pane)newValue).getChildren()).add(label2D);
                }
            }
        });
    }
    
    private void initListners(){
        this.setOnMouseDragged(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                
                double dx = getScene().getWidth() - ((Pane)getParent()).getWidth();
                double dy = getScene().getHeight()- ((Pane)getParent()).getHeight();

                double xPos = event.getSceneX() - dx;
                double yPos = event.getSceneY() - dy;
                
                setCenterX(xPos);
                setCenterY(yPos);
            }            
        });
        
        this.layoutXProperty().addListener((observable) -> { this.setLayoutX(0.0); });
        this.layoutYProperty().addListener((observable) -> { this.setLayoutY(0.0); });
    }
    
    private void initLabel(Vertex vertex){
        this.label2D.textProperty().bind(vertex.labelProperty());
        this.label2D.layoutXProperty().addListener((observable) -> { this.label2D.setLayoutX(0.0); });
        this.label2D.layoutYProperty().addListener((observable) -> { this.label2D.setLayoutY(0.0); });
        this.label2D.xProperty().bind(this.centerXProperty().subtract(this.label2D.prefWidth(0) / 2));
        this.label2D.yProperty().bind(this.centerYProperty().add(this.label2D.prefHeight(0)/4));
    }
    
    public void setVertex(Vertex vertex){
        this.vertex.setValue(vertex);
    }
    
    public Vertex getVertex(){
        return this.vertex.getValue();
    }
    
    public void initGraphicSettings(String circleCss,String labelCss){    
        this.label2D.setStyle(labelCss);
        this.setStyle(circleCss);
    }

    @Override
    public int compareTo(Object o) {
        if(o instanceof Vertex2D)
            return this.getVertex().compareTo(((Vertex2D) o).getVertex());
        return -1;
    }
    
    @Override
    public boolean equals(Object o){
        if(o instanceof Vertex2D)
            return this.getVertex().equals(((Vertex2D) o).getVertex());
        return false;
    }
    
    public Text getLabel(){
        return this.label2D;
    }
    
    public void setLabel(Text t){
        this.label2D = t;
    }
    
    @Override
    public String toString(){
        return this.getVertex().getLabel();
    }

    @Override
    public String UIBehavior() {
        return "Vertex: " + this.getVertex().getLabel();
    }
}
