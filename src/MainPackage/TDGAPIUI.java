package MainPackage;

import MainPackage.API.Composants.Autres.Graph;
import MainPackage.API.Composants.Autres.Vertex;
import MainPackage.API.Composants.Liaison.WeightedArc;
import MainPackage.API.Composants.Liaison.Edge;
import MainPackage.API.Composants.Liaison.WeightedEdge;
import MainPackage.API.Composants.Liaison.Relation;
import MainPackage.API.Exceptions.NoVertex2DException;
import MainPackage.API.Exceptions.RelationMismatchException;
import MainPackage.API.Representation.IncidenceMatrix;
import MainPackage.API.Representation.WeightMatrix;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class TDGAPIUI extends Application {
    
    @Override
    public void start(Stage primaryStage) throws NoVertex2DException, RelationMismatchException, IOException {
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/MainPackage/MVC/Fxmls/MainWindowFXML.fxml")), 
                800, 600);
        
        primaryStage.setTitle("GraphIt");
        primaryStage.getIcons().add(new Image((TDGAPIUI.class.getResourceAsStream("/MainPackage/MVC/Icons/icone.png"))));
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.setOnCloseRequest((event) -> { System.exit(0); });
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
