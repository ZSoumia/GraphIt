package MainPackage.MVC.Controllers;

import Algorithmes.Colouration;
import Algorithmes.PathElement;
import Algorithmes.*;
import MainPackage.API.Composants.Autres.Graph;
import MainPackage.API.Composants.Autres.Graph.TypeGraph;
import MainPackage.API.Composants.Autres.Vertex;
import MainPackage.API.Composants.Liaison.Arc;
import MainPackage.API.Composants.Liaison.WeightedArc;
import MainPackage.API.Composants.Liaison.Edge;
import MainPackage.API.Composants.Liaison.WeightedEdge;
import MainPackage.API.Composants.Liaison.Relation;
import MainPackage.API.Drawers.Drawable;
import MainPackage.API.Drawers.Graph2D;
import MainPackage.API.Drawers.Relation2D;
import MainPackage.API.Drawers.Vertex2D;
import MainPackage.API.Exceptions.NoVertex2DException;
import MainPackage.API.Exceptions.RelationMismatchException;
import javafx.fxml.Initializable;
import com.jfoenix.controls.JFXColorPicker;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Callback;



public class MainWindowController implements Initializable {

    @FXML
    private JFXColorPicker colorChooser;

    @FXML
    private TreeView<Drawable> componentsTree;

    @FXML
    private Pane workSpace;
    

    @FXML
    void addGraph(ActionEvent event) throws RelationMismatchException, NoVertex2DException {
        TextInputDialog dialogForName = new TextInputDialog("Graph " + this.componentsTree.getRoot().getChildren().size());
                        dialogForName.setTitle("New graph");
                        dialogForName.setHeaderText("Graph name ");
                        dialogForName.setContentText("Please enter the graph name :");

        Optional<String> resultName = dialogForName.showAndWait();
                        
        if (resultName.isPresent() && !this.existGraph2DNode(resultName.get())){
            ChoiceDialog<String> dialogType = new ChoiceDialog<>("Non weighted & unoriented", new String[]{
            				"Non weighted & unoriented",
                            "Weighted & unoriented",
                            "Non weighted & oriented",
                            "Pondere et oriente"});
                                 dialogType.setTitle("New graph");
                                 dialogType.setHeaderText("Graph name ");
                                 dialogType.setContentText("Please choose the graph type :");

            Optional<String> resultType = dialogType.showAndWait();
                             
            if(resultType.isPresent()){
                Graph graph = new Graph(resultName.get(),new Vertex[]{});
                       graph.setTypeGraph(
                            resultType.get().equals("Non weighted & unoriented") ? TypeGraph.NOT_PONDERATED_NOT_ORIENTED :
                            resultType.get().equals("Weighted & unoriented") ? TypeGraph.PONDERATED_NOT_ORIENTED :
                            resultType.get().equals("Non weighted & oriented") ? TypeGraph.NOT_PONDERATED_ORIENTED :
                            TypeGraph.PONDERATED_ORIENTED
                       );
                this.createGraph2DNode(graph);
            }
        }
        else if(resultName.isPresent()){
            Alert alert = new Alert(AlertType.ERROR);
                  alert.setTitle("New graph");
                  alert.setHeaderText("Error...");
                  alert.setContentText("Please choose a graph name that doesn't exist already");
                  alert.showAndWait();
        }
    }

    @FXML
    void addRelation(ActionEvent event) throws RelationMismatchException, NoVertex2DException {
        TreeItem<Drawable> firstVertex = this.componentsTree.getSelectionModel().getSelectedItems().get(0);
        TreeItem<Drawable> secondVertex = this.componentsTree.getSelectionModel().getSelectedItems().get(1);
                
        if(firstVertex != null && secondVertex != null){
            if(firstVertex.getValue() instanceof Vertex2D && secondVertex.getValue() instanceof Vertex2D){
                TreeItem<Drawable> firstVertexParent = (TreeItem<Drawable>) firstVertex.getParent();
                TreeItem<Drawable> secondVertexParent = (TreeItem<Drawable>) secondVertex.getParent();
                
                if(firstVertexParent.getValue().equals(secondVertexParent.getValue())){
                    TextInputDialog dialogForName = new TextInputDialog("");
                                    dialogForName.setTitle("New relation");
                                    dialogForName.setHeaderText("Relation's name ");
                                    dialogForName.setContentText("Please enter the relation's name :");
                                    
                    Optional<String> resultName = dialogForName.showAndWait();
                    
                    if(resultName.isPresent() && !this.existRelation2DNode(resultName.get())){
                        
                        if(((Graph2D)firstVertexParent.getValue()).getGraph().getTypeGraph() == TypeGraph.PONDERATED_NOT_ORIENTED || 
                           ((Graph2D)firstVertexParent.getValue()).getGraph().getTypeGraph()== TypeGraph.PONDERATED_ORIENTED){
                            TextInputDialog dialogForWeight = new TextInputDialog("");
                                            dialogForWeight.setTitle("New relation");
                                            dialogForWeight.setHeaderText("Relation's weight");
                                            dialogForWeight.setContentText("Please enter the relation's weight value :");
                                            
                            Optional<String> resultWeight = dialogForWeight.showAndWait();
                            
                            if(resultWeight.isPresent()){
                                try {
                                    if(((Graph2D)firstVertexParent.getValue()).getGraph().getTypeGraph() == TypeGraph.NOT_PONDERATED_ORIENTED ||
                                            ((Graph2D)firstVertexParent.getValue()).getGraph().getTypeGraph() == TypeGraph.PONDERATED_ORIENTED){
                                        ChoiceDialog<String> dialogType = new ChoiceDialog<>("From " + ((Vertex2D)firstVertex.getValue()).getVertex().getLabel() + " to  " + ((Vertex2D)secondVertex.getValue()).getVertex().getLabel(), new String[]{
                                                        "From " + ((Vertex2D)firstVertex.getValue()).getVertex().getLabel() + " to  " + ((Vertex2D)secondVertex.getValue()).getVertex().getLabel(),
                                                        "From " + ((Vertex2D)secondVertex.getValue()).getVertex().getLabel() + " to  " + ((Vertex2D)firstVertex.getValue()).getVertex().getLabel()});
                                                             dialogType.setTitle("New relation");
                                                             dialogType.setHeaderText("Direction of relation");
                                                             dialogType.setContentText("Please choose the relation's direction :");

                                        Optional<String> resultDirection = dialogType.showAndWait();

                                        if(resultDirection.isPresent()){
                                            if(("From " + ((Vertex2D)firstVertex.getValue()).getVertex().getLabel() + " to  " + ((Vertex2D)secondVertex.getValue()).getVertex().getLabel()).equals(resultDirection.get()))
                                                this.replaceGraph2DNode(resultName.get(), Double.parseDouble(resultWeight.get()), firstVertex, secondVertex);
                                            else this.replaceGraph2DNode(resultName.get(), Double.parseDouble(resultWeight.get()), secondVertex, firstVertex);
                                        }
                                    }
                                    else
                                        this.replaceGraph2DNode(resultName.get(), Double.parseDouble(resultWeight.get()), firstVertex, secondVertex);
                                }
                                catch(NumberFormatException exp){
                                    exp.printStackTrace();
                                    
                                    Alert alert = new Alert(AlertType.ERROR);
                                          alert.setTitle("New relation");
                                          alert.setHeaderText("Error...");
                                          alert.setContentText("Please choose a valid weight (real number)");
                                          alert.showAndWait();
                                }
                            }
                        }
                        else {
                            if(((Graph2D)firstVertexParent.getValue()).getGraph().getTypeGraph() == TypeGraph.NOT_PONDERATED_ORIENTED ||
                                    ((Graph2D)firstVertexParent.getValue()).getGraph().getTypeGraph() == TypeGraph.PONDERATED_ORIENTED){
                            
                                if(((Graph2D)firstVertexParent.getValue()).getGraph().getTypeGraph() == TypeGraph.NOT_PONDERATED_ORIENTED ||
                                            ((Graph2D)firstVertexParent.getValue()).getGraph().getTypeGraph() == TypeGraph.PONDERATED_ORIENTED){
                                        ChoiceDialog<String> dialogType = new ChoiceDialog<>("From " + ((Vertex2D)firstVertex.getValue()).getVertex().getLabel() + " to  " + ((Vertex2D)secondVertex.getValue()).getVertex().getLabel(), new String[]{
                                                        "From " + ((Vertex2D)firstVertex.getValue()).getVertex().getLabel() + " to  " + ((Vertex2D)secondVertex.getValue()).getVertex().getLabel(),
                                                        "From " + ((Vertex2D)secondVertex.getValue()).getVertex().getLabel() + " to  " + ((Vertex2D)firstVertex.getValue()).getVertex().getLabel()});
                                                             dialogType.setTitle("New relation");
                                                             dialogType.setHeaderText("Direction of the  relation");
                                                             dialogType.setContentText("Please choose the relation's direction :");

                                        Optional<String> resultDirection = dialogType.showAndWait();

                                        if(resultDirection.isPresent()){
                                            if(("From " + ((Vertex2D)firstVertex.getValue()).getVertex().getLabel() + " to  " + ((Vertex2D)secondVertex.getValue()).getVertex().getLabel()).equals(resultDirection.get()))
                                                this.replaceGraph2DNode(resultName.get(), 0.0, firstVertex, secondVertex);
                                            else this.replaceGraph2DNode(resultName.get(), 0.0, secondVertex, firstVertex);
                                        }
                                    }
                            }
                            else 
                                this.replaceGraph2DNode(resultName.get(), 0.0, firstVertex, secondVertex);
                        }
                            
                    }
                    else if(resultName.isPresent()){
                        Alert alert = new Alert(AlertType.ERROR);
                              alert.setTitle("New relation");
                              alert.setHeaderText("Error...");
                              alert.setContentText("Please choose a relation name that doesn't exist already ");
                              alert.showAndWait();
                    }
                }
                else {
                    Alert alert = new Alert(AlertType.ERROR);
                          alert.setTitle("New relation");
                          alert.setHeaderText("Error...");
                          alert.setContentText("Please choose 2 vertecies, the first one is the left one and the second is the right one");
                          alert.showAndWait();
                }
            }
            else {
                Alert alert = new Alert(AlertType.ERROR);
                      alert.setTitle("New relation");
                      alert.setHeaderText("Error...");
                      alert.setContentText("Please choose 2 vertecies, the first one is the left one and the second is the right one");
                      alert.showAndWait();
            }
        }
        else {
            Alert alert = new Alert(AlertType.ERROR);
                  alert.setTitle("New relation");
                  alert.setHeaderText("Error...");
                  alert.setContentText("Please choose 2 vertecies, the first one is the left one and the second is the right one");
                  alert.showAndWait();
        }
    }

    @FXML
    
    void addVertex(ActionEvent event) throws RelationMismatchException, NoVertex2DException {
        TreeItem<Drawable> selectedTreeItem = this.componentsTree.getSelectionModel().getSelectedItem();
        
        if((selectedTreeItem != null) && (selectedTreeItem.getValue() instanceof Graph2D)){
            TextInputDialog dialog = new TextInputDialog("Graph " + this.componentsTree.getRoot().getChildren().size());
                        dialog.setTitle("New vertex");
                        dialog.setHeaderText("Vertex name");
                        dialog.setContentText("Please enter the vertex name :");

            Optional<String> result = dialog.showAndWait();
            
            if (result.isPresent() && !this.existVertex2DNode(result.get()))
                this.replaceGraph2DNode(result.get(), selectedTreeItem);
            else if(result.isPresent()){
                Alert alert = new Alert(AlertType.ERROR);
                      alert.setTitle("Add a vertex");
                      alert.setHeaderText("Error...");
                      alert.setContentText("Please enter a vertex name that doesn't exist already.");
                      alert.showAndWait();
            }
        }
        else {
            Alert alert = new Alert(AlertType.ERROR);
                  alert.setTitle("Add a vertex");
                  alert.setHeaderText("Error...");
                  alert.setContentText("First select a graph , Please .");
                  alert.showAndWait();
        }
    }

    @FXML
    void deleteComponent(ActionEvent event) throws RelationMismatchException, NoVertex2DException {
        TreeItem<Drawable> selectedTreeItem = this.componentsTree.getSelectionModel().getSelectedItem();
        
        if(selectedTreeItem != null){
            if(selectedTreeItem.getValue() instanceof Graph2D)
                this.deleteGraph2DNode( selectedTreeItem);
            else if(selectedTreeItem.getValue() instanceof Vertex2D)
                this.deleteVertex2DNode(selectedTreeItem);
            else if(selectedTreeItem.getValue() instanceof Relation2D)
                this.deleteRelation2DNode(selectedTreeItem);
        }
        else {
            Alert alert = new Alert(AlertType.ERROR);
                  alert.setTitle("Removal of an element");
                  alert.setHeaderText("Error...");
                  alert.setContentText("First select an element, please .");
                  alert.showAndWait();
        }
    }
    
    @FXML
    void printAdjacenseMatrix(ActionEvent event) {
        TreeItem<Drawable> selectedTreeItem = this.componentsTree.getSelectionModel().getSelectedItem();
        
        if(selectedTreeItem != null && selectedTreeItem.getValue() instanceof Graph2D){
            Graph2D selectedGraph2D = (Graph2D) selectedTreeItem.getValue();
            Graph selectedGraph = selectedGraph2D.getGraph();
            
            this.constructAdjancenseMatrix(selectedGraph);
        }
        else {
            Alert alert = new Alert(AlertType.ERROR);
                  alert.setTitle("Displaying a Matrix");
                  alert.setHeaderText("Error...");
                  alert.setContentText("First select a graph,please .");
                  alert.showAndWait();
        }
    }

    @FXML
    void printIncidenceMatrix(ActionEvent event) {
        TreeItem<Drawable> selectedTreeItem = this.componentsTree.getSelectionModel().getSelectedItem();
        
        if(selectedTreeItem != null && selectedTreeItem.getValue() instanceof Graph2D){
            Graph2D selectedGraph2D = (Graph2D) selectedTreeItem.getValue();
            Graph selectedGraph = selectedGraph2D.getGraph();
           
            this.constructIncidenceMatrix(selectedGraph);
        }
        else {
            Alert alert = new Alert(AlertType.ERROR);
                  alert.setTitle("Displaying a Matrix");
                  alert.setHeaderText("Error...");
                  alert.setContentText("First select a graph.");
                  alert.showAndWait();
        }
    }

    @FXML
    void printWeightMatrix(ActionEvent event) {
        TreeItem<Drawable> selectedTreeItem = this.componentsTree.getSelectionModel().getSelectedItem();

        if(selectedTreeItem != null && selectedTreeItem.getValue() instanceof Graph2D){
            Graph2D selectedGraph2D = (Graph2D) selectedTreeItem.getValue();
            Graph selectedGraph = selectedGraph2D.getGraph();

            if(selectedGraph.getTypeGraph() == TypeGraph.PONDERATED_NOT_ORIENTED || selectedGraph.getTypeGraph() == TypeGraph.PONDERATED_ORIENTED)
                this.constructWeightMatrix(selectedGraph);
            else {
                Alert alert = new Alert(AlertType.ERROR);
                      alert.setTitle("Displaying a Matrix");
                      alert.setHeaderText("Error...");
                      alert.setContentText("A non weighted graph Doesn't have a Weight Matrix.");
                      alert.showAndWait();
            }
        }
        else {
            Alert alert = new Alert(AlertType.ERROR);
                  alert.setTitle("Displaying a Matrix");
                  alert.setHeaderText("Error...");
                  alert.setContentText("First select a graph ,please .");
                  alert.showAndWait();
        }
    }
    
    @FXML
    void applyBellmanFord(ActionEvent event) {
        TreeItem<Drawable> selectedTreeItem = this.componentsTree.getSelectionModel().getSelectedItem();

        if(selectedTreeItem != null && selectedTreeItem.getValue() instanceof Graph2D){
            Graph targetedGraph = ((Graph2D) selectedTreeItem.getValue()).getGraph();
            
            ChoiceDialog<Vertex> dialogType = new ChoiceDialog<Vertex>(targetedGraph.getVertices().first(),
                                                                       (Vertex[])targetedGraph.getVertices().toArray(new Vertex[]{}));
                                 dialogType.setTitle("Djikskra's Algorithm");
                                 dialogType.setHeaderText("start Vertex");
                                 dialogType.setContentText("Please choose a start vertex: ");

            Optional<Vertex> resultType = dialogType.showAndWait();
                             
            if(resultType.isPresent()){
                ShortestPaths paths = new ShortestPaths();
                                 paths.setStratingNode(resultType.get());
            
                Relation[][] resu = paths.getShortestPaths(targetedGraph, resultType.get(),
                        paths.BellemanFord(targetedGraph, resultType.get()));
                ArrayList<PathMatrixLine> paths1 = new  ArrayList<PathMatrixLine>();

                for(Relation[] road : resu)
                    paths1.add(new PathMatrixLine(road[0].getLeftVertex(), road[road.length - 1].getRightVertex(), road));
                
                this.constructPathMatrix("BellemanFord's shortests paths ",paths1.toArray(new PathMatrixLine[paths1.size()]));
            }
            else {
                Alert alert = new Alert(AlertType.ERROR);
                      alert.setTitle("BellmanFord's Algorithm");
                      alert.setHeaderText("Error...");
                      alert.setContentText("Please choose a start vertex.");
                      alert.showAndWait();
            }
        }
        else {
            Alert alert = new Alert(AlertType.ERROR);
                  alert.setTitle("BellmanFord's Algorithm");
                  alert.setHeaderText("Error...");
                  alert.setContentText("First choose a graph, please.");
                  alert.showAndWait();
        }
    }

    @FXML
    void applyDjikskra(ActionEvent event) {
        TreeItem<Drawable> selectedTreeItem = this.componentsTree.getSelectionModel().getSelectedItem();

        if(selectedTreeItem != null && selectedTreeItem.getValue() instanceof Graph2D){
            Graph targetedGraph = ((Graph2D) selectedTreeItem.getValue()).getGraph();
            
            ChoiceDialog<Vertex> dialogType = new ChoiceDialog<Vertex>(targetedGraph.getVertices().first(),
                                                                       (Vertex[])targetedGraph.getVertices().toArray(new Vertex[]{}));
                                 dialogType.setTitle("Djikskra's Algorithm");
                                 dialogType.setHeaderText("Start vertex ");
                                 dialogType.setContentText("Please choose a start vertex: ");

            Optional<Vertex> resultType = dialogType.showAndWait();
                             
            if(resultType.isPresent()){
                ShortestPaths paths = new ShortestPaths();
                                 paths.setStratingNode(resultType.get());
            
                Relation[][] resu = paths.getShortestPaths(targetedGraph, resultType.get(),
                        paths.Djisktra(targetedGraph, resultType.get()));
                ArrayList<PathMatrixLine> paths1 = new  ArrayList<PathMatrixLine>();

                for(Relation[] road : resu)
                   paths1.add(new PathMatrixLine(road[0].getLeftVertex(), road[road.length - 1].getRightVertex(), road));
                
                this.constructPathMatrix("Djisktra's shortests paths", paths1.toArray(new PathMatrixLine[paths1.size()]));
            }
            else {
                Alert alert = new Alert(AlertType.ERROR);
                      alert.setTitle("Djikskra's Algorithm");
                      alert.setHeaderText("Error...");
                      alert.setContentText("Please choose a start vertex.");
                      alert.showAndWait();
            }
        }
        else {
            Alert alert = new Alert(AlertType.ERROR);
                  alert.setTitle("Djikskra's Algorithm");
                  alert.setHeaderText("Error...");
                  alert.setContentText("First choose a graph, please .");
                  alert.showAndWait();
        }
    }
    
    @FXML
    private void applyFloyd() {
        TreeItem<Drawable> selectedTreeItem = this.componentsTree.getSelectionModel().getSelectedItem();
                
        if(selectedTreeItem != null && selectedTreeItem.getValue() instanceof Graph2D){
            Graph targetedGraph = ((Graph2D) selectedTreeItem.getValue()).getGraph();
            ShortestPaths floyd = new ShortestPaths();
            ArrayList<ShortestPaths> resultat = floyd.FloydWarshall(targetedGraph);
            ArrayList<FloydDistancesMatrixLine> distances = new ArrayList<FloydDistancesMatrixLine>();
            ArrayList<FloydPredecessorMatrixLine> predecessors = new ArrayList<FloydPredecessorMatrixLine>();
            for(ShortestPaths l : resultat ){

                double[] dis = new double[l.getRoads().size()];
                int i = 0;
                Vertex[] vertexs = new Vertex[l.getRoads().size()];
                for(PathElement e : l.getRoads()){ 
                    dis[i] = e.getDistanceVertex();
                    if(e.getPredecessor() == null) 
                        vertexs[i] = new Vertex("NULL");
                    else vertexs[i] = e.getPredecessor();
                    i++;
                }
                distances.add(new FloydDistancesMatrixLine(dis));
                predecessors.add(new FloydPredecessorMatrixLine(vertexs));
            }
            
            this.constructFloydDistancesMatrix(targetedGraph, 
                    distances.toArray(new FloydDistancesMatrixLine[distances.size()]), 
                    predecessors.toArray(new FloydPredecessorMatrixLine[predecessors.size()])
            );
        }
        else {
            Alert alert = new Alert(AlertType.ERROR);
                  alert.setTitle("Floyd's algorithm");
                  alert.setHeaderText("Error...");
                  alert.setContentText("First select a graph.");
                  alert.showAndWait();
        }
    }

    @FXML
    void applyKruskal(ActionEvent event) throws NoVertex2DException, NoVertex2DException, RelationMismatchException {
        TreeItem<Drawable> selectedTreeItem = this.componentsTree.getSelectionModel().getSelectedItem();

        if(selectedTreeItem != null && selectedTreeItem.getValue() instanceof Graph2D){
            Graph targetedGraph = ((Graph2D) selectedTreeItem.getValue()).getGraph();
           Graph tree = targetedGraph.kruskal();  

            this.createCopyGraph2DNode(tree);
        }
        else {
            Alert alert = new Alert(AlertType.ERROR);
                  alert.setTitle("Displaying a  Matrix");
                  alert.setHeaderText("Error...");
                  alert.setContentText("First select a graph, please .");
                  alert.showAndWait();
        }
    }

    @FXML
    void applyPrim(ActionEvent event) throws NoVertex2DException, RelationMismatchException {
      
        TreeItem<Drawable> selectedTreeItem = this.componentsTree.getSelectionModel().getSelectedItem();

        if(selectedTreeItem != null && selectedTreeItem.getValue() instanceof Graph2D){

            Graph targetedGraph = ((Graph2D) selectedTreeItem.getValue()).getGraph();
            Graph tree = targetedGraph.prim();
            this.createCopyGraph2DNode(tree);
        }
        else {
            Alert alert = new Alert(AlertType.ERROR);
                  alert.setTitle("Displaying a matrix");
                  alert.setHeaderText("Error...");
                  alert.setContentText("First select a graph,please .");
                  alert.showAndWait();
        }
    }

    @FXML
    void applyWelshPowel(ActionEvent event) {
        //add colors to vertices 
        TreeItem<Drawable> selectedTreeItem = this.componentsTree.getSelectionModel().getSelectedItem();

        if(selectedTreeItem != null && selectedTreeItem.getValue() instanceof Graph2D){
            
            Graph targetedGraph = ((Graph2D) selectedTreeItem.getValue()).getGraph();
            Colouration colouration = new Colouration(targetedGraph);
            int i = 0;
            for(CopyOnWriteArrayList<Vertex> sameColor : colouration.getSameColourVerticesList()){
                Color color = colouration.getColours().get(i);
                for(Vertex vertex : sameColor){
                    vertex.getVertex2D().setFill(color);
                }
                i++;
            }
            
        }
        else {
            Alert alert = new Alert(AlertType.ERROR);
                  alert.setTitle("Displaying a Matrix");
                  alert.setHeaderText("Error...");
                  alert.setContentText("First select a graph ,please.");
                  alert.showAndWait();
        }
    }
 
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            this.initComponentsTree();
            initColorChooser();
        } catch (RelationMismatchException ex) {
            Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Vertex a = new Vertex("A");
        Vertex b = new Vertex("B");
        Vertex c = new Vertex("C");
        Vertex d = new Vertex("D");
        Vertex e = new Vertex("E");
        Vertex f = new Vertex("F");
        
        Relation e1 = new WeightedEdge("e1", 6, a, c);
        Relation e2 = new WeightedEdge("e2", 2, a, d);
        Relation e3 = new WeightedEdge("e3", 3, b, a);
        Relation e4 = new WeightedEdge("e4", 3, c, e);
        Relation e5 = new WeightedEdge("e5", 1, c, f);
        Relation e6 = new WeightedEdge("e6", 2, d, b);
        Relation e7 = new WeightedEdge("e7", 2, e, a);
        Relation e8 = new WeightedEdge("e8", 1, f, e);
        Relation e9 = new WeightedEdge("e9", 1, f, d);
        Relation e10 = new WeightedEdge("e10", 1, c, d);
        Relation e11 = new WeightedEdge("e11", 3, d, c);
        
        try {
            Graph g = new Graph("G", a, b, c, d, e, f);
                   g.setTypeGraph(TypeGraph.PONDERATED_ORIENTED);
            this.createGraph2DNode(g);
            
        } catch (RelationMismatchException ex) {
            Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoVertex2DException ex) {
            Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void initComponentsTree() throws RelationMismatchException {
        TreeItem<Drawable> rootItem = new TreeItem<Drawable> (new Drawable(){
            @Override
            public String UIBehavior() {
                return "Yeaaah it's working !";
            }
        });
        rootItem.setExpanded(true);
        this.componentsTree.setCellFactory(new Callback<TreeView<Drawable>, TreeCell<Drawable>>() {
            @Override
            public TreeCell<Drawable> call(TreeView<Drawable> param) {
                return new TreeCell<Drawable>(){
                    @Override
                    protected void updateItem(Drawable item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        }
                        else {
                            String imagePath = "/MainPackage/MVC/Icons/GraphTree.png";
                            
                            if(item instanceof Vertex2D)
                                imagePath = "/MainPackage/MVC/Icons/CircleTree.png";
                            else if(item instanceof Relation2D)
                                imagePath = "/MainPackage/MVC/Icons/RelationTree.png";
                            
                            setGraphic(new ImageView(new Image(getClass().getResourceAsStream(imagePath))));
                            setText(item.UIBehavior());
                        }
                    } 
                };
            }
        });
        this.componentsTree.setShowRoot(false);
        this.componentsTree.setRoot(rootItem);
        this.componentsTree.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.componentsTree.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<Drawable>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<Drawable>> observable, TreeItem<Drawable> oldValue, TreeItem<Drawable> newValue) {
                if(newValue != null && componentsTree.getSelectionModel().getSelectedItems().size() == 1){
                    Drawable item = newValue.getValue();

                    if(item instanceof Vertex2D)
                        colorChooser.setValue((Color)((Vertex2D) item).getFill());
                    else if(item instanceof Relation2D)
                        colorChooser.setValue((Color)(((Relation2D) item).getStroke()));
                }
            }
        });
    }
    
    private void initColorChooser(){
        this.colorChooser.valueProperty().addListener(new ChangeListener<Color>() {
            @Override
            public void changed(ObservableValue<? extends Color> observable, Color oldValue, Color newValue) {
                if(newValue != null){
                    ObservableList<TreeItem<Drawable>> selectedDrawables = componentsTree.getSelectionModel().getSelectedItems();
                    
                    if(selectedDrawables != null)
                        for(TreeItem<Drawable> drawableTreeItem: selectedDrawables){
                            Drawable drawable = drawableTreeItem.getValue();

                            if(drawable instanceof Vertex2D)
                                ((Vertex2D) drawable).setFill(newValue);
                            else if(drawable instanceof Relation2D)
                                ((Relation2D) drawable).setStroke(newValue);
                        }
                }
            }
        });
    }
    
    private void createGraph2DNode(Graph graph) throws NoVertex2DException{
        Graph2D graph2D = new Graph2D(graph);
        
        TreeItem<Drawable> graphTreeItem = new TreeItem<Drawable>(graph2D);
                           graphTreeItem.setGraphic(workSpace);
                           graphTreeItem.setExpanded(true);
        
        for(Vertex vertex: graph.getVertices())
            graph2D.getEnsembleVertex().add(new Vertex2D(vertex));
        
        for(Relation relation: graph.getRelations())
            this.workSpace.getChildren().add(new Relation2D(relation));
        
        for(Vertex vertex: graph.getVertices())
            this.workSpace.getChildren().add(vertex.getVertex2D());
        
        for(Vertex vertex: graph.getVertices()){
            TreeItem<Drawable> vertexTreeItem = new TreeItem<Drawable>(vertex.getVertex2D());
                               vertexTreeItem.setExpanded(true);
            
            for(Relation relation: graph.getRelations()){
                if(relation.isIncidentTo(vertex)){
                    TreeItem<Drawable> relationTreeItem = new TreeItem<Drawable>(relation.getRelation2D());
                                       relationTreeItem.setExpanded(true);
                    vertexTreeItem.getChildren().add(relationTreeItem);
                }
                graph2D.getEnsembleRelation().add(relation.getRelation2D());
            }
            
            graphTreeItem.getChildren().add(vertexTreeItem);
        }
                
        this.componentsTree.getRoot().getChildren().add(graphTreeItem);
        this.componentsTree.getSelectionModel().clearSelection();
        this.componentsTree.getSelectionModel().select(graphTreeItem);
    }
    private void createCopyGraph2DNode(Graph graph) throws NoVertex2DException, RelationMismatchException{
        List<Vertex> ancientVertexs = new ArrayList<Vertex>();
                          ancientVertexs = (List<Vertex>)
                                  Arrays.asList((Vertex[])graph.getVertices().toArray(new Vertex[]{}));
                                      
        Vertex[] newVertexs = new Vertex[graph.getVertices().size()];
            
        for(int i = 0; i < newVertexs.length; i++)
            newVertexs[i] = new Vertex(ancientVertexs.get(i).getLabel());
        
        switch(graph.getTypeGraph()){
            case NOT_PONDERATED_NOT_ORIENTED:
                for(Relation relation: graph.getRelations()){
                    new Edge(relation.getLabel(), newVertexs[ancientVertexs.indexOf(relation.getLeftVertex())], 
                                                   newVertexs[ancientVertexs.indexOf(relation.getRightVertex())]);
                }
                break;
            case NOT_PONDERATED_ORIENTED:
                for(Relation relation: graph.getRelations()){
                    new Arc(relation.getLabel(), newVertexs[ancientVertexs.indexOf(relation.getLeftVertex())], 
                                                   newVertexs[ancientVertexs.indexOf(relation.getRightVertex())]);
                }
                break;
            case PONDERATED_NOT_ORIENTED:
                for(Relation relation: graph.getRelations()){
                    new WeightedEdge(relation.getLabel(), relation.getWeight(), 
                            newVertexs[ancientVertexs.indexOf(relation.getLeftVertex())], 
                            newVertexs[ancientVertexs.indexOf(relation.getRightVertex())]);
                }
                break;
            case PONDERATED_ORIENTED:
                for(Relation relation: graph.getRelations()){
                    new WeightedArc(relation.getLabel(), relation.getWeight(), 
                            newVertexs[ancientVertexs.indexOf(relation.getLeftVertex())], 
                            newVertexs[ancientVertexs.indexOf(relation.getRightVertex())]);
                }
                break;
        }
        
        Graph newTree = new Graph(graph.getNameGraph(), newVertexs);
               newTree.setTypeGraphProperty(graph.getTypeGraphProperty());
               
        this.createGraph2DNode(newTree);
    }
    
    private void deleteGraph2DNode(TreeItem<Drawable> graphe2DTi){        
            for(Vertex2D s: ((Graph2D)graphe2DTi.getValue()).getEnsembleVertex()){
                this.workSpace.getChildren().remove(s);
                this.workSpace.getChildren().remove(s.getLabel());
            }
            for(Relation2D r: ((Graph2D)graphe2DTi.getValue()).getEnsembleRelation()){
                this.workSpace.getChildren().remove(r);
                this.workSpace.getChildren().remove(r.getLabel());
                if(r.getArrow() != null){
                    this.workSpace.getChildren().remove(r.getArrow().getHighestStroke());
                    this.workSpace.getChildren().remove(r.getArrow().getLowestStroke());
                }
            }
            
            this.componentsTree.getRoot().getChildren().remove(graphe2DTi);
            ((Graph2D)graphe2DTi.getValue()).getGraph().setGraph2D(null);
    }
    
    private void deleteVertex2DNode(TreeItem<Drawable> vertex2DTi) throws RelationMismatchException, NoVertex2DException{
        Vertex2D vertex2D = (Vertex2D) vertex2DTi.getValue();
        Graph2D graph2D = (Graph2D) (vertex2DTi.getParent().getValue());
        Graph graph = graph2D.getGraph();
        
        Vertex[] ancientVertexs = new Vertex[graph.getVertices().size()];
                 ancientVertexs = graph.getVertices().toArray(ancientVertexs);
    
        Vertex[] newVertexs = new Vertex[graph.getVertices().size() - 1];
        
        for(int i = 0,j = 0; i < ancientVertexs.length; i++)
            if(ancientVertexs[i] != vertex2D.getVertex())
                newVertexs[j++] = ancientVertexs[i];
        
        Graph newGraph = new Graph(graph.getNameGraph(), newVertexs);
               newGraph.setTypeGraphProperty(graph.getTypeGraphProperty());
        
        this.deleteGraph2DNode(vertex2DTi.getParent());
        this.createGraph2DNode(newGraph);
    }
    
    private void deleteRelation2DNode(TreeItem<Drawable> relation2DTi) throws RelationMismatchException, NoVertex2DException {
        Relation2D relation2D = (Relation2D) relation2DTi.getValue();
        Graph2D graph2D = (Graph2D) (relation2DTi.getParent().getParent().getValue());
        Graph graph = graph2D.getGraph();

        Relation[] ancientRelations = new Relation[graph.getRelations().size()];
                   ancientRelations = graph.getRelations().toArray(ancientRelations);
                   
        Relation[] newRelations = new Relation[graph.getRelations().size() - 1];
        
        for(int i = 0,j = 0; i < ancientRelations.length; i++)
            if(ancientRelations[i] != relation2D.getRelation())
                newRelations[j++] = ancientRelations[i];
        
        Graph newGraph = new Graph(graph.getNameGraph(), newRelations);
               newGraph.setTypeGraphProperty(graph.getTypeGraphProperty());
        
        this.deleteGraph2DNode(relation2DTi.getParent().getParent());
        this.createGraph2DNode(newGraph);
    }
    
    private boolean existGraph2DNode(String name){
        for(TreeItem<Drawable> ti : this.componentsTree.getRoot().getChildren())
            if(((Graph2D)(ti.getValue())).getGraph().getNameGraph().equals(name))
                return true;
        return false;
    }
    
    private boolean existRelation2DNode(String name){
        for(TreeItem<Drawable> ti : this.componentsTree.getRoot().getChildren())
            for(Relation2D rel2D: ((Graph2D)ti.getValue()).getEnsembleRelation())
                if(rel2D.getRelation().getLabel().equals(name))
                    return true;
        return false;
    }
    
    private boolean existVertex2DNode(String name){
        for(TreeItem<Drawable> ti : this.componentsTree.getRoot().getChildren())
            for(Vertex2D som2D: ((Graph2D)ti.getValue()).getEnsembleVertex())
                if(som2D.getLabel().getText().equals(name))
                    return true;
        return false;
    }
    
    private void replaceGraph2DNode(String newVertex, TreeItem<Drawable> graphe2DTi) throws NoVertex2DException, RelationMismatchException{
        Graph ancientG = ((Graph2D)graphe2DTi.getValue()).getGraph();
        
        Vertex[] ancientVertexs = new Vertex[ancientG.getVertices().size()];
                 ancientVertexs = ancientG.getVertices().toArray(ancientVertexs);
                         
        Vertex[] newVertexs = new Vertex[ancientVertexs.length + 1];
                
        for(int i = 0; i < ancientVertexs.length; i++)
            newVertexs[i] = ancientVertexs[i];
                
            newVertexs[newVertexs.length - 1] = new Vertex(newVertex);
                
        Graph newG = new Graph(ancientG.getNameGraph(), newVertexs);
               newG.setTypeGraphProperty(ancientG.getTypeGraphProperty());
                
        this.deleteGraph2DNode(graphe2DTi);
        this.createGraph2DNode(newG);
    }
    
    private void replaceGraph2DNode(String relationName, double weight, TreeItem<Drawable> s2dgti, TreeItem<Drawable> s2ddti) throws RelationMismatchException, NoVertex2DException{        
        Vertex2D s2dg = (Vertex2D) s2dgti.getValue();
        Vertex2D s2dd = (Vertex2D) s2ddti.getValue();
                
        Graph ancientGraph = ((Graph2D) (s2dgti.getParent().getValue())).getGraph();
        Relation newRelation = null;
        
        switch (ancientGraph.getTypeGraph()) {
            case NOT_PONDERATED_NOT_ORIENTED:
                newRelation = new Edge(relationName, s2dg.getVertex(), s2dd.getVertex());
                break;
            case NOT_PONDERATED_ORIENTED:
                newRelation = new Arc(relationName, s2dg.getVertex(), s2dd.getVertex());
                break;
            case PONDERATED_NOT_ORIENTED:
                newRelation = new WeightedEdge(relationName, weight, s2dg.getVertex(), s2dd.getVertex());
                break;
            default:
                newRelation = new WeightedArc(relationName, weight, s2dg.getVertex(), s2dd.getVertex());
                break;
        }
        
        Vertex[] newVertexs = new Vertex[ancientGraph.getVertices().size()];
                 newVertexs = ancientGraph.getVertices().toArray(newVertexs);
                 
        Graph newGraph = new Graph(ancientGraph.getNameGraph(), newVertexs);
               newGraph.setTypeGraphProperty(ancientGraph.getTypeGraphProperty());
        
        this.deleteGraph2DNode(s2dgti.getParent());
        this.createGraph2DNode(newGraph);
    }

    private void constructAdjancenseMatrix(Graph graph){        
        boolean[][] matrix = graph.getAdjacencyMatrix().getRelationsMatrix();
        
        Vertex[] vertexs = new Vertex[graph.getVertices().size()];
                 vertexs = graph.getVertices().toArray(vertexs);
        
        LinkedList<AdjacenseMatrixLine> lines = new LinkedList<AdjacenseMatrixLine>();
        
        for(int i = 0; i < matrix.length; i++)
            lines.add(new AdjacenseMatrixLine(vertexs[i].getLabel(), matrix[i]));
        
        int nbColumn = matrix[0].length;
        
        TableView<AdjacenseMatrixLine> table = new TableView<AdjacenseMatrixLine>();
                           
        for(int i = 0; i < nbColumn + 1; i++){
            final int k = i;
            if(i == 0){
                TableColumn<AdjacenseMatrixLine, String> columnName = new TableColumn<AdjacenseMatrixLine,String>();
                                                         columnName.setPrefWidth(50.0);
                                                         columnName.setStyle("-fx-alignment: center; -fx-pref-height: 50;");
                                                         columnName.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<AdjacenseMatrixLine, String>, ObservableValue<String>>() {
                                                             @Override
                                                             public ObservableValue<String> call(TableColumn.CellDataFeatures<AdjacenseMatrixLine, String> param) {
                                                                 return new SimpleStringProperty(param.getValue().getVertexName());
                                                             }
                                                         });
                table.getColumns().add(columnName);
            }
            else {
                TableColumn<AdjacenseMatrixLine, Boolean> columnI = new TableColumn<AdjacenseMatrixLine, Boolean>();
                                                          columnI.setText(vertexs[i - 1].getLabel());
                                                          columnI.setPrefWidth(50.0);
                                                          columnI.setCellFactory(new Callback<TableColumn<AdjacenseMatrixLine, Boolean>, TableCell<AdjacenseMatrixLine, Boolean>>() {
                                                                @Override
                                                                public TableCell<AdjacenseMatrixLine, Boolean> call(TableColumn<AdjacenseMatrixLine, Boolean> param) {
                                                                    return new TableCell<AdjacenseMatrixLine, Boolean>(){
                                                                        @Override
                                                                        protected void updateItem(Boolean item,boolean empty){
                                                                            super.updateItem(item, empty);
                                                                            if(empty){
                                                                                setText(null);
                                                                                setGraphic(null);
                                                                            }
                                                                            else{
                                                                                CheckBox cell = new CheckBox();
                                                                                         cell.setSelected(item.booleanValue());
                                                                                         cell.setDisable(true);
                                                                                         cell.setOpacity(1.0);
                                                                                setGraphic(cell);
                                                                            }
                                                                        }
                                                                    };
                                                                }
                                                          });
                                                          columnI.setStyle("-fx-alignment: center; -fx-pref-height: 50;");
                                                          columnI.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<AdjacenseMatrixLine, Boolean>, ObservableValue<Boolean>>() {
                                                                @Override
                                                                public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<AdjacenseMatrixLine, Boolean> param) {
                                                                    return new SimpleBooleanProperty(param.getValue().getRelations()[k - 1]).asObject();
                                                                }
                                                          });
                table.getColumns().add(columnI);
            }
        }
        
        table.getItems().addAll(lines);
        
        BorderPane borderPane = new BorderPane();
                   borderPane.setCenter(table);
        Stage stage = new Stage();
              stage.setScene(new Scene(borderPane, (nbColumn + 2) * 50, (lines.size() + 1) * 50));
              stage.setTitle("Matrice d'adjacense");
              stage.setResizable(false);
              stage.show();
    }
    
    private void constructIncidenceMatrix(Graph graph) {

        Integer[][] matrix = graph.getIncidenceMatrix().getRelationsMatrix();
        
        Vertex[] vertexs = new Vertex[graph.getVertices().size()];
                 vertexs = graph.getVertices().toArray(vertexs);
        Relation[] relations = new Relation[graph.getRelations().size()];
                   relations = graph.getRelations().toArray(relations);
        
        LinkedList<IncidenceMatrixLine> lines = new LinkedList<IncidenceMatrixLine>();
        
        for(int i = 0; i < matrix.length; i++)
            lines.add(new IncidenceMatrixLine(vertexs[i].getLabel(), matrix[i]));
        
        int nbColumn = matrix[0].length;
        
        TableView<IncidenceMatrixLine> table = new TableView<IncidenceMatrixLine>();
                           
        for(int i = 0; i < nbColumn + 1; i++){
            final int k = i;
            if(i == 0){
                TableColumn<IncidenceMatrixLine, String> columnName = new TableColumn<IncidenceMatrixLine,String>();
                                                         columnName.setPrefWidth(50.0);
                                                         columnName.setStyle("-fx-alignment: center; -fx-pref-height: 50;");
                                                         columnName.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<IncidenceMatrixLine, String>, ObservableValue<String>>() {
                                                             @Override
                                                             public ObservableValue<String> call(TableColumn.CellDataFeatures<IncidenceMatrixLine, String> param) {
                                                                 return new SimpleStringProperty(param.getValue().getVertexName());
                                                             }
                                                         });
                table.getColumns().add(columnName);
            }
            else {
                TableColumn<IncidenceMatrixLine, Integer> columnI = new TableColumn<IncidenceMatrixLine, Integer>();
                                                          columnI.setText(relations[i - 1].getLabel());
                                                          columnI.setPrefWidth(50.0);
                                                          columnI.setCellFactory(new Callback<TableColumn<IncidenceMatrixLine, Integer>, TableCell<IncidenceMatrixLine, Integer>>() {
                                                                @Override
                                                                public TableCell<IncidenceMatrixLine, Integer> call(TableColumn<IncidenceMatrixLine, Integer> param) {
                                                                    return new TableCell<IncidenceMatrixLine, Integer>(){
                                                                        @Override
                                                                        protected void updateItem(Integer item,boolean empty){
                                                                            super.updateItem(item, empty);
                                                                            if(empty || item.intValue() == 0){
                                                                                setText(null);
                                                                                setGraphic(null);
                                                                            }
                                                                            else{
                                                                                Circle cell = new Circle();
                                                                                       cell.setRadius(5.0);
                                                                                       cell.setFill(item.intValue() == 1 ? Paint.valueOf("#0000FF") : item.intValue() == -1.0 ? Paint.valueOf("#FF0000") : Paint.valueOf("#FFFFFF"));
                                                                                setGraphic(cell);
                                                                            }
                                                                        }
                                                                    };
                                                                }
                                                          });
                                                          columnI.setStyle("-fx-alignment: center; -fx-pref-height: 50;");
                                                          columnI.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<IncidenceMatrixLine, Integer>, ObservableValue<Integer>>() {
                                                                @Override
                                                                public ObservableValue<Integer> call(TableColumn.CellDataFeatures<IncidenceMatrixLine, Integer> param) {
                                                                    return new SimpleIntegerProperty(param.getValue().getRelations()[k - 1]).asObject();
                                                                }
                                                          });
                table.getColumns().add(columnI);
            }
        }
        
        table.getItems().addAll(lines);
        
        BorderPane borderPane = new BorderPane();
                   borderPane.setCenter(table);
        Stage stage = new Stage();
              stage.setScene(new Scene(borderPane, (nbColumn + 2) * 50, (lines.size() + 1) * 50));
              stage.setTitle("Adjacency matrix");
              stage.setResizable(false);
              stage.show();
    }
    
    private void constructWeightMatrix(Graph graph) {
    	
        Double[][] matrix = graph.getWeightMatrix().getMatriceRelations();
        
        Vertex[] vertexs = new Vertex[graph.getVertices().size()];
                 vertexs = graph.getVertices().toArray(vertexs);
        
        LinkedList<WeightMatrixLine> lines = new LinkedList<WeightMatrixLine>();
        
        for(int i = 0; i < matrix.length; i++)
            lines.add(new WeightMatrixLine(vertexs[i].getLabel(), matrix[i]));
        
        int nbColumn = matrix[0].length;
        
        TableView<WeightMatrixLine> table = new TableView<WeightMatrixLine>();
                           
        for(int i = 0; i < nbColumn + 1; i++){
            final int k = i;
            if(i == 0){
                TableColumn<WeightMatrixLine, String> columnName = new TableColumn<WeightMatrixLine,String>();
                                                         columnName.setPrefWidth(50.0);
                                                         columnName.setStyle("-fx-alignment: center; -fx-pref-height: 50;");
                                                         columnName.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<WeightMatrixLine, String>, ObservableValue<String>>() {
                                                             @Override
                                                             public ObservableValue<String> call(TableColumn.CellDataFeatures<WeightMatrixLine, String> param) {
                                                                 return new SimpleStringProperty(param.getValue().getVertexName());
                                                             }
                                                         });
                table.getColumns().add(columnName);
            }
            else {
                TableColumn<WeightMatrixLine, String> columnI = new TableColumn<WeightMatrixLine, String>();
                                                          columnI.setText(vertexs[i - 1].getLabel());
                                                          columnI.setPrefWidth(50.0);
                                                          columnI.setStyle("-fx-alignment: center; -fx-pref-height: 50;");
                                                          columnI.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<WeightMatrixLine, String>, ObservableValue<String>>() {
                                                                @Override
                                                                public ObservableValue<String> call(TableColumn.CellDataFeatures<WeightMatrixLine, String> param) {
                                                                    return (new SimpleStringProperty(param.getValue().getRelations()[k - 1] != null ? Double.toString(param.getValue().getRelations()[k - 1]) : ""));
                                                                }
                                                          });
                table.getColumns().add(columnI);
            }
        }
        
        table.getItems().addAll(lines);
        
        BorderPane borderPane = new BorderPane();
                   borderPane.setCenter(table);
        Stage stage = new Stage();
              stage.setScene(new Scene(borderPane, (nbColumn + 2) * 50, (lines.size() + 1) * 50));
              stage.setTitle("Adjacency matrix");
              stage.setResizable(false);
              stage.show();
    }

    private void constructPathMatrix(String stageTitle, PathMatrixLine ... paths){
        int nbColumn = 0;
        
        for(PathMatrixLine path: paths)
            if(nbColumn < path.getPath().length)
                nbColumn = path.getPath().length;
        
        TableView<PathMatrixLine> table = new TableView<PathMatrixLine>();
                           
        for(int i = 0; i < nbColumn + 1; i++){
            final int k = i;
            if(i == 0){
                TableColumn<PathMatrixLine, String> columnName = new TableColumn<PathMatrixLine,String>();
                                                         columnName.setPrefWidth(150.0);
                                                         columnName.setText("Paths \\ Steps ");
                                                         columnName.setStyle("-fx-alignment: center; -fx-pref-height: 50;");
                                                         columnName.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PathMatrixLine, String>, ObservableValue<String>>() {
                                                             @Override
                                                             public ObservableValue<String> call(TableColumn.CellDataFeatures<PathMatrixLine, String> param) {
                                                                 return new SimpleStringProperty("[ " + param.getValue().getStart().getLabel() + " ; " + param.getValue().getTarget().getLabel() + " ]");
                                                             }
                                                         });
                table.getColumns().add(columnName);
            }
            else {
                TableColumn<PathMatrixLine, String> columnI = new TableColumn<PathMatrixLine, String>();
                                                          columnI.setText("Step n° = " + i);
                                                          columnI.setPrefWidth(100.0);
                                                          columnI.setStyle("-fx-alignment: center; -fx-pref-height: 50;");
                                                          columnI.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PathMatrixLine, String>, ObservableValue<String>>() {
                                                                @Override
                                                                public ObservableValue<String> call(TableColumn.CellDataFeatures<PathMatrixLine, String> param) {
                                                                    return (new SimpleStringProperty(((k - 1) < param.getValue().getPath().length) ? param.getValue().getPath()[k - 1].getLabel() + " = ( " + param.getValue().getPath()[k - 1].getLeftVertex().getLabel() + " ; " + param.getValue().getPath()[k - 1].getRightVertex().getLabel() + " )" : ""));
                                                                }
                                                          });
                table.getColumns().add(columnI);
            }
        }
        
        table.getItems().addAll(paths);
        
        BorderPane borderPane = new BorderPane();
                   borderPane.setCenter(table);
        Stage stage = new Stage();
              stage.setScene(new Scene(borderPane, (nbColumn + 2) * 100, (paths.length + 1) * 50));
              stage.setTitle(stageTitle);
              stage.setResizable(false);
              stage.show();
    }
    
    private void constructFloydDistancesMatrix(Graph graph, FloydDistancesMatrixLine[] distancesLines, FloydPredecessorMatrixLine[] predecessorsLines){
        final Vertex[] vertexs  = (Vertex[]) graph.getVertices().toArray(new Vertex[]{});
        
        int nbColumn = 0;
        
        for(FloydDistancesMatrixLine path: distancesLines)
            if(nbColumn < path.getDistances().length)
                nbColumn = path.getDistances().length;
        
        TableView<FloydDistancesMatrixLine> distancesTable = new TableView<FloydDistancesMatrixLine>();
        TableView<FloydPredecessorMatrixLine> predecessorsTable = new TableView<FloydPredecessorMatrixLine>();
                           
        for(int i = 0; i < nbColumn + 1; i++){
            final int k = i;
            
            if(i == 0){
                TableColumn<FloydDistancesMatrixLine, String> distancesColumnName = new TableColumn<FloydDistancesMatrixLine,String>();
                                                              distancesColumnName.setPrefWidth(150.0);
                                                              distancesColumnName.setText("Start \\ End");
                                                              distancesColumnName.setStyle("-fx-alignment: center; -fx-pref-height: 50;");
                                                              distancesColumnName.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<FloydDistancesMatrixLine, String>, ObservableValue<String>>() {
                                                                  @Override
                                                                  public ObservableValue<String> call(TableColumn.CellDataFeatures<FloydDistancesMatrixLine, String> param) {
                                                                      return new SimpleStringProperty(vertexs[distancesTable.getItems().indexOf(param.getValue())].getLabel());
                                                                  }
                                                              });
                                                              
                TableColumn<FloydPredecessorMatrixLine, String> predecessorColumnName = new TableColumn<FloydPredecessorMatrixLine,String>();
                                                                predecessorColumnName.setPrefWidth(150.0);
                                                                predecessorColumnName.setText("Start \\ End");
                                                                predecessorColumnName.setStyle("-fx-alignment: center; -fx-pref-height: 50;");
                                                                predecessorColumnName.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<FloydPredecessorMatrixLine, String>, ObservableValue<String>>() {
                                                                    @Override
                                                                    public ObservableValue<String> call(TableColumn.CellDataFeatures<FloydPredecessorMatrixLine, String> param) {
                                                                        return new SimpleStringProperty(vertexs[predecessorsTable.getItems().indexOf(param.getValue())].getLabel());
                                                                    }
                                                                });
                                                              
                distancesTable.getColumns().add(distancesColumnName);
                predecessorsTable.getColumns().add(predecessorColumnName);
            }
            else {
                TableColumn<FloydDistancesMatrixLine, String> distancesColumnI = new TableColumn<FloydDistancesMatrixLine, String>();
                                                              distancesColumnI.setText(vertexs[k - 1].getLabel());
                                                              distancesColumnI.setPrefWidth(100.0);
                                                              distancesColumnI.setStyle("-fx-alignment: center; -fx-pref-height: 50;");
                                                              distancesColumnI.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<FloydDistancesMatrixLine, String>, ObservableValue<String>>() {
                                                                    @Override
                                                                    public ObservableValue<String> call(TableColumn.CellDataFeatures<FloydDistancesMatrixLine, String> param) {
                                                                        return (new SimpleStringProperty((k - 1) < param.getValue().getDistances().length ? param.getValue().getDistances()[k - 1] + "" : ""));
                                                                    }
                                                              });
                
                TableColumn<FloydPredecessorMatrixLine, String> predecessorColumnI = new TableColumn<FloydPredecessorMatrixLine, String>();
                                                                predecessorColumnI.setText(vertexs[k - 1].getLabel());
                                                                predecessorColumnI.setPrefWidth(100.0);
                                                                predecessorColumnI.setStyle("-fx-alignment: center; -fx-pref-height: 50;");
                                                                predecessorColumnI.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<FloydPredecessorMatrixLine, String>, ObservableValue<String>>() {
                                                                    @Override
                                                                    public ObservableValue<String> call(TableColumn.CellDataFeatures<FloydPredecessorMatrixLine, String> param) {
                                                                        return new SimpleStringProperty((k - 1) < param.getValue().getPredecessors().length ? param.getValue().getPredecessors()[k - 1].getLabel() : "");
                                                                    }
                                                                });
                                                                
                distancesTable.getColumns().add(distancesColumnI);
                predecessorsTable.getColumns().add(predecessorColumnI);
            }
        }
        
        distancesTable.getItems().addAll(distancesLines);
        predecessorsTable.getItems().addAll(predecessorsLines);
        
        SplitPane borderPane = new SplitPane();
                  borderPane.getItems().addAll(new BorderPane(distancesTable), new BorderPane(predecessorsTable));
                  borderPane.setDividerPositions(0.5);
                  borderPane.setOrientation(Orientation.HORIZONTAL);
                   
        Stage stage = new Stage();
              stage.setScene(new Scene(borderPane, 2 * (nbColumn + 2) * 100, (distancesLines.length + 1) * 55));
              stage.setTitle("Floyd's algorithm");
              stage.setResizable(false);
              stage.show();
    }
    
    private class AdjacenseMatrixLine {
        private String vertexName = null;
        private boolean[] relations = null;
        
        public AdjacenseMatrixLine(){};
        
        public AdjacenseMatrixLine(String vertexName, boolean[] relations){
            this.vertexName = vertexName;
            this.relations = relations;
        }
        
        public boolean[] getRelations(){
            return this.relations;
        }
        
        public void setRelations(boolean[] relations){
            this.relations = relations;
        }

        public String getVertexName() {
            return vertexName;
        }

        public void setVertexName(String vertexName) {
            this.vertexName = vertexName;
        }        
    }
    
    private class IncidenceMatrixLine {
        private String vertexName = null;
        private Integer[] relations = null;
        public IncidenceMatrixLine(){};
        
        public IncidenceMatrixLine(String vertexName, Integer[] relations){
            this.vertexName = vertexName;
            this.relations = relations;
        }
        
        public Integer[] getRelations(){
            return this.relations;
        }
        
        public void setRelations(Integer[] relations){
            this.relations = relations;
        }

        public String getVertexName() {
            return vertexName;
        }

        public void setVertexName(String vertexName) {
            this.vertexName = vertexName;
        }        
    }
    
    private class WeightMatrixLine {
        private String vertexName = null;
        private Double[] relations = null;
        
        public WeightMatrixLine(){};
        public WeightMatrixLine(String vertexName, Double[] relations){
            this.vertexName = vertexName;
            this.relations = relations;
        }
        
        public Double[] getRelations(){
            return this.relations;
        }
        
        public void setRelations(Double[] relations){
            this.relations = relations;
        }

        public String getVertexName() {
            return vertexName;
        }

        public void setVertexName(String vertexName) {
            this.vertexName = vertexName;
        }        
    }
    
    private class PathMatrixLine {
        private Vertex start = null,
                       target = null;
        private Relation[] path = null;
    
        public PathMatrixLine(Vertex start, Vertex target, Relation ... path){
            this.start = start;
            this.target = target;
            this.path = path;
        }

        public Relation[] getPath() {
            return path;
        }

        public void setPath(Relation[] path) {
            this.path = path;
        }

        public Vertex getStart() {
            return start;
        }

        public void setStart(Vertex start) {
            this.start = start;
        }

        public Vertex getTarget() {
            return target;
        }

        public void setTarget(Vertex target) {
            this.target = target;
        }
    }
    private class FloydDistancesMatrixLine {
        private double[] distances = null;
        
        public FloydDistancesMatrixLine(double ... distances){
            this.distances = distances;
        }

        public double[] getDistances() {
            return this.distances;
        }

        public void setPath(double[] distances) {
            this.distances = distances;
        }
    }
    
    private class FloydPredecessorMatrixLine {
        private Vertex[] predecessors = null;
        
        public FloydPredecessorMatrixLine(Vertex[] predecessors){
            this.predecessors = predecessors;
        }

        public Vertex[] getPredecessors() {
            return predecessors;
        }

        public void setPredecessors(Vertex[] predecessor) {
            this.predecessors = predecessor;
        }
    }
}
