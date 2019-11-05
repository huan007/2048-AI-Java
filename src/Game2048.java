import java.util.Scanner;

import static java.lang.Math.pow;

public class Game2048 {
    public static void main(String args[]) {
        int numOfThreads = 4;
        Scanner in = new Scanner(System.in);
        int numberOfGames = 4;
        int[] winCount = new int[16];
        GameInstance.initialize(numberOfGames);
        for (int i = 0; i < numOfThreads; i++) {
            Thread t = new Thread(new GameInstance());
            t.start();
        }

        //System.out.println("Benchmark is over!");
        //System.out.println("Statistic: ");
        //for (int i = 0; i < 16; i++) {
        //    if (winCount[i] != 0)
        //        System.out.println(String.format("%d:\t%d", (int) pow(2,i), winCount[i]));
        //}
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
