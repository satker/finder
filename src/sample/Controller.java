package sample;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import sample.engine.Container;
import sample.engine.SearchFiles;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

interface OpenFile {
    String File(String str);
}

public class Controller extends Container implements Initializable {

    protected static volatile ObservableList<Container> res_files = FXCollections.observableArrayList();

    @FXML
    private TableView<Container> result_finder;

    @FXML
    private TableColumn<Container, Integer> id_find;

    @FXML
    private TableColumn<Container, String> name_file;

    @FXML
    private TextField inner_finder;

    @FXML
    private TextArea text_output_file;

    @FXML
    private TextField what_find;

    @FXML
    private TextField what_find_text;

    @FXML
    private Label change_name;

    private static String choose_res;

    @FXML
    private void startFind() {
        SearchFiles mem_find = new SearchFiles(inner_finder.getText(), what_find_text.getText(), what_find.getText());
        Thread main_thread = new Thread(mem_find);
        main_thread.start();
        id_find.setCellValueFactory(new PropertyValueFactory<>("id"));
        name_file.setCellValueFactory(new PropertyValueFactory<>("name"));
        result_finder.setItems(res_files);
        result_finder.setRowFactory(tv -> {
            TableRow<Container> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY
                        && event.getClickCount() == 1) {
                    Container clickedRow = row.getItem();
                    choose_res = clickedRow.getName();
                    /////// Вывод имени
                    change_name.setText(new File(choose_res).getName());
                }
            });
            return row;
        });
    }

    @FXML
    private void openFile() {
        // Вывод содержимого файла
        OpenFile open_file_text = (str) -> {
            String result;
            try (Stream<String> stream = Files.lines(Paths.get(str), Charset.forName("ISO-8859-1"))) {
                List<String> linkedList = stream.
                        collect(Collectors.toList()).
                        stream().
                        map(a -> "\n" + a).
                        reduce(String::concat).
                        stream().
                        collect(Collectors.toList());
                result = linkedList.get(0);
            } catch (IOException e) {
                result = "error_exception";
            }
            return result;
        };
        text_output_file.textProperty().addListener((ChangeListener<Object>) (observable, oldValue, newValue) -> {
            text_output_file.setScrollTop(Double.MAX_VALUE); //this will scroll to the bottom
            //use Double.MIN_VALUE to scroll to the top
        });
        text_output_file.setText(open_file_text.File(choose_res));
        //text_output_file.appendText("");
        /////////
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }
}
