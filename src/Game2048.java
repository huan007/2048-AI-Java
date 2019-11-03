import java.util.Scanner;

public class Game2048 {
    public static void main(String args[]) {
        Board2048 gameBoard = new Board2048();
        Scanner in = new Scanner(System.in);
        while (true) {
            if (!gameBoard.checkIfCanGo())
                break;
            ExpectiMax expectiMax = new ExpectiMax(gameBoard, gameBoard.getScore(), 9);
            Board2048.Directions bestDirection = expectiMax.computeDecision();
            System.out.println(gameBoard.toString());
            System.out.println("Best direction: " + bestDirection.name());
            //System.out.print("Enter your next move: ");
            //String move = in.nextLine();
            //gameBoard.move(getDirection(move).getRotateValue());
            gameBoard.move(bestDirection.getRotateValue());

        }
        System.out.println("Game over!");
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
