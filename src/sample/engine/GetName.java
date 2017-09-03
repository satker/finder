package sample.engine;

public class GetName {
    
    public String getNameFile(String string){
        String[] strings = string.split("\\\\");
        return strings[strings.length-1];
    }
}
