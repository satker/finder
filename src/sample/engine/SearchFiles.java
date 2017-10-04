package sample.engine;
/*
     Модуль поиска (мозг программы)
 */

import sample.Controller;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

// Функциональный интерфейс для проверки файлов
interface Modules {
    boolean control(String what, String testString) throws IOException;
}

public class SearchFiles extends Controller implements Runnable {
    public SearchFiles(String directory, String find_text, String find_type) {
        this.directory = directory.trim();
        SearchFiles.find_text = find_text;
        SearchFiles.find_type = find_type.trim();
    }

    private String directory; // Искомая директория
    private static String find_text; // Искомый текст
    private static String find_type; // Искомое расширение
    private static int id = 1; // ID результата

    // Проверка расширения
    private Modules control_file_type = (what, testString) -> {
        Pattern p = Pattern.compile(".+\\." + what + "$");
        Matcher m = p.matcher(testString);
        return m.matches();
    };
    // Проверка наличия искомого текста в файле
    private Modules control_text = (file_name, find_text) -> {
        Stream<String> stream = Files.
                lines(Paths.get(file_name), Charset.forName("ISO-8859-1"));
        return stream.
                anyMatch(s -> s.contains(find_text));
    };

    @Override
    public void run() {
        directory = directory.equals("") ? "C:\\" : directory;
        find_type = find_type.equals("") ? "log" : find_type; // если ничего не ввели выставляем по-умолчанию .log
        LinkedList<String> res_set = currentDirectories(directory);
        LinkedList<String> mem_for_add = new LinkedList<>();
        while (res_set.size() > 0) {
            // Параллельные стримы для перебора коллекции
            res_set.parallelStream().filter(Objects::nonNull).forEach(s -> mem_for_add.addAll(currentDirectories(s)));
            res_set.clear();
            res_set.addAll(mem_for_add);
            mem_for_add.clear();
        }
    }
    // Возвращает список директорий в папке
    private synchronized LinkedList<String> currentDirectories(String string) {
        LinkedList<String> linkedList = new LinkedList<>();
        // Список файлов текущей директории
        String[] currentFiles = new File(string).list();
        if (currentFiles != null) {
            Arrays.stream(currentFiles).filter(Objects::nonNull).map(i -> string + "\\" + i)
                    .forEach(i -> {
                        try {
                            File file = new File(i);
                            String add_file = (file.canRead() && control_file_type.control(find_type, file.getName()) &&
                                    (find_text.equals("") || control_text.control(i, find_text)) ) ?
                                    i : null;
                            // Если файл делаем сразу проверку
                            if (file.isFile() && add_file != null && file.canRead()) {
                                res_files.add(new Container(id, add_file));
                                id++;
                            }
                            // Если каталог записываем в колекцию и продолжаем поиск
                            else {
                                linkedList.add(i);
                            }
                        } catch (IOException e) {
                            System.out.println("error");
                        }
                    });
        }
        return linkedList;
    }
}

