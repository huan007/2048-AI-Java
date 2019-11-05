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
            {pow(2,15), pow(2,14), pow(2,13), pow(2,12)},
            {pow(2,11), pow(2,10), pow(2,9), pow(2,8)},
            {pow(2,7), pow(2,6), pow(2,5), pow(2,4)},
            {pow(2,3), pow(2,2), pow(2,1), pow(2,0)}
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
        int spaceCount = 0;
        HashMap<Integer, List<Point>> tileMap = new HashMap<>();
        float colRating = 0;
        float rowRating = 0;
        for (int x = 0; x < boardSize; x++) {
            for (int y = 0; y < boardSize; y++) {
                int value = m_state.getBoard()[x][y];
                if (value == 0)
                    spaceCount++;
                if (!tileMap.containsKey(value))
                    tileMap.put(value, new ArrayList<>());
                // Add coordinate to the list for that tile value (i.e 16 at (0,1))
                tileMap.get(value).add(new Point(x,y));
            }
        }
        int direction = 1;
        for (int x = 0; x < boardSize; x++) {
            int lastValue = m_state.getBoard()[x][0];
            float currentColRating = 0;
            for (int y = 1; y < boardSize; y++) {
                int value = m_state.getBoard()[x][y];
                // Monotonically
                if (value <= lastValue) {
                    currentColRating += pow(4, lastValue);
                    lastValue = value;
                }
                else break;
            }
            colRating += currentColRating;
            // Flip the direction
            direction = -direction;
        }
        direction = 1;
        for (int y = 0; y < boardSize; y++) {
            int lastValue = m_state.getBoard()[0][y];
            float currentRowRating = 0;
            for (int x = 1; x < boardSize; x++) {
                int value = m_state.getBoard()[x][y];
                // Monotonically
                if (value <= lastValue) {
                    currentRowRating += pow(4, lastValue);
                    lastValue = value;
                }
                else break;
            }
            rowRating += currentRowRating;
            direction = -direction;
        }
        if (!m_state.checkIfCanGo())
            return 0;

        float spaceRating = spaceCount * 50;
        return rowRating + colRating + spaceRating;
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
