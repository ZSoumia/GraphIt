package MainPackage.API.Exceptions;

public class NoVertex2DException extends Exception{
    public NoVertex2DException(){
        super("You are about to instantiate a relation2D of an unknown Vertexs2D relation (null)");
    }
}
