package Algorithmes;

import MainPackage.API.Composants.Autres.Graph;
import MainPackage.API.Composants.Autres.Vertex;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import javafx.scene.paint.Color;

public class Colouration {
     
    private Graph g;
    private ArrayList<CopyOnWriteArrayList<Vertex>> sameColourVerticesList = new ArrayList<CopyOnWriteArrayList<Vertex>>();
    private int ChromaticNumber = 0;
    private ArrayList<Color> colours = new ArrayList<Color>();
    
     public Colouration(Graph g){
        this.g = g;
        this.sameColourVerticesList = this.welshPowell();
        this.ChromaticNumber = this.sameColourVerticesList.size();
        this.RandomColors();
    }
    
    private ArrayList<CopyOnWriteArrayList<Vertex>>  welshPowell(){
        ArrayList<CopyOnWriteArrayList<Vertex>> result= new ArrayList<CopyOnWriteArrayList<Vertex>>();
        CopyOnWriteArrayList<Vertex> entree = this.g.getNodesSortedByDegree();
        CopyOnWriteArrayList<Vertex> coloured = new CopyOnWriteArrayList<Vertex>();
        CopyOnWriteArrayList<Vertex> sColour = new CopyOnWriteArrayList<Vertex>();
        boolean pasAdjacent;
        
        while(!entree.isEmpty()){
            sColour.add(entree.get(0));// list of vertices with a given colour C1
            coloured.add(entree.get(0));
            entree.remove(0);
            
            for(Vertex s :entree){
                
                Iterator it = sColour.iterator(); 
                pasAdjacent = true;
                while((it.hasNext())&& (pasAdjacent))//  check if s is adjacent to a vertex that has the same colour c1
                    pasAdjacent = !this.g.getListAdjacents((Vertex)it.next()).contains(s);
                // if it is not adjacent to any vertex that has the same color, it is added to the list of  vertices that has a c1 colour 
               
                if(pasAdjacent){
                    sColour.add(s);
                    coloured.add(s);// it's a already a coloured vertex 
                }
            }
            
            result.add(sColour);
            sColour = new CopyOnWriteArrayList<Vertex>();
            entree.removeAll(coloured);
        } 
        return result;
    }
    
    private void RandomColors(){
        double a = 0,
              b = 360;
        for(int i = 0;i < this.ChromaticNumber; i++){
            double hue = Math.random()*b;
            this.colours.add(Color.hsb(hue, 0.9, 0.9)); 
        }
    }
    
    public ArrayList<CopyOnWriteArrayList<Vertex>> getSameColourVerticesList() {
		return sameColourVerticesList;
	}

	public void setSameColourVerticesList(ArrayList<CopyOnWriteArrayList<Vertex>> sameColourVerticesList) {
		this.sameColourVerticesList = sameColourVerticesList;
	}

	public ArrayList<Color> getColours() {
		return colours;
	}

	public void setColours(ArrayList<Color> colours) {
		this.colours = colours;
	}

	public int getChromaticNumber() {
        return ChromaticNumber;
    }
}
