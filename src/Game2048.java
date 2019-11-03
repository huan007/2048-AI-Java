import java.util.Scanner;

public class Game2048 {
    public static void main(String args[]) {
        Board2048 gameBoard = new Board2048();
        Scanner in = new Scanner(System.in);
        while (true) {
            if (!gameBoard.checkIfCanGo())
                break;
            ExpectiMax expectiMax = new ExpectiMax(gameBoard, gameBoard.getScore(), 3);
            expectiMax.initAndBuildTree();
            System.out.println("Game simulated!");
            for (SimulationTreeNode child : expectiMax.getRootNode().getChildren()) {
                System.out.println("Simulation: " + child.getDirection().name());
                System.out.println(child.getState().toString());
            }
            System.out.println(gameBoard.toString());
            System.out.print("Enter your next move: ");
            String move = in.nextLine();
            gameBoard.move(getDirection(move).getRotateValue());

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
