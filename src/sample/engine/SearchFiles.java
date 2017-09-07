package sample.engine;
/*
     Модуль поиска (мозг программы)
 */

import sample.Controller;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

public class SearchFiles extends Controller implements Runnable {
    private String directory; // Искомая директория
    private static String find_text; // Искомый текст
    private static String find_type; // Искомое расширение
    private static volatile int id = 1; // ID результата

    public static void setFind_text(String find_text) {
        SearchFiles.find_text = find_text;
    }

    public static void setFind_type(String find_type) {
        SearchFiles.find_type = find_type;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    @Override
    public void run() {
        try {
            LinkedList<String> res_set = current_Files(directory);
            while (res_set.size() > 0) {
                res_set.addAll(current_Files(res_set.getFirst()));
                res_set.remove(0);

            }

        } catch (IOException e) {
            System.out.println("Error");
        }
                    /*else  {
                        //Создаем пул потоков для рекурсии потоков
                        ExecutorService threadPool = Executors.newFixedThreadPool(1);
                        // Создаем и запускаем поток для поиска в директории
                        SearchFiles recursionThread = new SearchFiles();
                        recursionThread.setIs_resursion(true);
                        recursionThread.setDirectory(new_directory);
                        threadPool.execute(recursionThread);
                        // Закрываем потоки
                        threadPool.shutdown();
                        if (!(add_files(res_set.getFirst()) == null)) {
                            res_files.add(new Container(id, add_files(res_set.getFirst())));
                            id++;
                            res_set.remove(0);
                        } else {
                            res_set.remove(0);
                        }
                    }*/
    }


    private String add_files(String new_directory) throws IOException {
        String result_find = null;
        // если ничего не ввели выставляем по-умолчанию .log
        if (find_type.equals("")) {
            setFind_type("log");
        }
        // Если не ввели искомый текст убираем его из условия
        if (find_text.equals("")) {
            if (new RegexControl().RegexControl(find_type, new File(new_directory).getName())) {
                result_find = new_directory;
                // Если все введено проверяем условия
            }
        } else {
            if (new RegexControl().RegexControl(find_type, new File(new_directory).getName())) {
                if (new ControlSearchText().Control(new_directory, find_text)) {
                    result_find = new_directory;
                }
            }
        }
        return result_find;
    }

    private LinkedList<String> current_Files(String string) throws IOException {
        LinkedList<String> linkedList = new LinkedList<>();
        File file = new File(string);
        // Список файлов текущей директории
        String[] currentFiles = file.list();
        if (currentFiles != null) {
            for (String currentFile : currentFiles) {
                String add_mem = string + "\\" + currentFile;
                if (currentFile != null) {
                    // Если файл делаем сразу проверку
                    if (new File(add_mem).isFile()) {
                        if (!(add_files(add_mem) == null)) {
                            res_files.add(new Container(id, add_files(add_mem)));
                            id++;
                        }
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

