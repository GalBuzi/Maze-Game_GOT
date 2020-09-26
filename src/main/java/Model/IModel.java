package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.io.FileNotFoundException;

public interface IModel {
    void generateMaze(int width, int height);
    void moveCharacter(KeyCode movement);
    int getCharacterPositionRow();
    int getCharacterPositionColumn();
    void startServers();
    void fromTheStart();
    void solveMaze();
    Solution getSol();
    void exit();
    void saveGame(String username);
    void loadGame(File username) throws FileNotFoundException;
    boolean checkGameOver();
    void setSolution(Solution sol);
    Object getBoard();
    void dragMouse(MouseEvent e, double cellWight, double cellHight);
}
