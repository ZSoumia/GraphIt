package Algorithmes;

import MainPackage.API.Composants.Autres.Vertex;
import MainPackage.API.Composants.Liaison.Relation;

public class PathElement implements Comparable{
    
    private Vertex vertex, predecessor ;
    private double distanceVertex;
    private Relation link;
    private boolean DistanceIsInfinite;
    
    public PathElement(){
        this.DistanceIsInfinite = true;
    }
    
    public PathElement(Vertex s){
    this.vertex = s;
    this.DistanceIsInfinite = true;
    }
    
    public PathElement(Vertex s,Vertex p,double d){
        this.vertex = s;
        this.predecessor = p;
        this.distanceVertex = d;
        this.DistanceIsInfinite = false;
    }
    
    public PathElement(Vertex s,Vertex p){
        this.vertex = s;
        this.predecessor = p;
        if(s.equals(p)){
            this.distanceVertex = 0;
            this.DistanceIsInfinite = false;
        }
        else this.DistanceIsInfinite = true;
    }
    
    public PathElement(Vertex s,double d){
        this.vertex = s;
        this.distanceVertex = d;
        this.DistanceIsInfinite = false;
    }

    public Vertex getVertex() {
        return vertex;
    }

    public void setVertex(Vertex vertex) {
        this.vertex = vertex;
    }

    public Vertex getPredecessor() {
        return predecessor;
    }

    public void setPredecessor(Vertex predecessor) {
        this.predecessor = predecessor;
    }

    public double getDistanceVertex() {
        return distanceVertex;
    }

    public void setDistanceVertex(double distanceVertex) {
        this.distanceVertex = distanceVertex;
    }
    @Override 
    public String toString(){
        return "Element description : " + this.vertex.getLabel()+" =>  predecessor :"
                + this.getPredecessor()+"  => d("+this.vertex.getLabel()+") = "+this.distanceVertex
                +"\n";
    }

    @Override 
    public int compareTo(Object o) {
        return (int)(this.distanceVertex -((PathElement)o).getDistanceVertex());
    }

    public boolean isDistanceIsInfinite() {
        return DistanceIsInfinite;
    }

    public void setDistanceIsInfinite(boolean DistanceIsInfinite) {
        this.DistanceIsInfinite = DistanceIsInfinite;
    }

    public Relation getLink() {
        return link;
    }

    public void setLink(Relation link) {
        this.link = link;
    } 
}
