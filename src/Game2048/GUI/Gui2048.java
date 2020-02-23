package Game2048.GUI; /**
 * Gui2048.java  PSA8 Release
 */
/** PSA8 Release */

/*
 * Name: Huan Nguyen 
 * Login: cs8bwagg 
 * Date:  February 1, 2016
 * File:  GUI2048.java  
 * Sources of Help: Once upon a time, a boy was borned with an Incredible Mind. 
 Long story short, my Incredible Intelligence. jk~ 
 * Purpose: Create a GUI for 2048 game. (Front-end engine)
 *            
 *
 */

/*
 *	Class: Gui2048
 *	Description: Contain a functional GUI for the game 2048. 
 *			Can update changes and take in user key presses
 as input. 
 *
 *	Bugs: Nada.
 *
 *	Author: Huan Nguyen
 *
 *
 *
 */

import Game2048.AI.ExpectiMax;
import Game2048.Game.Board2048;
import Game2048.Game.Board2048.Directions;
import javafx.application.*;
import javafx.concurrent.Task;
import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.event.*;
import javafx.scene.input.*;
import javafx.scene.text.*;
import javafx.geometry.*;

public class Gui2048 extends Application {
    private String outputBoard; // The filename for where to save the Board
    private Board2048 board; // The 2048 Game Board

    private static final int TILE_WIDTH = 106;

    private static final int TEXT_SIZE_LOW = 55; // Low value tiles (2,4,8,etc)
    private static final int TEXT_SIZE_MID = 45; // Mid value tiles (128, 256, 512)
    private static final int TEXT_SIZE_HIGH = 40; // High value tiles (1024, 2048, 4096, 8192)
    private static final int TEXT_SIZE_EXTREME = 30; // Extreme value tiles (16k, 32k, 64k)

    // Fill colors for each of the Tile values
    private static final Color COLOR_EMPTY = Color.rgb(238, 228, 218, 0.35);
    private static final Color COLOR_2 = Color.rgb(238, 228, 218);
    private static final Color COLOR_4 = Color.rgb(237, 224, 200);
    private static final Color COLOR_8 = Color.rgb(242, 177, 121);
    private static final Color COLOR_16 = Color.rgb(245, 149, 99);
    private static final Color COLOR_32 = Color.rgb(246, 124, 95);
    private static final Color COLOR_64 = Color.rgb(246, 94, 59);
    private static final Color COLOR_128 = Color.rgb(237, 207, 114);
    private static final Color COLOR_256 = Color.rgb(237, 204, 97);
    private static final Color COLOR_512 = Color.rgb(237, 200, 80);
    private static final Color COLOR_1024 = Color.rgb(237, 197, 63);
    private static final Color COLOR_2048 = Color.rgb(237, 194, 46);
    private static final Color COLOR_OTHER = Color.BLACK;

    private static final Color[] COLORS = new Color[] {
            COLOR_EMPTY, COLOR_2, COLOR_4, COLOR_8, COLOR_16, COLOR_32, COLOR_64, COLOR_128, COLOR_256, COLOR_512,
            COLOR_1024, COLOR_2048, COLOR_OTHER

    };
    private static final Color COLOR_GAME_OVER = Color.rgb(238, 228, 218, 0.73);

    // For tiles >= 8
    private static final Color COLOR_VALUE_LIGHT = Color.rgb(249, 246, 242);

    // For tiles < 8
    private static final Color COLOR_VALUE_DARK = Color.rgb(119, 110, 101);


    /** Add your own Instance Variables here */
    //GridPane is already created
    private StackPane mainPane = new StackPane();
    private GridPane pane;
    final Text gameName = new Text("2048"); //Name of the game won't change
    Text scoreText = new Text("Score: 0");
    Rectangle[][] tileGrid;
    Text[][] tileText;
    int scoreValue = 0;
    GridPane gameOverPane;
    Text gameOverText;
    Scene mainScene;
    final int defaultSize = 4;
    Text mySignature = new Text("By Huan Nguyen");
    boolean isAIEnabled = false;
    AiTask aiTask;
    Text depthText;
    int depth = 7;

    /**
     * The "main()" method for JavaFX. Help setting up the GUI.
     * @param primaryStage the main stage of the GUI
     */
    @Override
    public void start(Stage primaryStage) {
        // Process Arguments and Initialize the Game Board
        processArgs(getParameters().getRaw().toArray(new String[0]));

        // Create the pane that will hold all of the visual objects
        pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        pane.setPadding(new Insets(11.5, 12.5, 13.5, 14.5));
        pane.setStyle("-fx-background-color: rgb(187, 173, 160)");
        // Set the spacing between the Tiles
        pane.setHgap(15);
        pane.setVgap(15);

                pane.setOnKeyPressed(normalKeyHandler);
        /** Add your Code for the GUI Here */
        initGrid();//initialize the GridPane
        mainPane.getChildren().add(pane);
        mainScene = new Scene(mainPane);
        primaryStage.setTitle("GUI 2048");
        primaryStage.setScene(mainScene);
        primaryStage.show();
        pane.requestFocus();
        pane.setDisable(false);
    }

    /** Add your own Instance Methods Here */
    /**
     * Initialize the grid (game board) in the GUI.
     */
    private void initGrid() {
        gameName.setFont(Font.font("Comic Sans MS", FontPosture.ITALIC, 40));
        scoreText.setFont(Font.font("Comic Sans MS", FontPosture.ITALIC, 25));
        pane.add(gameName, 0, 0, board.getBoardSize() / 2, 1);//add game name text
        pane.add(scoreText, (board.getBoardSize() + 1) / 2, 0, board.getBoardSize() / 2, 1);//add score text
        pane.setHalignment(gameName, HPos.CENTER);
        pane.setHalignment(scoreText, HPos.CENTER);
        //Initial grid tile and text
        tileGrid = new Rectangle[board.getBoardSize()][board.getBoardSize()];
        tileText = new Text[board.getBoardSize()][board.getBoardSize()];
        scoreValue = (int) board.getScore();//get the score
        // Getting tempArray for value of current grid
        int[][] numberGrid = board.getBoard();
        // Set score text equal to current score
        scoreText.setText("Score: " + scoreValue);

        int tileSize = TILE_WIDTH * defaultSize / board.getBoardSize();
        // Initialize tileGrid for the color of the grid
        for (int row = 0; row < board.getBoardSize(); row++) {
            for (int column = 0; column < board.getBoardSize(); column++) {//determine color of grid based value
                tileGrid[row][column] =
                        new Rectangle(tileSize, tileSize);
                pane.add(tileGrid[row][column], column, row + 1);
            }
        }
        // Initialize tileText through loop
        for (int row = 0; row < board.getBoardSize(); row++) {
            for (int column = 0; column < board.getBoardSize(); column++) {
                tileText[row][column] = new Text("");
                //add text into grid pane
                pane.setHalignment(tileText[row][column], HPos.CENTER);
                pane.add(tileText[row][column], column, row + 1);
            }
        }
        // Adding my signature
        mySignature.setFont(Font.font("Comic Sans MS", FontPosture.ITALIC, 15));
        pane.setHalignment(mySignature, HPos.RIGHT);
        pane.add(mySignature, board.getBoardSize() - 2, board.getBoardSize() + 1,
                2, 1);

        // Display AI Depth of Search
        depthText = new Text("Depth of Search: " + depth);
        depthText.setFont(Font.font("Comic Sans MS", FontPosture.ITALIC, 15));
        pane.add(depthText, 0, board.getBoardSize() + 1,
                2, 1);
        update();
    }

    /**
     * Update the GUI once the game is changed. This method will needed to be
     * called manually.
     */
    private void update() {//update the grid GUI & Score
        scoreValue = (int) board.getScore();//get the score

        // Getting tempArray for value of current grid
        int[][] numberGrid = board.getBoard();

        // Set score text equal to current score
        scoreText.setText("Score: " + scoreValue);
        // Updating tileGrid for the color of the grid
        for (int row = 0; row < board.getBoardSize(); row++) {
            for (int column = 0; column < board.getBoardSize(); column++) {//determine color of grid based value
                if (numberGrid[row][column] >= 13)
                    tileGrid[row][column].setFill(COLOR_OTHER);
                else
                    tileGrid[row][column].setFill(COLORS[numberGrid[row][column]]);

            }
        }

        // Updating tileText through loop
        for (int row = 0; row < board.getBoardSize(); row++) {
            for (int column = 0; column < board.getBoardSize(); column++) {//determine what text to put in the tile
                if (numberGrid[row][column] == 0)//blank tile
                    tileText[row][column].setText("");

                else {//put number into tile
                    tileText[row][column].setText(
                            Integer.toString((int) Math.pow(2, numberGrid[row][column])));
                    if (numberGrid[row][column] < 7) {
                        tileText[row][column].setFont(
                                Font.font("Times New Roman",
                                        FontWeight.BOLD, TEXT_SIZE_LOW));
                    }

                    if (numberGrid[row][column] >= 7 &&
                            numberGrid[row][column] < 10) {
                        tileText[row][column].setFont(
                                Font.font("Times New Roman",
                                        FontWeight.BOLD, TEXT_SIZE_MID));
                    }

                    if (numberGrid[row][column] >= 10 &&
                            numberGrid[row][column] < 14) {
                        tileText[row][column].setFont(
                                Font.font("Times New Roman",
                                        FontWeight.BOLD, TEXT_SIZE_HIGH));
                    }

                    if (numberGrid[row][column] >= 14) {
                        tileText[row][column].setFont(
                                Font.font("Times New Roman",
                                        FontWeight.BOLD, TEXT_SIZE_EXTREME));
                    }

                    if (numberGrid[row][column] < 3)
                        tileText[row][column].setFill(COLOR_VALUE_DARK);
                    else
                        tileText[row][column].setFill(COLOR_VALUE_LIGHT);
                }

            }
        }
        if (!board.checkIfCanGo()) {
            disableAI();
            if (gameOverPane == null) {
                gameOverPane = new GridPane();
                gameOverPane.setStyle(
                        "-fx-background-color: rgb(238, 228, 218,0.73)");
                gameOverText = new Text("Game Over!");
                gameOverText.setFont(Font.font("Comic Sans MS", 40));
                gameOverText.setFill(Color.BLACK);
                gameOverPane.add(gameOverText, 0, 0);
                gameOverPane.setAlignment(Pos.CENTER);
                gameOverPane.setHalignment(gameOverText, HPos.CENTER);
                mainPane.getChildren().add(gameOverPane);
            }
        }
        if (isAIEnabled)
            runAI();
    }

    /**
     * Restart the game and reset the GUI (if the Game Over pan showed up).
     */
    private void restart() {
        board = new Board2048();
        pane.requestFocus();
        pane.setDisable(false);
        update();
        if (gameOverPane != null) {
            mainPane.getChildren().remove(gameOverPane);
            gameOverPane = null;
        }
    }

    /**
     * Cycle through the depth of search that the AI will perform. Allow
     * user to specify how cautious they want the AI to go.
     */
    private void switchDepthOfSearch() {
        if (depth == 7)
            depth = 9;
        else if (depth == 9)
            depth = 5;
        else if (depth == 5)
            depth = 7;
        depthText.setText("Depth of Search: " + depth);
    }

    /**
     * Enable AI and update KeyPress Handler to limit user's interaction with
     * the game while the AI is running.
     */
    private void enableAI() {
        isAIEnabled = true;
        runAI();
        pane.setOnKeyPressed(null);
        pane.setOnKeyPressed(limitedKeyHandler);
    }

    /**
     * Disable AI and and remove user's limitation while AI is running, allow
     * user to take full control of the game.
     */
    private void disableAI() {
        stopAI();
        isAIEnabled = false;
        pane.setOnKeyPressed(null);
        pane.setOnKeyPressed(normalKeyHandler);
    }

    /**
     * Run AI in a separate task so the GUI remains responsive.
     */
    private void runAI() {
        // Only run AI if game is not over
        if (board.checkIfCanGo()) {
            aiTask = new AiTask(board, depth);
            aiTask.setOnSucceeded((succeededEvent) -> {
                if (aiTask.getValue() != null) {
                    board.move(aiTask.getValue().getRotateValue());
                    update();
                }
            });
            Thread t = new Thread(aiTask);
            t.setDaemon(true);
            t.start();
        }
    }

    /**
     * Stop the AI by canceling the existing AI task.
     */
    private void stopAI() {
        aiTask.cancel();
    }

    /** Event Handlers **/

    /**
     * Normal KeyPress Handler. This handler allow user full control over the game.
     */
    EventHandler<KeyEvent> normalKeyHandler = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent keyEvent) {
            switch (keyEvent.getCode()) {
                case UP:
                    board.move(Directions.UP.getRotateValue());//calling move method
                    update();
                    break;

                case DOWN:
                    board.move(Directions.DOWN.getRotateValue());//calling move method
                    update();
                    break;

                case LEFT:
                    board.move(Directions.LEFT.getRotateValue());//calling move method
                    update();
                    break;

                case RIGHT:
                    board.move(Directions.RIGHT.getRotateValue());//calling move method
                    update();
                    break;

                case R:
                    restart();
                    break;

                case C:
                    switchDepthOfSearch();
                    break;

                case ENTER:
                    // If Game Over then reset the game and enable AI
                    if (!board.checkIfCanGo()) {
                        restart();
                    }
                    enableAI();
                    break;

                default:
                    break;
            }
        }
    };

    /**
     * Limited KeyPress Handler. Only allow user to change the depth, or stop
     * the AI while the AI is running.
     */
    EventHandler<KeyEvent> limitedKeyHandler = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent keyEvent) {
            switch (keyEvent.getCode()) {
                case C:
                    switchDepthOfSearch();
                    break;

                case ENTER:
                    disableAI();
                    break;

                default:
                    break;
            }
        }
    };

    /** Task Class for AI **/
    public class AiTask extends Task<Directions> {

        Board2048 m_gameBoard;
        int m_depth = 7;

        /**
         * Public constructor to create an AI task.
         * @param originalBoard original game board that AI is supposed to solve
         * @param depth depth of search that AI will perform
         */
        public AiTask(Board2048 originalBoard, int depth) {
            this.m_gameBoard = originalBoard;
            m_depth = depth;
        }

        @Override
        protected Directions call() throws Exception {
            ExpectiMax expectiMax = new ExpectiMax(m_gameBoard, m_gameBoard.getScore(), m_depth);
            Board2048.Directions bestDirection = expectiMax.computeDecision();
            return bestDirection;
        }
    }
    /** DO NOT EDIT BELOW */

    // The method used to process the command line arguments
    private void processArgs(String[] args) {
        String inputBoard = null;//The filename for where to load the Board
        int boardSize = 0;   // The Size of the Board

        // Arguments must come in pairs
        if ((args.length % 2) != 0) {
            printUsage();
            System.exit(-1);
        }

        // Process all the arguments
        for (int i = 0; i < args.length; i += 2) {
            if (args[i].equals("-i")) {   // We are processing the argument that specifies
                // the input file to be used to set the board
                inputBoard = args[i + 1];
            } else if (args[i].equals("-o")) {   // We are processing the argument that specifies
                // the output file to be used to save the board
                outputBoard = args[i + 1];
            } else if (args[i].equals("-s")) {   // We are processing the argument that specifies
                // the size of the Board
                boardSize = Integer.parseInt(args[i + 1]);
            } else {   // Incorrect Argument
                printUsage();
                System.exit(-1);
            }
        }

        // Set the default output file if none specified
        if (outputBoard == null)
            outputBoard = "2048.board";
        // Set the default Board size if none specified or less than 2
        if (boardSize < 2)
            boardSize = 4;

        // Initialize the Game Board
        try {
            if (inputBoard != null)
                board = new Board2048();
            else
                board = new Board2048();
        } catch (Exception e) {
            System.out.println(e.getClass().getName() +
                    " was thrown while creating a " +
                    "Board from file " + inputBoard);
            System.out.println("Either your Board(String, Random) " +
                    "Constructor is broken or the file isn't " +
                    "formated correctly");
            System.exit(-1);
        }
    }

    // Print the Usage Message
    private static void printUsage() {
        System.out.println("Gui2048");
        System.out.println("Usage:  Gui2048 [-i|o file ...]");
        System.out.println();
        System.out.println("  Command line arguments come in pairs of the " +
                "form: <command> <argument>");
        System.out.println();
        System.out.println("  -i [file]  -> Specifies a 2048 board that " +
                "should be loaded");
        System.out.println();
        System.out.println("  -o [file]  -> Specifies a file that should be " +
                "used to save the 2048 board");
        System.out.println("                If none specified then the " +
                "default \"2048.board\" file will be used");
        System.out.println("  -s [size]  -> Specifies the size of the 2048" +
                "board if an input file hasn't been");
        System.out.println("                specified.  If both -s and -i" +
                "are used, then the size of the board");
        System.out.println("                will be determined by the input" +
                " file. The default size is 4.");
    }
}
