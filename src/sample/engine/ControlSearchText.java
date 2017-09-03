package sample.engine;

/*
Проверяем в содержимом файла искомый текст
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ControlSearchText {

    volatile Boolean result_bool = new Boolean(false);

    public boolean Control(String file_name, String find_text) throws IOException{
        File file = new File(file_name);
        BufferedReader fin = new BufferedReader(new FileReader(file));
        String line;
        while((line = fin.readLine()) != null) {
            if(line.contains(find_text)){
                this.result_bool = true;
            }
        }
        return this.result_bool;
    }


}
