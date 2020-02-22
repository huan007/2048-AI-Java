package Game2048.Game;

import java.util.Scanner;

public class Benchmark2048 {
    public static void main(String args[]) {
        int numOfThreads = 3;
        Scanner in = new Scanner(System.in);
        int numberOfGames = 100;
        GameInstance.initialize(9, numberOfGames, numOfThreads);
        Thread[] threads = new Thread[numOfThreads];
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < numOfThreads; i++) {
            Thread t = new Thread(new GameInstance());
            threads[i] = t;
            t.start();
        }

        for (int i = 0; i < numOfThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Benchmark is over!");
        System.out.println(String.format("Time elapse: %d seconds", (endTime - startTime) / 1000));
        GameInstance.printStatistics();
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
