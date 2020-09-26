package ViewModel;


import Model.IModel;
import Model.MyModel;
import algorithms.mazeGenerators.Maze;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.input.KeyCode;
import algorithms.search.Solution;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Observable;
import java.util.Observer;

public class MyViewModel extends Observable implements Observer {

    private IModel myModel;
    private int characterPositionRowIndex;
    private int characterPositionColumnIndex;
    public StringProperty characterPositionRow = new SimpleStringProperty("1"); //For Binding
    public StringProperty characterPositionColumn = new SimpleStringProperty("1"); //For Binding

    public MyViewModel(IModel model){
        this.myModel = model;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o==myModel){
            characterPositionRowIndex = myModel.getCharacterPositionRow();
            characterPositionRow.set(characterPositionRowIndex + "");
            characterPositionColumnIndex = myModel.getCharacterPositionColumn();
            characterPositionColumn.set(characterPositionColumnIndex + "");
            setChanged();
            notifyObservers();
        }
    }

    public void dragMouseChar(MouseEvent mouseEvent, double cellHeight , double cellWidth){
        myModel.dragMouse(mouseEvent,cellHeight,cellWidth);
    }
    public void generateMaze(int width, int height){
        myModel.generateMaze(width, height);
    }

    public void moveCharacter(KeyCode movement){
        myModel.moveCharacter(movement);
    }

    public Maze getMaze() {
        return (Maze)myModel.getBoard();
    }

    public void exit(){
        myModel.exit();
    }

    public void goToStart(){
        myModel.fromTheStart();
    }

    public void solveCurrentMaze(){
        myModel.solveMaze();
    }

    public Solution getSolution(){
        return myModel.getSol();
    }

    public void saveMaze(String username){
        myModel.saveGame(username);
    }

    public void loadMaze(File toOpen) throws FileNotFoundException {
        myModel.loadGame(toOpen);
    }

    public void fromTheStart(){
        myModel.fromTheStart();
    }

    public int getCharacterPositionRow() {
        return characterPositionRowIndex;
    }

    public int getCharacterPositionColumn() {
        return characterPositionColumnIndex;
    }

    public boolean isGameOver() {
        return myModel.checkGameOver();
    }

    public void setSolInModel(Solution sol){
        myModel.setSolution(sol);
    }
}
