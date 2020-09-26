package View;

import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import com.sun.org.apache.xpath.internal.SourceTree;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.*;

public class SaveController {
    @FXML
    private MyViewModel viewModel;
    public javafx.scene.control.Button btn_save;
    public javafx.scene.control.TextField txF_Save;

    public SaveController(){};
    public SaveController(MyViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
    }
    private void showAlert(String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(alertMessage);
        alert.show();
    }

    public void SaveMaze(ActionEvent actionEvent) {

        viewModel.saveMaze(txF_Save.getText());
        showAlert("The maze saved successfully:\n under the name: '"+ txF_Save.getText()+"'");
        Stage stage = (Stage) btn_save.getScene().getWindow();
        stage.close();
    }
}
