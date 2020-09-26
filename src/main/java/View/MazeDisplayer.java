package View;

import algorithms.mazeGenerators.Maze;
import algorithms.search.AState;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.paint.Color;

import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

public class MazeDisplayer extends Canvas {

    private Maze maze;
    private int characterPositionRow;
    private int characterPositionColumn;
    private int goalRow;
    private int goalCol;
    private double cellHeight;
    private double cellWidth;
    private Solution solution;
    private StringProperty ImageFileNameWall = new SimpleStringProperty();
    public StringProperty ImageFileNameCharacter = new SimpleStringProperty();
    private StringProperty ImageFileNameGoal = new SimpleStringProperty();
    private StringProperty ImageFileNameSol = new SimpleStringProperty();


    public void setMaze(Maze maze) {
        this.maze = maze;
        characterPositionRow = maze.getStartPosition().getRowIndex();
        characterPositionColumn = maze.getStartPosition().getColumnIndex();
        goalRow = maze.getGoalPosition().getRowIndex();
        goalCol = maze.getGoalPosition().getColumnIndex();
        redraw();
    }

    public void setCharacterPosition(int row, int column) {
        characterPositionRow = row;
        characterPositionColumn = column;
        redraw();
    }

    public void clearCanvas(){
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());
    }

    public void setSolution(Solution sol){
        this.solution = sol;
        redraw();
    }

    public int getCharacterPositionRow() {
        return characterPositionRow;
    }

    public int getCharacterPositionColumn() {
        return characterPositionColumn;
    }

    public boolean checkPosition(int row, int col){
        if((col==getCharacterPositionColumn() && row==getCharacterPositionRow())||(col==getCharacterPositionColumn()+1&&row==getCharacterPositionRow()+1)||
                (col==getCharacterPositionColumn()+1&&row==getCharacterPositionRow()-1)||(col==getCharacterPositionColumn()-1&&row==getCharacterPositionRow()-1)||
                (col==getCharacterPositionColumn()-1&&row==getCharacterPositionRow()+1)){
            return true;
        }
        return false;
    }

    public double getCellHeight() {
        return cellHeight;
    }

    public double getCellWidth() {
        return cellWidth;
    }

    public void redraw() {
        if (maze != null) {
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            cellHeight = canvasHeight / maze.getNumofrows();
            cellWidth = canvasWidth / maze.getNumofcols();

            try {
                Image wallImage = new Image(new FileInputStream(ImageFileNameWall.get()));
                Image characterImage = new Image(new FileInputStream(ImageFileNameCharacter.get()));
                if(CharactersController.HalisiTrue)
                    characterImage = new Image((new FileInputStream(("resources/Images/Halisi.jpg"))));
                if(CharactersController.JonTrue)
                    characterImage = new Image((new FileInputStream(("resources/Images/jonSnow.jpg"))));
                if(CharactersController.CerseiTrue)
                    characterImage = new Image((new FileInputStream(("resources/Images/Cersei.jpg"))));
                if(CharactersController.NiightKingTrue)
                    characterImage = new Image((new FileInputStream(("resources/Images/NightKing.jpg"))));
                if(CharactersController.StannisTrue)
                    characterImage = new Image((new FileInputStream(("resources/Images/Stannis.png"))));
                if(CharactersController.BranTrue)
                    characterImage = new Image((new FileInputStream(("resources/Images/Bran.png"))));
                if(CharactersController.LittleFingerTrue)
                    characterImage = new Image((new FileInputStream(("resources/Images/LittleFinger.jpg"))));
                if(CharactersController.SansaTrue)
                    characterImage = new Image((new FileInputStream(("resources/Images/Sansa.png"))));

                Image goalImage = new Image(new FileInputStream(ImageFileNameGoal.get()));
                Image solveImage = new Image(new FileInputStream(ImageFileNameSol.get()));

                GraphicsContext gc = getGraphicsContext2D();
                gc.clearRect(0, 0, getWidth(), getHeight());

                //Draw Maze
                for (int i = 0; i < maze.getNumofrows(); i++) {
                    for (int j = 0; j < maze.getNumofcols(); j++) {
                        if (maze.getValOfCell(i,j) == 1) {
                            gc.drawImage(wallImage, j * cellWidth , i * cellHeight, cellWidth , cellHeight);
                        }
                    }
                }

                if (solution!=null) {
                    List<AState> solPath = solution.getSolutionPath();
                    boolean currentPosition= false;
                    for (int i = 1; i < solPath.size(); i++) {
                        int row = ((MazeState) solPath.get(i)).getPos().getRowIndex();
                        int col = ((MazeState) solPath.get(i)).getPos().getColumnIndex();
                        if(row == getCharacterPositionRow()+1 && col == getCharacterPositionColumn() ||
                                row == getCharacterPositionRow() && col == getCharacterPositionColumn() ||
                                row == getCharacterPositionRow()&& col == getCharacterPositionColumn()+1 ||
                                row == getCharacterPositionRow()-1&& col == getCharacterPositionColumn() ||
                                row == getCharacterPositionRow()&& col == getCharacterPositionColumn()-1){
                            currentPosition=true;
                        }
                        if(currentPosition) {
                            gc.drawImage(solveImage, col *cellWidth , row * cellHeight, cellWidth, cellHeight);
                        }
                    }
                }

                //Draw Character+Goal
                gc.drawImage(goalImage, goalCol *cellWidth , goalRow *cellHeight ,cellWidth , cellHeight);
                gc.drawImage(characterImage, characterPositionColumn *cellWidth , characterPositionRow *cellHeight , cellWidth ,cellHeight );

            } catch (FileNotFoundException e) {
                //e.printStackTrace();
            }

        }
    }

    @Override
    public double minHeight(double width)
    {
        return 135.0;
    }

    @Override
    public double maxHeight(double width)
    {
        return 1080.0;
    }

    @Override
    public double prefHeight(double height)
    {
        return minHeight(height);
    }

    @Override
    public double prefWidth(double width)
    {
        return minWidth(width);
    }

    @Override
    public double minWidth(double height)
    {
        return 240.0;
    }
    @Override
    public double maxWidth(double height)
    {
        return 1920.0;
    }

    @Override
    public boolean isResizable()
    {
        return true;
    }

    @Override
    public void resize(double width, double height)
    {
        super.setWidth(width);
        super.setHeight(height);
        redraw();
    }


    //region Properties

    public String getImageFileNameWall() {
        return ImageFileNameWall.get();
    }
    public void setImageFileNameWall(String imageFileNameWall) {
        this.ImageFileNameWall.set(imageFileNameWall);
    }


    public void setImageFileNameGoal(String ImageFileNameGoal) {this.ImageFileNameGoal.set(ImageFileNameGoal); }

    public String getImageFileNameGoal() {
        return ImageFileNameGoal.get();
    }

    public String getImageFileNameSol() {
        return ImageFileNameSol.get();
    }

    public String getImageFileNameCharacter() {
        return ImageFileNameCharacter.get();
    }

    public void setImageFileNameCharacter(String imageFileNameCharacter) {
        this.ImageFileNameCharacter.set(imageFileNameCharacter);
    }

    public void setImageFileNameSol(String imageFileNameSol) {
        this.ImageFileNameSol.set(imageFileNameSol);
    }

    public int getGoalRow() {
        return goalRow;
    }

    public void setGoalRow(int goalRow) {
        this.goalRow = goalRow;
    }

    public int getGoalCol() {
        return goalCol;
    }

    public void setGoalCol(int goalCol) {
        this.goalCol = goalCol;
    }


}
