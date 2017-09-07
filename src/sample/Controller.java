package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import sample.engine.Container;
import sample.engine.OpenFile;
import sample.engine.SearchFiles;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private TextField what_find;

    @FXML
    private TextField what_find_text;

    @FXML
    private Label change_name;

    private static String choose_res;

    @FXML
    private void startFind(ActionEvent actionEvent){
        SearchFiles mem_find = new SearchFiles();
        mem_find.setDirectory(inner_finder.getText());
        mem_find.setFind_text(what_find_text.getText());
        mem_find.setFind_type(what_find.getText());
        ExecutorService pool = Executors.newCachedThreadPool();
        pool.execute(mem_find);
        pool.shutdown();
        id_find.setCellValueFactory(new PropertyValueFactory<>("id"));
        name_file.setCellValueFactory(new PropertyValueFactory<>("name"));
        result_finder.setItems(res_files);
        result_finder.setRowFactory(tv -> {
            TableRow<Container> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (! row.isEmpty() && event.getButton()==MouseButton.PRIMARY
                        && event.getClickCount() == 1) {
                            Container clickedRow = row.getItem();
                            choose_res = clickedRow.getName();
                            /////// Вывод имени
                            change_name.setText(new File(choose_res).getName());
                    /*try {
                        /////// Вывод содержания
                        String str_final = new String();
                        for (Object i : new OpenFile().File(clickedRow.getName())) {
                            str_final += "\n";
                            str_final += i.toString();
                        }
                        change_text.setText(str_final);
                        /////////
                    } catch (IOException e){
                        System.out.println("File not Found");
                    }*/
                }
            });
            return row;
        });
    }

    @FXML
    private void OpenFile(){
        try {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);

            alert.setTitle("Information");
            alert.setHeaderText(null);
            /////// Вывод содержания
            String str_final = new String();
            for (Object i : new OpenFile().File(choose_res)) {
                str_final += "\n" + i.toString();
            }
            alert.setContentText(str_final);
            alert.showAndWait();
            /////////
        } catch (IOException e){
            System.out.println("File not Found");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb){

    }
}
