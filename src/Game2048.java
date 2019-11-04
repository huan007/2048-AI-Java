import java.util.Scanner;

import static java.lang.Math.pow;

public class Game2048 {
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int numberOfGames = 100;
        int[] winCount = new int[16];
        for (int i = 0; i < numberOfGames; i++) {
            Board2048 gameBoard = new Board2048();
            while (true) {
                if (!gameBoard.checkIfCanGo())
                    break;
                ExpectiMax expectiMax = new ExpectiMax(gameBoard, gameBoard.getScore(), 3);
                Board2048.Directions bestDirection = expectiMax.computeDecision();
                System.out.println(gameBoard.toString());
                System.out.println("Best direction: " + bestDirection.name());
                //System.out.print("Enter your next move: ");
                //String move = in.nextLine();
                //gameBoard.move(getDirection(move).getRotateValue());
                gameBoard.move(bestDirection.getRotateValue());
            }
            System.out.println("Game over!");
            int largest = 0;
            for (int x = 0; x < 4; x++) {
                for (int y = 0; y < 4; y++) {
                    if (gameBoard.getBoard()[x][y] != 0)
                        if (gameBoard.getBoard()[x][y] > largest)
                            largest = gameBoard.getBoard()[x][y];
                }
            }
            winCount[largest]++;
        }
        System.out.println("Benchmark is over!");
        System.out.println("Statistic: ");
        for (int i = 0; i < 16; i++) {
            if (winCount[i] != 0)
                System.out.println(String.format("%d:\t%d", (int) pow(2,i), winCount[i]));
        }
    }

    public static Board2048.Directions getDirection(String move) {
        if (move.indexOf("w") != -1)
            return Board2048.Directions.UP;
        if (move.indexOf("s") != -1)
            return Board2048.Directions.DOWN;
        if (move.indexOf("a") != -1)
            return Board2048.Directions.LEFT;
        return Board2048.Directions.RIGHT;
    }
}
