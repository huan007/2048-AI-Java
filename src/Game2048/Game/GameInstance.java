package Game2048.Game;

import Game2048.AI.ExpectiMax;

import static java.lang.Math.pow;

public class GameInstance implements Runnable {
    private static int numberOfGames = 0;
    private static int numberOfGamesPlayed = 0;
    private static int numberOfThreads = 0;
    private static int[] winCount = new int[16];
    private static int depth = 3;

    /**
     * Play the game with AI enabled on a separate thread.
     */
    @Override
    public void run() {
        // If there is more game to play then play the game, if not then stop the thread
        int gameNumber = -1;
        while ((gameNumber = fetchNextGame()) != -1) {
            System.out.println(String.format("Game %d is now playing", gameNumber));
            Board2048 gameBoard = new Board2048();
            while (true) {
                if (!gameBoard.checkIfCanGo())
                    break;
                ExpectiMax expectiMax = new ExpectiMax(gameBoard, gameBoard.getScore(), depth);
                Board2048.Directions bestDirection = expectiMax.computeDecision();
                if (numberOfThreads == 1) {
                    System.out.println(gameBoard.toString());
                    System.out.println("Best direction: " + bestDirection.name());
                }
                gameBoard.move(bestDirection.getRotateValue());
            }
            if (numberOfThreads == 1)
                System.out.println("Game over!");
            int largest = 0;
            for (int x = 0; x < 4; x++) {
                for (int y = 0; y < 4; y++) {
                    if (gameBoard.getBoard()[x][y] != 0)
                        if (gameBoard.getBoard()[x][y] > largest)
                            largest = gameBoard.getBoard()[x][y];
                }
            }
            recordLargestAchieved(largest);
            System.out.println(String.format("Game %d is now finished", gameNumber));
        }
    }

    /**
     * Initialize information that will be shared between threads.
     * @param depth depths of search that Expectiminimax will perform
     * @param numberOfGames number of games to be played
     * @param numberOfThreads number of threads that will be spawned
     */
    public static synchronized void initialize(int depth, int numberOfGames, int numberOfThreads) {
        GameInstance.depth = depth;
        GameInstance.numberOfGames = numberOfGames;
        GameInstance.numberOfGamesPlayed = 0;
        GameInstance.numberOfThreads = numberOfThreads;
    }

    /**
     * Set number of games to be played
     * @param numberOfGames number of games to be played
     */
    public static synchronized void setNumberOfGames(int numberOfGames) {
        GameInstance.numberOfGames = numberOfGames;
    }

    /**
     * Increment the number of games played.
     */
    public static synchronized void incrementNumberOfGamesPlayed() {
        GameInstance.numberOfGamesPlayed++;
    }

    /**
     * Fetch the next game number and increment the number of games played.
     * @return next game's number, -1 if all games are currently played
     */
    public static synchronized int fetchNextGame() {
        if (!(numberOfGamesPlayed == numberOfGames)) {
            GameInstance.numberOfGamesPlayed++;
            return GameInstance.numberOfGamesPlayed;
        }
        else return -1;
    }

    /**
     * Record the largest tile achieved from the given game
     * @param largest largest tile achieved from the given game
     */
    public static synchronized void recordLargestAchieved(int largest) {
        GameInstance.winCount[largest]++;
    }

    /**
     * Print out stats: Number of tiles achieved for the given tile value.
     */
    public static synchronized void printStatistics() {
        System.out.println("Statistic: ");
        for (int i = 0; i < 16; i++) {
            if (winCount[i] != 0)
                System.out.println(String.format("%d:\t%d", (int) pow(2,i), winCount[i]));
        }
    }
}
