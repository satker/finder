package sample.engine;
/*
     Модуль поиска (мозг программы)
 */
import sample.Controller;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchFiles extends Controller implements Runnable{
    private String directory; // Искомая директория
    private static String find_text; // Искомый текст
    private static String find_type; // Искомое расширение
    private static volatile int id = 1; // ID результата

    public void setFind_type(String find_type) {
        this.find_type = find_type;
    }

    public void setFind_text(String find_text) {
        this.find_text = find_text;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    //Добавляем новую партию в массив
    @Override
    public void run() {
        File file = new File(directory);
        // Список файлов текущей директории
        String [] currentFiles = file.list();
        //если нет файлов ничего не делаем
        if (currentFiles == null) {
        }
        //Если есть, идем дальше и добавляем новую партию в массив
        else {for (String new_directory:
                currentFiles) {
            // Выводим полный путь файла
            new_directory = getDirectory()+"\\"+new_directory;
            // Ищем .log файл
            try{
                // если ничего не ввели выставляем по-умолчанию .log
                if(find_type.equals("")){
                    setFind_type(".log");
                }
                // Если не ввели искомый текст убираем его из условия
                if(find_text.equals("")){
                    if(new_directory.contains(find_type)){
                            res_files.add(new Container(id, new_directory));
                            id++;
                // Если все введено проверяем условия
                }} else {
                    if(new_directory.contains(find_type)){
                        if(new ControlSearchText().Control(new_directory, find_text)) {
                            res_files.add(new Container(id, new_directory));
                            id++;
                        }
                }
            }}
            catch (NullPointerException e1){
                System.out.println("Whats wrong");
            }
            catch (IOException e){
                System.out.println("Error");
            }
            // Если это дирректория ищем далее, ищем в этой директории
            if (new Control().search(new_directory)){
                //Создаем пул потоков для рекурсии потоков
                ExecutorService threadPool = Executors.newFixedThreadPool(1);
                // Создаем и запускаем поток для поиска в директории
                SearchFiles recursionThread = new SearchFiles();
                recursionThread.setDirectory(new_directory);
                threadPool.execute(recursionThread);
                // Закрываем потоки
                threadPool.shutdown();
            }
        }}
    }
}
// Проверка директория ли это
class Control extends SearchFiles {
    Boolean search(String s){
        File name = new File(s);
        return name.isDirectory();
    }
}

