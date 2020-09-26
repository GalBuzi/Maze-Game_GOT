package Model;

import Client.Client;
import Client.IClientStrategy;
import IO.MyDecompressorInputStream;
import Server.Server;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import java.net.UnknownHostException;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;

import java.io.*;
import java.net.InetAddress;
import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.scene.input.MouseEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

public class MyModel extends Observable implements IModel {

    private Maze maze;
    private Server generatingServer;
    private Server solvingServer;
    private int characterPositionRow = 1;
    private int characterPositionColumn = 1;
    private Solution solution;
    private static final Logger LOG = LogManager.getLogger();
    public static String who;

    public MyModel() {
        generatingServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        solvingServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
        Configurator.setLevel("com.example.Foo", Level.DEBUG);
        Configurator.setRootLevel(Level.INFO);
    }


    public void startServers() {
        generatingServer.start();
        solvingServer.start();
    }

    public void stopServers() {
        generatingServer.stop();
        solvingServer.stop();
        Platform.exit();
        setChanged();
        notifyObservers("stopServer");
    }

    /**
     * generating new maze
     * @param width num of columns
     * @param height num of rows
     */
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
                        LOG.info(String.format("Client with IP %s Asked for new maze in Port %s" , InetAddress.getLocalHost(), 5400));
                        LOG.info(String.format("Client Asked For Maze With %s Rows and %s Columns" , height , width));
                        byte[] compressedMaze = (byte[])((byte[])fromServer.readObject());
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[12 + (width*height)];
                        is.read(decompressedMaze);
                        maze = new Maze(decompressedMaze);
                        LOG.info(String.format("Client received new Maze with Starting point at %s and Ending point at %s" , maze.getStartPosition().toString() , maze.getGoalPosition().toString()));
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
    public Object getBoard() {
        return maze;
    }

    /**
     * move charecter according to user key pressing
     * @param movement - the key which pressed by user
     */
        @Override
    public void moveCharacter(KeyCode movement) {
        switch (movement) {
            case NUMPAD8:
            case UP:
                if(CheckMovementDirection(characterPositionRow-1 ,characterPositionColumn)){
                    characterPositionRow--;
                }
                break;
            case NUMPAD9:
                if(CheckMovementDirection(characterPositionRow-1 ,characterPositionColumn+1)){
                    characterPositionRow--;
                    characterPositionColumn++;
                }
                break;
            case NUMPAD6:
            case RIGHT:
                if(CheckMovementDirection(characterPositionRow ,characterPositionColumn+1)) {
                    characterPositionColumn++;
                }
                break;
            case NUMPAD3:
                if(CheckMovementDirection(characterPositionRow+1 ,characterPositionColumn+1)) {
                    characterPositionColumn++;
                    characterPositionRow++;
                }
                break;
            case NUMPAD2:
            case DOWN:
                if(CheckMovementDirection(characterPositionRow+1 ,characterPositionColumn)) {
                    characterPositionRow++;
                }
                break;
            case NUMPAD1:
                if(CheckMovementDirection(characterPositionRow+1 ,characterPositionColumn-1)) {
                    characterPositionColumn--;
                    characterPositionRow++;
                }
                break;
            case NUMPAD4:
            case LEFT:
                if(CheckMovementDirection(characterPositionRow ,characterPositionColumn-1)) {
                    characterPositionColumn--;
                }
                break;
            case NUMPAD7:
                if(CheckMovementDirection(characterPositionRow-1 ,characterPositionColumn-1)) {
                    characterPositionColumn--;
                    characterPositionRow--;
                }
                break;
        }
        setChanged();
        notifyObservers();
    }

    /**
     * check if certain move is valid
     * @param newRow row to move to
     * @param newCol col to move to
     * @return
     */
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


    public void dragMouse(MouseEvent mouseEvent, double cellHeight , double cellWidth) {
        double upperBound = characterPositionRow * cellWidth;
        double leftBound = characterPositionColumn * cellHeight;
        double bottomBound = (characterPositionRow + 1) * cellWidth;
        double rightBound = (characterPositionColumn + 1) *  cellHeight;

        // Move Up
        if((mouseEvent.getY() < upperBound)
                && ((mouseEvent.getX() < rightBound) && (mouseEvent.getX() > leftBound)))
        {
            moveCharacter(KeyCode.UP);
        }
        // Move Down
        else if((mouseEvent.getY() > bottomBound)
                && ((mouseEvent.getX() < rightBound) && (mouseEvent.getX() > leftBound)))
        {
            moveCharacter(KeyCode.DOWN);
        }
        // Move Right
        else if((mouseEvent.getX() > rightBound)
                && ((mouseEvent.getY() < bottomBound) && (mouseEvent.getY() > upperBound)))
        {
            moveCharacter(KeyCode.RIGHT);
        }
        // Move Left
        else if((mouseEvent.getX() < leftBound)
                && ((mouseEvent.getY() < bottomBound) && (mouseEvent.getY() > upperBound)))
        {
            moveCharacter(KeyCode.LEFT);
        }
        // Move UP-Right
        else if((mouseEvent.getY() < upperBound) && (mouseEvent.getX() > rightBound))
        {
            moveCharacter(KeyCode.NUMPAD9);
        }
        // Move Down-Left
        else if((mouseEvent.getY() > bottomBound) && (mouseEvent.getX() < upperBound))
        {
            moveCharacter(KeyCode.NUMPAD1);
        }
        // Move Up-Left
        else if((mouseEvent.getY() < upperBound) && (mouseEvent.getX() < leftBound))
        {
            moveCharacter(KeyCode.NUMPAD7);
        }
        // Move Down-Right
        else if((mouseEvent.getY() > bottomBound) && (mouseEvent.getX() > rightBound))
        {
            moveCharacter(KeyCode.NUMPAD3);
        }

    }

    /**
     * set character to start point
     */
    @Override
    public void fromTheStart() {
        solution = null;
        characterPositionRow = maze.getStartPosition().getRowIndex();
        characterPositionColumn = maze.getStartPosition().getColumnIndex();
        setChanged();
        notifyObservers();
    }

    /**
     * solve maze as part of displaying it
     */
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

    /**
     * create solution to user and send it to him
     */
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
                        LOG.info(String.format("Client with IP %s Asked to solve maze in Port %s" , InetAddress.getLocalHost(), 5401));
                        toServer.flush();
                        solution = (Solution)fromServer.readObject(); //read generated maze (compressed with MyCompressor)
                        LOG.info(String.format("Solution For Client is %s steps long" ,solution.getSolutionPath().size()));
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

    /**
     * check if user is at ending point
     * @return
     */
    @Override
    public boolean checkGameOver() {
        if( characterPositionRow == maze.getGoalPosition().getRowIndex() &&
                characterPositionColumn == maze.getGoalPosition().getColumnIndex() ){
           return true;
        }
        return false;
    }

    /**
     * save game to folder saveMazes by user name
     * @param username
     */
    @Override
    public void saveGame(String username) {
        ObjectOutputStream saveObj = null;
        try {
            LOG.info(String.format("Saving Maze %s" , username));
            saveObj = new ObjectOutputStream(new FileOutputStream("saveMazes/" + username));
            maze.setStart(new Position(characterPositionRow,characterPositionColumn));
            saveObj.writeObject(maze);
            saveObj.flush();
            saveObj.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * load game from directory of saved mazes
     * @param toOpen
     * @throws FileNotFoundException
     */

    @Override
    public  void loadGame(File toOpen) throws FileNotFoundException {
        try {
            LOG.info(String.format("Loading Maze %s" , toOpen.toString()));
            ObjectInputStream loadedMaze = new ObjectInputStream(new FileInputStream(toOpen));
            maze = (Maze)loadedMaze.readObject();
            characterPositionRow = maze.getStartPosition().getRowIndex();
            characterPositionColumn = maze.getStartPosition().getColumnIndex();
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

    public void setSolution(Solution solution) {
        this.solution = solution;
    }
}
