package sample.engine;

/*
Открываем выбранный файл
 */
import java.io.*;
import java.util.LinkedList;

public class OpenFile {
    public LinkedList File(String str) throws IOException{
        File file = new File(str);
        BufferedReader fin = new BufferedReader(new FileReader(file));
        LinkedList<String> linkedList = new LinkedList<>();
        String line;
        while((line = fin.readLine()) != null) {
            linkedList.add(line);
        }
        return linkedList;
    }
}
