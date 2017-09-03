package sample.engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ControlSearchText {
    volatile Boolean result_bool = new Boolean(false);

    public Boolean getResult_bool() {
        return result_bool;
    }

    public void setResult_bool(Boolean result_bool) {
        this.result_bool = result_bool;
    }

    public boolean Control(String file_name, String find_text) throws IOException{
        File file = new File(file_name);
        BufferedReader fin = new BufferedReader(new FileReader(file));
        String line;
        while((line = fin.readLine()) != null) {
            if(line.contains(find_text)){
                setResult_bool(true);
            }
        }
        return getResult_bool();
    }


}
