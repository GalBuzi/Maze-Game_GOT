package View;

import algorithms.mazeGenerators.Maze;
import algorithms.search.AState;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

public class MazeDisplayer extends Canvas {

    private Maze maze;
    private int characterPositionRow;
    private int characterPositionColumn;
    private int goalRow;
    private int goalCol;
    private Solution solution;
    private StringProperty ImageFileNameWall = new SimpleStringProperty();
    private StringProperty ImageFileNameCharacter = new SimpleStringProperty();
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

    public void redraw() {
        if (maze != null) {
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            double cellHeight = canvasHeight / maze.getNumofrows();
            double cellWidth = canvasWidth / maze.getNumofcols();

            try {
                Image wallImage = new Image(new FileInputStream(ImageFileNameWall.get()));
                Image characterImage = new Image(new FileInputStream(ImageFileNameCharacter.get()));
                Image goalImage = new Image(new FileInputStream(ImageFileNameGoal.get()));
                Image solveImage = new Image(new FileInputStream(ImageFileNameSol.get()));

                GraphicsContext gc = getGraphicsContext2D();
                gc.clearRect(0, 0, getWidth(), getHeight());

                //Draw Maze
                for (int i = 0; i < maze.getNumofrows(); i++) {
                    for (int j = 0; j < maze.getNumofcols(); j++) {
                        if (maze.getValOfCell(i,j) == 1) {
                            gc.drawImage(wallImage, j * cellHeight, i * cellWidth, cellHeight, cellWidth);
                        }
                    }
                }


                if (solution!=null) {
                    List<AState> solPath = solution.getSolutionPath();
                    for (int i = 1; i < solPath.size(); i++) {
                        int row = ((MazeState) solPath.get(i)).getPos().getRowIndex();
                        int col = ((MazeState) solPath.get(i)).getPos().getColumnIndex();

                        gc.drawImage(solveImage, col * cellHeight, row * cellWidth, cellHeight, cellWidth);
                    }
                }

                //Draw Character+Goal
                gc.drawImage(goalImage, goalCol * cellHeight, goalRow * cellWidth, cellHeight, cellWidth);
                gc.drawImage(characterImage, characterPositionColumn * cellHeight, characterPositionRow * cellWidth, cellHeight, cellWidth);

            } catch (FileNotFoundException e) {
                //e.printStackTrace();
            }
        }
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
