package sample.engine;
/*
     Модуль поиска (мозг программы)
 */

import org.jetbrains.annotations.NotNull;
import sample.Controller;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;
import java.util.regex.Pattern;

// Функциональный интерфейс для проверки файлов
interface Modules {
    boolean control(String what, String testString);
}

public class SearchFiles extends Controller implements Runnable {
    public SearchFiles(@NotNull String directory, String find_text, @NotNull String find_type) {
        this.directory = directory.trim().equals("") ? "C:\\" : directory;
        SearchFiles.find_text = find_text;
        SearchFiles.find_type = find_type.trim().equals("") ? "log" : find_type;// если ничего не ввели выставляем по-умолчанию .log
    }
    private String directory; // Искомая директория
    private static String find_text; // Искомый текст
    private static String find_type; // Искомое расширение
    private static int id = 1; // ID результата

    @Override
    public void run() {
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
    private LinkedList<String> currentDirectories(String string) {
        // Проверка расширения
        Modules control_file_type = (what, testString) -> Pattern.compile(".+\\." + what + "$").
                matcher(testString).
                matches();
        // Проверка наличия искомого текста в файле
        Modules control_text = (file_name, find_text) -> {
            boolean result = false;
            try {
                result = Files.lines(Paths.get(file_name), Charset.forName("ISO-8859-1")).
                        anyMatch(s -> s.contains(find_text));
            } catch (IOException e) {
                System.out.println("IOException");
            }
            return result;
        };
        LinkedList<String> linkedList = new LinkedList<>();
        // Список файлов текущей директории
        String[] currentFiles = new File(string).list();
        if (currentFiles != null) {
            Arrays.stream(currentFiles).
                    map(i -> string + "\\" + i)
                    .forEach(i -> {
                        File file = new File(i);
                        String add_file = file.canRead() && control_file_type.control(find_type, file.getName()) &&
                                (find_text.equals("") || control_text.control(i, find_text)) ?
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
                    });
        }
        return linkedList;
    }
}