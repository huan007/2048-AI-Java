public class GameInstance implements Runnable {
    private static int numberOfGames = 0;
    private static int numberOfGamesPlayed = 0;
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
                ExpectiMax expectiMax = new ExpectiMax(gameBoard, gameBoard.getScore(), 1);
                Board2048.Directions bestDirection = expectiMax.computeDecision();
                //System.out.println(gameBoard.toString());
                //System.out.println("Best direction: " + bestDirection.name());
                gameBoard.move(bestDirection.getRotateValue());
            }
            //System.out.println("Game over!");
            int largest = 0;
            for (int x = 0; x < 4; x++) {
                for (int y = 0; y < 4; y++) {
                    if (gameBoard.getBoard()[x][y] != 0)
                        if (gameBoard.getBoard()[x][y] > largest)
                            largest = gameBoard.getBoard()[x][y];
                }
            }
            //winCount[largest]++;
            System.out.println(String.format("Game %d is now finished", gameNumber));
        }
    }

    public static synchronized void initialize(int numberOfGames) {
        GameInstance.numberOfGames = numberOfGames;
        GameInstance.numberOfGamesPlayed = 0;
    }

    public static synchronized void setNumberOfGames(int numberOfGames) {
        GameInstance.numberOfGames = numberOfGames;
    }

    public static synchronized void incrementNumberOfGamesPlayed() {
        GameInstance.numberOfGamesPlayed++;
    }

    public static synchronized int fetchNextGame() {
        if (!(numberOfGamesPlayed == numberOfGames)) {
            GameInstance.numberOfGamesPlayed++;
            return GameInstance.numberOfGamesPlayed;
        }
        else return -1;
    }
}
