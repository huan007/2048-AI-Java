import java.util.ArrayList;
import java.util.List;

public class SimulationTreeNode {
    private Board2048 m_state;
    private String m_nextTurn;
    private long m_score;
    private Board2048.Directions m_direction;
    private double m_chance;
    private List<SimulationTreeNode> m_children;

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
}
