package Model;

import Client.Client;
import Client.IClientStrategy;
import IO.MyDecompressorInputStream;
import Server.Server;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import java.net.UnknownHostException;

import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;

import java.io.*;
import java.net.InetAddress;
import java.util.Observable;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyModel extends Observable implements IModel {

    private ExecutorService threadPool = Executors.newFixedThreadPool(4);
    private Maze maze;
    private Server generatingServer;
    private Server solvingServer;
    private int characterPositionRow = 1;
    private int characterPositionColumn = 1;
    private Solution solution;

    public MyModel() {
        generatingServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        solvingServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
    }

    public void startServers() {
        generatingServer.start();
        solvingServer.start();
    }

    public void stopServers() {
        generatingServer.stop();
        solvingServer.stop();
        Platform.exit();
        setChanged();///???
        notifyObservers("stopServer");
    }

    @Override
    public void generateMaze(int width, int height) {
        solution = null;
        Platform.runLater(() -> {
            askServerToGenerate(width, height);
            characterPositionRow = maze.getStartPosition().getRowIndex();
            characterPositionColumn = maze.getStartPosition().getColumnIndex();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            setChanged();
            notifyObservers();
        });
    }

    private void askServerToGenerate(int width, int height) {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        int[] mazeDimensions = new int[]{width,height};
                        toServer.writeObject(mazeDimensions);
                        toServer.flush();
                        byte[] compressedMaze = (byte[])((byte[])fromServer.readObject());
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[12 + (width*height)];
                        is.read(decompressedMaze);
                        maze = new Maze(decompressedMaze);

                    } catch (OptionalDataException var10) {
                    } catch (Exception var11) {
                        var11.printStackTrace();
                    }

                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException var1) {
            var1.printStackTrace();
        }
    }


    @Override
    public Maze getMaze() {
        return maze;
    }

    //    @Override
//    public void moveCharacter(KeyCode movement) {
//        switch (movement) {
//            case NUMPAD8:
//                if(CheckMovementDirection(characterPositionRow-1 ,characterPositionColumn)){
//                    characterPositionRow--;
//                }
//                break;
//            case NUMPAD9:
//                if(CheckMovementDirection(characterPositionRow-1 ,characterPositionColumn+1)){
//                    characterPositionRow--;
//                    characterPositionColumn++;
//                }
//                break;
//            case NUMPAD6:
//                if(CheckMovementDirection(characterPositionRow ,characterPositionColumn+1)) {
//                    characterPositionColumn++;
//                }
//                break;
//            case NUMPAD3:
//                if(CheckMovementDirection(characterPositionRow+1 ,characterPositionColumn+1)) {
//                    characterPositionColumn++;
//                    characterPositionRow++;
//                }
//                break;
//            case NUMPAD2:
//                if(CheckMovementDirection(characterPositionRow+1 ,characterPositionColumn)) {
//                    characterPositionRow++;
//                }
//                break;
//            case NUMPAD1:
//                if(CheckMovementDirection(characterPositionRow+1 ,characterPositionColumn-1)) {
//                    characterPositionColumn--;
//                    characterPositionRow++;
//                }
//                break;
//            case NUMPAD4:
//                if(CheckMovementDirection(characterPositionRow ,characterPositionColumn-1)) {
//                    characterPositionColumn--;
//                }
//                break;
//            case NUMPAD7:
//                if(CheckMovementDirection(characterPositionRow-1 ,characterPositionColumn-1)) {
//                    characterPositionColumn--;
//                    characterPositionRow--;
//                }
//                break;
//        }
//        setChanged();
//        notifyObservers();
//    }

    @Override
    public void moveCharacter(KeyCode movement) {
        switch (movement) {
            case UP:
                if(CheckMovementDirection(characterPositionRow-1 ,characterPositionColumn)){
                    characterPositionRow--;
                }
                break;
            case DOWN:
                if(CheckMovementDirection(characterPositionRow+1 ,characterPositionColumn)) {
                    characterPositionRow++;
                }
                break;
            case RIGHT:
                if(CheckMovementDirection(characterPositionRow ,characterPositionColumn+1)) {
                    characterPositionColumn++;
                }
                break;
            case LEFT:
                if(CheckMovementDirection(characterPositionRow ,characterPositionColumn-1)) {
                    characterPositionColumn--;
                }
                break;
        }
        setChanged();
        notifyObservers();
    }

    private boolean CheckMovementDirection(int newRow, int newCol) {
        return (newRow >= 0 && newRow < maze.getNumofrows() && newCol >=0 && newCol <maze.getNumofcols() &&
                (maze.getValOfCell(newRow , newCol) == 0 || maze.getValOfCell(newRow , newCol) == 5 ||
                maze.getValOfCell(newRow , newCol) == 9) );
    }

    @Override
    public int getCharacterPositionRow() {
        return characterPositionRow;
    }

    @Override
    public int getCharacterPositionColumn() {
        return characterPositionColumn;
    }


    @Override
    public void fromTheStart() {
        solution = null;
        characterPositionRow = maze.getStartPosition().getRowIndex();
        characterPositionRow = maze.getStartPosition().getRowIndex();
        setChanged();
        notifyObservers();
    }

    @Override
    public void solveMaze() {
        Platform.runLater(() -> {
            getSolutionFromServer();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            setChanged();
            notifyObservers("solve");
            setChanged();
            notifyObservers();
        });
    }

    private void getSolutionFromServer() {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        toServer.writeObject(maze); //send current maze to server
                        toServer.flush();
                        solution = (Solution)fromServer.readObject(); //read generated maze (compressed with MyCompressor)
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Solution getSol() {
        return solution;
    }

    @Override
    public void exit() {
        stopServers();
    }

    @Override
    public boolean checkGameOver() {
        if( characterPositionRow == maze.getGoalPosition().getRowIndex() &&
                characterPositionColumn == maze.getGoalPosition().getColumnIndex() ){
           return true;
        }
        return false;
    }

    @Override
    public void saveCurrentMaze(String username) {
        ObjectOutputStream saveObj = null;
        try {
            saveObj = new ObjectOutputStream(new FileOutputStream("saveMazes/" + username));
            saveObj.writeObject(maze);
            saveObj.flush();
            saveObj.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public  void loadExistingMaze(File toOpen) throws FileNotFoundException {
        try {
            ObjectInputStream loadedMaze = new ObjectInputStream(new FileInputStream(toOpen));
            maze = (Maze)loadedMaze.readObject();
            characterPositionRow =maze.getStartPosition().getRowIndex();
            characterPositionColumn =maze.getStartPosition().getColumnIndex();
        }catch (IOException e){}
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        setChanged();
        notifyObservers("load");
    }

    private void popAlertToUser(String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(alertMessage);
        alert.show();
    }
}
