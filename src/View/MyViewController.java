package View;

import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Observable;
import java.util.Observer;

public class MyViewController implements Observer, IView {

    @FXML
    private MyViewModel myViewModel;
    public MazeDisplayer mazeDisplayer;
    public javafx.scene.control.TextField txtfld_rowsNum;
    public javafx.scene.control.TextField txtfld_columnsNum;
    public javafx.scene.control.Label lbl_rowsNum;
    public javafx.scene.control.Label lbl_columnsNum;
    public javafx.scene.control.Button btn_generateMaze;
    public javafx.scene.control.Button btn_solveMaze;
    private MediaPlayer mediaPlayer;
    private boolean muteMusic;

    public boolean isFinished;



    public void setViewModel(MyViewModel viewModel) {
        this.myViewModel = viewModel;
        bindProperties(viewModel);
    }

    private void bindProperties(MyViewModel viewModel) {
        lbl_rowsNum.textProperty().bind(viewModel.characterPositionRow);
        lbl_columnsNum.textProperty().bind(viewModel.characterPositionColumn);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o == myViewModel) {
            mazeDisplayer.setSolution(myViewModel.getSolution());
            displayMaze(myViewModel.getMaze());
            btn_generateMaze.setDisable(false);
            btn_solveMaze.setDisable(false);
//            btn_generateMaze.setDisable(false);
//            if (arg.equals("solve")) {
//                displaySolution(myViewModel.getSolution());
//                btn_solveMaze.setDisable(true);
//            }
        }
    }



    @Override
    public void displayMaze(Maze maze) {
        mazeDisplayer.setMaze(maze);
        int characterPositionRow = myViewModel.getCharacterPositionRow();
        int characterPositionColumn = myViewModel.getCharacterPositionColumn();
        mazeDisplayer.setCharacterPosition(characterPositionRow, characterPositionColumn);
        this.characterPositionRow.set(characterPositionRow + "");
        this.characterPositionColumn.set(characterPositionColumn + "");
    }

    public void displaySolution(Solution sol) {
        mazeDisplayer.setSolution(myViewModel.getSolution());
        int characterPositionRow = myViewModel.getMaze().getGoalPosition().getRowIndex();
        int characterPositionColumn = myViewModel.getMaze().getGoalPosition().getColumnIndex();
        mazeDisplayer.setCharacterPosition(characterPositionRow, characterPositionColumn);
        this.characterPositionRow.set(characterPositionRow + "");
        this.characterPositionColumn.set(characterPositionColumn + "");
    }

    public void generateMaze() {
        playSong("GOT_Open.mp3");
        int height = Integer.valueOf(txtfld_rowsNum.getText());
        int width = Integer.valueOf(txtfld_columnsNum.getText());
        btn_generateMaze.setDisable(true);
        btn_solveMaze.setDisable(true);
        myViewModel.generateMaze(width, height);
    }

    public void solveMaze(ActionEvent actionEvent) {
        showAlert("Solving maze..");
        myViewModel.solveCurrentMaze();
    }

    public void endGif(KeyEvent keyEvent){
        try {
            Stage stage = new Stage();
            //stage.setTitle("endGame");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("EndGame.fxml").openStream());
            Scene scene = new Scene(root, 360, 266);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
        } catch (Exception e) {
        }
    }

    private void showAlert(String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(alertMessage);
        alert.show();
    }


    public void KeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ESCAPE)) {
            keyEvent.consume();
            myViewModel.exit();
        }

        isFinished = myViewModel.isGameOver();
        if(!isFinished){
            keyEvent.consume();
            myViewModel.moveCharacter(keyEvent.getCode());
            isFinished = myViewModel.isGameOver();
            if(isFinished){
                endGif(keyEvent);
            }
        }

    }

    public void playSong(String songName){
        if(mediaPlayer!=null)
            mediaPlayer.stop();
        String songPath = "resources/Music/" + songName;
        Media nowPlaying = new Media(new File(songPath).toURI().toString());
        mediaPlayer = new MediaPlayer(nowPlaying);
        mediaPlayer.play();
    }

    public void stopSong(){
        if(mediaPlayer!=null)
            mediaPlayer.stop();
        mediaPlayer = null;
    }


    public void muteSong(){
        if(muteMusic) {
            playSong("GOT_Open.mp3");
            muteMusic= false;
        }
        else {
            stopSong();
            muteMusic =true;
        }
    }

    public void mouseClicked(MouseEvent mouseEvent){
        mazeDisplayer.requestFocus();
    }

    //region String Property for Binding
    public StringProperty characterPositionRow = new SimpleStringProperty();

    public StringProperty characterPositionColumn = new SimpleStringProperty();

    public String getCharacterPositionRow() {
        return characterPositionRow.get();
    }

    public StringProperty characterPositionRowProperty() {
        return characterPositionRow;
    }

    public String getCharacterPositionColumn() {
        return characterPositionColumn.get();
    }

    public StringProperty characterPositionColumnProperty() {
        return characterPositionColumn;
    }

    public void setResizeEvent(Scene scene) {
        long width = 0;
        long height = 0;
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                System.out.println("Width: " + newSceneWidth);
                mazeDisplayer.setWidth( mazeDisplayer.getWidth() + ( newSceneWidth.doubleValue() - oldSceneWidth.doubleValue() ));
                mazeDisplayer.redraw();

            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                System.out.println("Height: " + newSceneHeight);
                mazeDisplayer.setHeight( mazeDisplayer.getHeight() + ( newSceneHeight.doubleValue() - oldSceneHeight.doubleValue() ));
                mazeDisplayer.redraw();
            }
        });
    }

    public String getGenerateMethod() { return Server.Configurations.getGenerateMethod(); }
    public String getNumberOfThreads() { return (Integer.toString(Server.Configurations.getpoolMaxThreads())); }
    public String getSolveMethod() { return Server.Configurations.getSolveMethod(); }

    public void clearMaze(ActionEvent actionEvent){
        mazeDisplayer.clearCanvas();
        //stopSong();
    }

    public void saveMaze(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("save Projects");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("SaveMaze.fxml").openStream());
            SaveController SaveC = fxmlLoader.getController();
            SaveC.setViewModel(myViewModel);
            Scene scene = new Scene(root, 300, 100);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadMaze(ActionEvent actionEvent) throws FileNotFoundException {
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File("saveMazes/"));
        fc.setTitle( "Choose a maze");
        File toOpen = fc.showOpenDialog(null);
        mazeDisplayer.setSolution(null);;
        if(toOpen !=null)
            myViewModel.loadMaze(toOpen);
        //playSong("spongeRemix.mp3");
        //btn_loadMaze.setDisable(true);
        //btn_solveMaze.setDisable(false);
    }


    public void getProperties(ActionEvent actionEvent) {

        try {
            Stage stage = new Stage();
            stage.setTitle("Properties:");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("Properties.fxml").openStream());
            Scene scene = new Scene(root, 400, 200);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void howToPlay(ActionEvent actionEvent){
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader();
            stage.setTitle("How to Play?");
            Parent root = fxmlLoader.load(getClass().getResource("howToPlay.fxml").openStream());
            Scene scene = new Scene(root, 500, 330);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
        } catch (Exception e) {
        }
    }

    public void exitWindow(ActionEvent actionEvent){
        myViewModel.exit();
    }


    public void About(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("AboutController");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("About.fxml").openStream());
            Scene scene = new Scene(root, 650, 400);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
        } catch (Exception e) {

        }
    }
}
