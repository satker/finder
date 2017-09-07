package sample.engine;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexControl {
    boolean RegexControl(String what, String testString){
        Pattern p = Pattern.compile(".+\\."+what+"$");
        Matcher m = p.matcher(testString);
        return m.matches();
    }

}
