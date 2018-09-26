package MainPackage.API.Exceptions;

public class RelationMismatchException extends Exception{
    public RelationMismatchException(){
        super("you are mixing two relations of two different types inside the same structure");
    }
}
