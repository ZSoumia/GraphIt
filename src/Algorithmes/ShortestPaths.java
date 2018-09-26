package Algorithmes;

import MainPackage.API.Composants.Autres.Graph;
import MainPackage.API.Composants.Autres.Vertex;
import MainPackage.API.Composants.Liaison.Relation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;

 public class ShortestPaths {
    
    private Vertex stratingNode;
    private ArrayList<PathElement> roads;
    
    public ShortestPaths(){}
    
    public ShortestPaths(Vertex sNode,ArrayList<PathElement> r){
        this.roads = r;
        this.stratingNode = sNode;
        
    }
    
    //------------------------ utilities --------------------------------------
    public Relation[] getPath(ArrayList<PathElement> elemts ,Vertex arrivee,Graph g){
        ArrayList<Relation> resu1 = new ArrayList<Relation>();
        int i = 0;

        PathElement arriveeE = null;
        for(PathElement element : elemts ){
            if(element.getVertex().equals(arrivee)){
                 arriveeE = element;
                 break;
            }
        }
        boolean found = false;
        Vertex a = arriveeE.getVertex(),
               b = arriveeE.getPredecessor();
       
       while(!found){
           arriveeE = this.getElementPath(elemts, a);
           if(arriveeE.equals(this.stratingNode)) found = true;
           resu1.add(arriveeE.getLink());
           b = arriveeE.getPredecessor();
           if(b.equals(this.stratingNode)) found = true;
           else a = b;
           i++;
        }
       Collections.reverse(resu1);
      return resu1.toArray(new Relation[resu1.size()]);  
    }


    // the following function search a pathElement based on his vertex
    private PathElement getElementPath(ArrayList<PathElement> paths,Vertex vertex){
        PathElement element = null;
        Iterator<PathElement> it = paths.iterator();
        boolean found = false;
        while(!found && it.hasNext()){
            PathElement e = it.next();
            if(e.getVertex().equals(vertex)){
                found = true;
                element = e;
            }
        }
        return element;
    }
    
    private void SetElementPath(ArrayList<PathElement> paths,PathElement element){
        
        Iterator<PathElement> it = paths.iterator();
        boolean found = false;
        while(!found && it.hasNext()){
            PathElement e = it.next();
            if(e.getVertex().equals(element.getVertex())){
                found = true;
                e.setPredecessor(element.getPredecessor());
                e.setDistanceVertex(element.getDistanceVertex());
            }
        }
    }

    public Relation[][] getShortestPaths(Graph g,Vertex depart,ArrayList<PathElement> algosResults){
        Relation[] relation ;
        Relation[][] resu = new Relation[algosResults.size()-1][1];
        int i = 0;
        for(Vertex s :g.getAdjacencyMatrix().getVertices()){
        
            if(!s.equals(depart)){
            relation = this.getPath(algosResults,s, g);
            for(Relation r : relation )
            resu[i] = relation;
            i++;
            
            }
        }
        return resu; 
    }
    
    //-----------------------------------shortest paths algorithms--------------------------------------------
     
    //ps: distance in Djisktra = -1 stands for infinity 
    public ArrayList<PathElement> Djisktra(Graph g,Vertex depart){
        
        ArrayList<PathElement> resu = new ArrayList<PathElement>();
        //initialization 
       PathElement element = new PathElement(depart,depart);
       resu.add(element);
       TreeSet<Vertex> vertexs = new TreeSet<Vertex>();
       vertexs.addAll(g.getAdjacencyMatrix().getVertices());
       vertexs.remove(depart);
       ArrayList<Relation> relation =  g.getSuccessorsRelations(depart);
       double distance = -1;
        Relation r1 = null;
       for(Vertex s:  vertexs){
           Iterator<Relation> it = relation.iterator();
           while(it.hasNext() && (distance == -1)){
               r1 = it.next();
               if(r1.getRightVertex().equals(s)) distance = r1.getWeight();
               else distance = -1;
           }
         
           if(g.getSuccessors(depart).contains(s)){
               element = new PathElement(s,depart,distance);
               element.setLink(r1);
           }
           else element = new PathElement(s,distance); 
           resu.add(element);
           distance = -1;
       } 
       //-------------------------------------------------------------------------------
        // algorithm
        double distanceMin;

         CopyOnWriteArrayList<Vertex> visited = new CopyOnWriteArrayList<Vertex>();
         visited.add(depart);
         
        while(!visited.containsAll(vertexs)){
             Collections.sort(resu);
             // choose the vertex that has the min d(x)
            Vertex choix = null;// chosen vertex 
             boolean choiceDone = false;
            Iterator<PathElement> it = resu.iterator();
            PathElement el;
             
            while(it.hasNext() && !choiceDone ){
                 
                 el = it.next();
                 if((el.getDistanceVertex() != -1) && !visited.contains(el.getVertex())){
                     choix = el.getVertex();
                     choiceDone = true;
                 }
            }
            visited.add(choix);
            
            for(Relation r :g.getSuccessorsRelations(choix)){
                if(!visited.contains(r.getRightVertex())){ // if it's already visited there is nothing to do 
                    el = this.getElementPath(resu, r.getRightVertex());// get the correspondent   elementPath 
                    distanceMin = el.getDistanceVertex();
                    if(distanceMin == -1)// case of a infinite distance  
                        distanceMin = r.getWeight()+
                                    this.getElementPath(resu, choix).getDistanceVertex(); 
                        else distanceMin = Math.min(el.getDistanceVertex(),
                                r.getWeight()+ this.getElementPath(resu, choix).getDistanceVertex());
                    if(el.getDistanceVertex() != distanceMin ){
                        el.setDistanceVertex(distanceMin);
                        el.setPredecessor(choix);
                        el.setLink(r);
                    }       
                }
                
            }    
        }
         
        return resu;
    }
    
    public ArrayList<PathElement> BellemanFord(Graph g, Vertex depart){
        
        ArrayList<PathElement> result = new ArrayList<PathElement>();
        // initialization 
        PathElement element = new PathElement(depart,depart);
        result.add(element);
        TreeSet<Vertex> vertexs = new TreeSet<Vertex>();
        vertexs.addAll(g.getAdjacencyMatrix().getVertices());
        vertexs.remove(depart);
        for(Vertex s:vertexs ){
            element = new PathElement(s);
            result.add(element);
        }
        
        //algorithm 
        int nbrIterations  = g.getAdjacencyMatrix().getVertices().size() ;
        for(int i = 1; i<= nbrIterations ; i++){
 
            for(Relation arete : g.getAdjacencyMatrix().getRelations()){
                PathElement element1 = this.getElementPath(result, arete.getLeftVertex());
                PathElement element2 = this.getElementPath(result, arete.getRightVertex());
                
                if(!element1.isDistanceIsInfinite()){
                if(element2.isDistanceIsInfinite()){
                    element2.setDistanceVertex(element1.getDistanceVertex() + arete.getWeight());
                    element2.setPredecessor(element1.getVertex());
                    element2.setLink(arete);
                    element2.setDistanceIsInfinite(false);
                    this.SetElementPath(result, element2);
                    
                }
                
                else if((element1.getDistanceVertex()+arete.getWeight())<
                            element2.getDistanceVertex()){
                        element2.setDistanceVertex(element1.getDistanceVertex()+ arete.getWeight());
                        element2.setPredecessor(element1.getVertex());
                        element2.setDistanceIsInfinite(false);
                        element2.setLink(arete);
                        this.SetElementPath(result, element2);
                    }
                        
                }
                
            }
        }
        return result;
    }
    
    
    public ArrayList<ShortestPaths> FloydWarshall(Graph g){
        
        ArrayList<ShortestPaths>  result = new ArrayList<ShortestPaths>();
        ArrayList<PathElement> ligne = new ArrayList<PathElement>();
        PathElement element;
        double distance;
        
        //initialization 
       for(Vertex vertex1: g.getAdjacencyMatrix().getVertices()){
           
           for(Vertex vertex2 : g.getAdjacencyMatrix().getVertices()){
               
                if(!g.getSuccessors(vertex1).contains(vertex2)){
                    
                    if(vertex1.equals(vertex2))element = new PathElement(vertex1,vertex1);
                    else element = new PathElement(vertex2);
                }
                else {// it's a successor 
                    
                    distance = 0 ;
                    for(Relation arete : g.getSuccessorsRelations(vertex1)){
                        if(arete.getRightVertex().equals(vertex2)){
                            distance = arete.getWeight();
                            break;
                        }
                    }
                    element = new PathElement(vertex2,vertex1,distance);
                }
                 ligne.add(element);
            }
           ShortestPaths  pcc= new ShortestPaths(vertex1,ligne);
           
        result.add(pcc);
        ligne =  new ArrayList<PathElement>();
        }
       // algorithm
        for(int k = 0; k < result.size();k++){
            for(int i = 0; i < result.size() ; i++ ){
                for(int j = 0; j < result.size() ; j++){
                   PathElement e1 = result.get(i).roads.get(j);
                   PathElement e2 = result.get(i).roads.get(k);
                   PathElement e3 = result.get(k).roads.get(j);
                    if (!e2.isDistanceIsInfinite() && !e3.isDistanceIsInfinite()){
                        if(e1.isDistanceIsInfinite()){
                            distance = e2.getDistanceVertex() + e3.getDistanceVertex();
                            result.get(i).roads.get(j).setDistanceIsInfinite(false);
                            result.get(i).roads.get(j).setDistanceVertex(distance);
                            result.get(i).roads.get(j).setPredecessor(result.get(k).roads.get(j).getPredecessor());
                        }
                        else{
                            distance = Math.min(e2.getDistanceVertex() + e3.getDistanceVertex(), e1.getDistanceVertex());
                            if(distance < e1.getDistanceVertex()){
                                result.get(i).roads.get(j).setDistanceIsInfinite(false);
                                result.get(i).roads.get(j).setDistanceVertex(distance);
                                result.get(i).roads.get(j).setPredecessor(result.get(k).roads.get(j).getPredecessor());
                            }
                        }
                    }
                }
            }
        } 
        return result;
    }
    

    
    
    @Override
    public String toString(){
        return "starting Node is :"+this.getStratingNode()+ "\n elements :"+this.getRoads()+"\n";
    }

    public Vertex getStratingNode() {
        return stratingNode;
    }

    public void setStratingNode(Vertex stratingNode) {
        this.stratingNode = stratingNode;
    }

    public ArrayList<PathElement> getRoads() {
        return roads;
    }

    public void setRoads(ArrayList<PathElement> roads) {
        this.roads = roads;
    }
}
