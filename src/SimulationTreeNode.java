import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.Math.pow;

public class SimulationTreeNode {
    private Board2048 m_state;
    private String m_nextTurn;
    private long m_score;
    private Board2048.Directions m_direction;
    private double m_chance;
    private List<SimulationTreeNode> m_children;

    private static double[][] m_weightMatrix = new double[][] {
            {pow(4,15), pow(4,14), pow(4,13), pow(4,12)},
            {pow(4,11), pow(4,10), pow(4,9), pow(4,8)},
            {pow(4,7), pow(4,6), pow(4,5), pow(4,4)},
            {pow(4,3), pow(4,2), pow(4,1), pow(4,0)}
    };

    public SimulationTreeNode(Board2048 state, String nextTurn, long score) {
        m_state = state;
        m_nextTurn = nextTurn;
        m_score = score;
        m_children = new ArrayList<>();
        m_chance = 1.0;
    }

    public void addChild(SimulationTreeNode child) {
        m_children.add(child);
    }

    public float payoff() {
        int boardSize = m_state.getBoardSize();
        float baseRating = 0;
        float smoothRating = 0;
        int spaceCount = 0;
        HashMap<Integer, List<Point>> tileMap = new HashMap<>();
        for (int x = 0; x < boardSize; x++) {
            for (int y = 0; y < boardSize; y++) {
                int value = m_state.getBoard()[x][y];
                baseRating += pow(2,value) * m_weightMatrix[x][y];
                if (value == 0)
                    spaceCount++;
                if (!tileMap.containsKey(value))
                    tileMap.put(value, new ArrayList<>());
                // Add coordinate to the list for that tile value (i.e 16 at (0,1))
                tileMap.get(value).add(new Point(x,y));
            }
        }
        for (int value : tileMap.keySet()) {
            List<Point> coordinates = (List<Point>) tileMap.get(value);
            int length = coordinates.size();
            for (int i = 0; i < length; i++) {
                for (int j = 1+1; j < length; j++) {
                    Point coordinate1 = coordinates.get(i);
                    Point coordinate2 = coordinates.get(j);
                    if ((Math.abs(coordinate1.x - coordinate2.x) == 0) && (Math.abs(coordinate1.y - coordinate2.y) == 1))
                        smoothRating++;
                    if ((Math.abs(coordinate1.x - coordinate2.x) == 1) && (Math.abs(coordinate1.y - coordinate2.y) == 0))
                        smoothRating++;
                }
            }
        }
        smoothRating = smoothRating * 50;
        float spaceRating = spaceCount * 20;
        float finalRating = baseRating + spaceRating + smoothRating;
        //Check if can only go down
        Board2048 tempBoard= new Board2048(m_state);
        boolean canMoveUp = tempBoard.checkIfCanMoveDirection(Board2048.Directions.UP);
        boolean canMoveLeft = tempBoard.checkIfCanMoveDirection(Board2048.Directions.LEFT);
        boolean canMoveRight = tempBoard.checkIfCanMoveDirection(Board2048.Directions.RIGHT);
        if ((!canMoveUp && !canMoveLeft && !canMoveRight) && (spaceCount > 4))
            return finalRating/2;

        if (!m_state.checkIfCanGo())
            return 0;

        return  finalRating;
    }

    public boolean isTerminal() {
        if (m_children.size() == 0)
            return true;
        else return false;
    }

    public boolean isMaxPlayer() {
        if (m_nextTurn.equalsIgnoreCase("max"))
            return true;
        else return false;
    }

    public boolean isChancePlayer() {
        if (m_nextTurn.equalsIgnoreCase("chance"))
            return true;
        else return false;
    }

    public List<SimulationTreeNode> getChildren() {
        return m_children;
    }

    public void setChance(double chance) {
        m_chance = chance;
    }

    public void setDirection(Board2048.Directions direction) {
        m_direction = direction;
    }

    public Board2048 getState() {
        return m_state;
    }

    public String getNextTurn() {
        return m_nextTurn;
    }

    public long getScore() {
        return m_score;
    }

    public Board2048.Directions getDirection() {
        return m_direction;
    }

    public double getChance() {
        return m_chance;
    }

    private class Point {
        public int x;
        public int y;
        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
