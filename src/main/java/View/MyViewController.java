package View;

import Model.MyModel;
import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Observable;
import java.util.Observer;

public class MyViewController implements Observer, IView {

    @FXML
    public MazeDisplayer mazeDisplayer;
    public AnchorPane anchorPane;
    public javafx.scene.layout.StackPane stackPane;
    public MenuBar menuBar;
    public javafx.scene.control.TextField txtfld_rowsNum;
    public javafx.scene.control.TextField txtfld_columnsNum;
    public javafx.scene.control.Label lbl_rowsNum;
    public javafx.scene.control.Label lbl_columnsNum;
    public javafx.scene.control.Button btn_generateMaze;
    public javafx.scene.control.Button btn_solveMaze;
    public javafx.scene.control.Button btn_startOver;
    public Button btn_showEmptyMaze;
    public Button btn_Choose_Characters;

    private boolean start = true;
    private MyViewModel myViewModel;
    private MediaPlayer mediaPlayer;
    private boolean muteMusic;
    public boolean isFinished;
    public static boolean stagecharacter;


    public void setViewModel(MyViewModel viewModel) {
        this.myViewModel = viewModel;
        bindProperties(viewModel);
        btn_generateMaze.setDisable(true);
        btn_showEmptyMaze.setDisable(true);
        btn_solveMaze.setDisable(true);
        btn_startOver.setDisable(true);
    }

    /**
     * bind properties that will be synchronized with controls in the GUI
     * @param viewModel
     */
    private void bindProperties(MyViewModel viewModel) {
        lbl_rowsNum.textProperty().bind(viewModel.characterPositionRow);
        lbl_columnsNum.textProperty().bind(viewModel.characterPositionColumn);
        menuBar.prefWidthProperty().bind(anchorPane.widthProperty());
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o == myViewModel) {
            if(myViewModel.getCharacterPositionRow() == mazeDisplayer.getGoalRow() &&
                    myViewModel.getCharacterPositionColumn() == mazeDisplayer.getGoalCol()  ){
                playSong("LionKing.mp3");
                endGif();
            }
            mazeDisplayer.setSolution(myViewModel.getSolution());
            displayMaze(myViewModel.getMaze());
            btn_generateMaze.setDisable(false);
            btn_startOver.setDisable(false);
            if (btn_showEmptyMaze.isDisable() == true) {
                btn_solveMaze.setDisable(false);
            }

        }
    }


    /**
     * show maze to user
     * @param maze
     */
    @Override
    public void displayMaze(Maze maze) {
        mazeDisplayer.setMaze(maze);
        int characterPositionRow = myViewModel.getCharacterPositionRow();
        int characterPositionColumn = myViewModel.getCharacterPositionColumn();
        mazeDisplayer.setCharacterPosition(characterPositionRow, characterPositionColumn);
        this.characterPositionRow.set(characterPositionRow + "");
        this.characterPositionColumn.set(characterPositionColumn + "");
    }

    /**
     * hide the solution from user
     */
    public void EmptyMaze() {
        mazeDisplayer.setMaze(myViewModel.getMaze());
        mazeDisplayer.setSolution(null);
        myViewModel.setSolInModel(null);
        int characterPositionRow = myViewModel.getCharacterPositionRow();
        int characterPositionColumn = myViewModel.getCharacterPositionColumn();
        mazeDisplayer.setCharacterPosition(characterPositionRow, characterPositionColumn);
        this.characterPositionRow.set(characterPositionRow + "");
        this.characterPositionColumn.set(characterPositionColumn + "");
        btn_showEmptyMaze.setDisable(true);
        btn_solveMaze.setDisable(false);
    }

    /**
     * create new Maze according the input of user, check the input is valid
     */
    public void generateMaze() {
        playSong("GOT_Open.mp3");
        boolean check = isNumeric(txtfld_rowsNum.getText(),txtfld_columnsNum.getText());
        if(check){
            int height = Integer.valueOf(txtfld_rowsNum.getText());
            int width = Integer.valueOf(txtfld_columnsNum.getText());
            if(height > 3 && width > 3){
                btn_generateMaze.setDisable(true);
                btn_solveMaze.setDisable(true);
                btn_startOver.setDisable(true);
                btn_showEmptyMaze.setDisable(true);
                myViewModel.generateMaze(width, height);
            }else{
                popAlertToUser("Please enter numeric values bigger than 3, so we could create a Maze for you :)");
            }
        }
        else{
            popAlertToUser("Please enter numeric values bigger than 3, so we could create a Maze for you :)");
        }
    }

    /**
     * check user's input to create the maze
     * @param h
     * @param w
     * @return
     */
    public static boolean isNumeric(String h, String w) {
        NumberFormat formatterH = NumberFormat.getInstance();
        ParsePosition posH = new ParsePosition(0);
        formatterH.parse(h, posH);

        NumberFormat formatterW = NumberFormat.getInstance();
        ParsePosition posW = new ParsePosition(0);
        formatterW.parse(w, posW);


        return h.length() == posH.getIndex() && w.length()== posW.getIndex();
    }

    /**
     * catch the button from user and solve the maze
     * @param actionEvent
     */
    public void solveMaze(ActionEvent actionEvent) {
        showAlert("Solving maze..");
        myViewModel.solveCurrentMaze();
        btn_showEmptyMaze.setDisable(false);
        btn_solveMaze.setDisable(true);
    }

    /**
     * pop up alert with string to user
     * @param alertMessage
     */
    private void showAlert(String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(alertMessage);
        alert.show();
    }

    /**
     * responsible for zooming by user while pressing ctrl+mouse wheel
     * @param scroll
     */
    public void setOnScroll(ScrollEvent scroll){
        if(scroll.isControlDown()){
            double zoomScale, deltaY;
            deltaY = scroll.getDeltaY();
            if (deltaY < 0) {
                zoomScale = 0.97;
                mazeDisplayer.setScaleX(mazeDisplayer.getScaleX() * zoomScale);
                mazeDisplayer.setScaleY(mazeDisplayer.getScaleY() * zoomScale);
                scroll.consume();
            } else if (deltaY > 0) {
                zoomScale = 1.03;
                mazeDisplayer.setScaleX(mazeDisplayer.getScaleX() * zoomScale);
                mazeDisplayer.setScaleY(mazeDisplayer.getScaleY() * zoomScale);
                scroll.consume();
            }
        }
    }

    /**
     * checking whick key was pressed and applying it to right function
     * @param keyEvent
     */
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
                stopSong();
                endGif();
                playSong("LionKing.mp3");

            }
        }

    }

    /**
     * play the music of the game
     * @param songName
     */
    public void playSong(String songName){
        if(mediaPlayer!=null)
            mediaPlayer.stop();
        String songPath = "resources/Music/" + songName;
        Media nowPlaying = new Media(new File(songPath).toURI().toString());
        mediaPlayer = new MediaPlayer(nowPlaying);
        mediaPlayer.play();
    }

    /**
     * stop song from playing by user choice
     */
    public void stopSong(){
        if(mediaPlayer!=null)
            mediaPlayer.stop();
        mediaPlayer = null;
    }


    /**
     * mute game sond and play ending music
     */
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

    /**
     * request focus on maze by clicking with mouse
     * @param mouseEvent
     */
    public void mouseClicked(MouseEvent mouseEvent){
        mazeDisplayer.requestFocus();
    }

    public void dragCharacter(MouseEvent mouseEvent){
        myViewModel.dragMouseChar(mouseEvent, mazeDisplayer.getCellWidth(),mazeDisplayer.getCellHeight());
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

    public String getGenerateMethod() { return Server.Configurations.getGenerateMethod(); }
    public String getNumberOfThreads() { return (Integer.toString(Server.Configurations.getpoolMaxThreads())); }
    public String getSolveMethod() { return Server.Configurations.getSolveMethod(); }

    public void clearMaze(ActionEvent actionEvent){
        mazeDisplayer.clearCanvas();
        //stopSong();
    }

    /**
     * opens a new window to save the game
     * @param actionEvent
     */
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

    /**
     *
     * @param actionEvent
     * @throws FileNotFoundException
     */
    public void loadMaze(ActionEvent actionEvent) throws FileNotFoundException {
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File("saveMazes/"));
        fc.setTitle( "Choose a maze");
        File toOpen = fc.showOpenDialog(null);
        mazeDisplayer.setSolution(null);
        if(toOpen !=null)
            myViewModel.loadMaze(toOpen);

    }

    /**
     * start the game from the beginning
     */
    public void startOver(){
        myViewModel.fromTheStart();
        btn_solveMaze.setDisable(false);
        btn_showEmptyMaze.setDisable(true);
    }


    /**
     * display properties of the game
     * @param actionEvent
     */
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

    /**
     * show instructions for the game
     * @param actionEvent
     */
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

    /**
     * exit game by pressing exit
     * @param actionEvent
     */
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

    /**
     * pop up message to user with string
     * @param alertMessage
     */
    private void popAlertToUser(String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(alertMessage);
        alert.show();
    }

    public void characters(ActionEvent actionEvent) {

        try {
            Stage stageChar = new Stage();
            stageChar.setTitle("Choose Character:");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("Characters.fxml").openStream());
            Scene scene = new Scene(root, 600, 350);
            stageChar.setScene(scene);
            stageChar.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stageChar.show();
            btn_generateMaze.setDisable(false);
            btn_solveMaze.setDisable(true);
            btn_startOver.setDisable(false);
            if(stagecharacter) {
                stagecharacter=false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * show winning window
     */
    public void endGif(){
        try {
            Stage stage = new Stage();
            //stage.setTitle("endGame");
            FXMLLoader fxmlLoader = new FXMLLoader();
//            Parent root = fxmlLoader.load(getClass().getResource("EndGameJone.fxml").openStream());
//            Scene scene = new Scene(root, 600, 400);
//            scene.getStylesheets().add(getClass().getResource("EndGameJon.css").toExternalForm());
//            stage.setScene(scene);

            if(CharactersController.JonTrue) {
                Parent root = fxmlLoader.load(getClass().getResource("EndGameJone.fxml").openStream());
                Scene scene = new Scene(root, 600, 400);
                scene.getStylesheets().add(getClass().getResource("EndGameJon.css").toExternalForm());
                stage.setScene(scene);
            }
            if(CharactersController.HalisiTrue) {
                Parent root = fxmlLoader.load(getClass().getResource("EndGameHalisi.fxml").openStream());
                Scene scene = new Scene(root, 600, 400);
                scene.getStylesheets().add(getClass().getResource("EndGameHalisi.css").toExternalForm());
                stage.setScene(scene);
            }
            if(CharactersController.CerseiTrue) {
                Parent root = fxmlLoader.load(getClass().getResource("EndGameCersei.fxml").openStream());
                Scene scene = new Scene(root, 600, 400);
                scene.getStylesheets().add(getClass().getResource("EndGameCersei.css").toExternalForm());
                stage.setScene(scene);
            }
            if(CharactersController.NiightKingTrue) {
                Parent root = fxmlLoader.load(getClass().getResource("EndGameNightKing.fxml").openStream());
                Scene scene = new Scene(root, 600, 400);
                scene.getStylesheets().add(getClass().getResource("EndGameNightKing.css").toExternalForm());
                stage.setScene(scene);
            }
            if(CharactersController.StannisTrue) {
                Parent root = fxmlLoader.load(getClass().getResource("EndGameStannis.fxml").openStream());
                Scene scene = new Scene(root, 600, 400);
                scene.getStylesheets().add(getClass().getResource("EndGameStannis.css").toExternalForm());
                stage.setScene(scene);
            }
            if(CharactersController.BranTrue) {
                Parent root = fxmlLoader.load(getClass().getResource("EndGameBran.fxml").openStream());
                Scene scene = new Scene(root, 600, 400);
                scene.getStylesheets().add(getClass().getResource("EndGameBran.css").toExternalForm());
                stage.setScene(scene);
            }
            if(CharactersController.LittleFingerTrue) {
                Parent root = fxmlLoader.load(getClass().getResource("EndGameLittleFinger.fxml").openStream());
                Scene scene = new Scene(root, 600, 400);
                scene.getStylesheets().add(getClass().getResource("EndGameLittleFinger.css").toExternalForm());
                stage.setScene(scene);
            }
            if(CharactersController.SansaTrue) {
                Parent root = fxmlLoader.load(getClass().getResource("EndGameSansa.fxml").openStream());
                Scene scene = new Scene(root, 600, 400);
                scene.getStylesheets().add(getClass().getResource("EndGameSansa.css").toExternalForm());
                stage.setScene(scene);
            }

            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
        } catch (Exception e) {
        }
    }


}