package sample.engine;
/*
     Модуль поиска (мозг программы)
 */

import sample.Controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

interface Modules<T> {
    boolean Control(T what, T testString) throws IOException;
}

public class SearchFiles extends Controller implements Runnable {
    public SearchFiles(String directory, String find_text, String find_type) {
        this.directory = directory;
        SearchFiles.find_text = find_text;
        SearchFiles.find_type = find_type;
    }

    private volatile String directory; // Искомая директория
    private static String find_text; // Искомый текст
    private static String find_type; // Искомое расширение
    private static volatile int id = 1; // ID результата

    @Override
    public void run() {
        try {
            LinkedList<String> res_set;
            LinkedList<String> mem_for_remove = new LinkedList<>();
            res_set = current_Files(directory);
            while (res_set.size() > 0) {
                mem_for_remove.clear();
                // Параллельные стримы для перебора коллекции
                res_set.parallelStream().forEach(s -> {
                    try {
                        res_set.addAll(current_Files(s));
                        mem_for_remove.add(s);
                    } catch (IOException exp){
                        System.out.println();
                    }
                });
                res_set.removeAll(mem_for_remove);
            }
        } catch (IOException e) {
            System.out.println("error");
        }
    }


    private synchronized String add_files(String new_directory) throws IOException {
        String result_find = null;
        String text = new File(new_directory).getName();
        // Проверка файла и его пути на искомое расширение
        Modules<String> control_file_type = (what, testString) -> {
            Pattern p = Pattern.compile(".+\\." + what + "$");
            Matcher m = p.matcher(testString);
            return m.matches();
        };
        // Проверка наличия искомого текста в файле
        Modules<String> control_text = (file_name, find_text) -> {
            Stream<String> stream = Files.lines(Paths.get(file_name));
            //File file = new File(file_name);
            //BufferedReader fin = new BufferedReader(new FileReader(file));
            //String line;
            //while ((line = fin.readLine()) != null) {
            //    if (line.contains(find_text)) {
            //        result_bool = true;
            //    }
            //}
            return stream.anyMatch((s)->s.contains(find_text));
        };
        // если ничего не ввели выставляем по-умолчанию .log
        if (find_type.equals("")) {
            find_type = "log";
        }
        // Если не ввели искомый текст убираем его из условия
        if (find_text.equals("")) {
            if (control_file_type.Control(find_type, text)) {
                result_find = new_directory;
            }
        } else {
            if (control_file_type.Control(find_type, text)) {
                if(control_text.Control(new_directory, find_text)) {
                    result_find = new_directory;
                }
            }
        }
        return result_find;
    }

    private synchronized LinkedList<String> current_Files(String string) throws IOException {
        LinkedList<String> linkedList = new LinkedList<>();
        File file = new File(string);
        // Список файлов текущей директории
        String[] currentFiles = file.list();
        if (currentFiles != null) {
            for (String currentFile : currentFiles) {
                String add_mem = string + "\\" + currentFile;
                file = new File(add_mem);
                if (currentFile != null) {
                    // Если файл делаем сразу проверку
                    if (file.isFile() && !(add_files(add_mem) == null) && file.canRead()) {
                        res_files.add(new Container(id, add_files(add_mem)));
                        id++;
                    }//////// Если каталог записываем в колекцию и продолжаем поиск
                    else {
                        linkedList.add(add_mem);
                    }
                }
            }
        }
        return linkedList;
    }
}

