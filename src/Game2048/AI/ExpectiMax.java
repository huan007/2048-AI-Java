package Game2048.AI;

import Game2048.Game.Board2048;

import java.util.HashMap;

public class ExpectiMax {
    private SimulationTreeNode m_rootNode;
    private long m_currentScore;
    private int m_depthOfTree;
    private HashMap<String, SimulationTreeNode> m_memory_build;
    private HashMap<String, Float> m_memory_calculate;

    public ExpectiMax(Board2048 rootState, long currentScore, int depth_of_tree) {
        this.m_rootNode = new SimulationTreeNode(rootState, "max", currentScore);
        this.m_currentScore = currentScore;
        this.m_depthOfTree = depth_of_tree;
        m_memory_build = new HashMap<>();
        m_memory_calculate = new HashMap<>();
    }

    public void initAndBuildTree() {
        int emptySpace = 0;
        int largest = 0;
        for (int x = 0; x < m_rootNode.getState().getBoardSize(); x++) {
            for (int y = 0; y < m_rootNode.getState().getBoardSize(); y++) {
                if (m_rootNode.getState().getBoard()[x][y] == 0)
                    emptySpace++;
                if (m_rootNode.getState().getBoard()[x][y] != 0)
                    if (m_rootNode.getState().getBoard()[x][y] > largest)
                        largest = m_rootNode.getState().getBoard()[x][y];
            }
        }
        if (emptySpace <= 4) {
            int depth;
            depth = m_depthOfTree + 4 - emptySpace;
            //System.gc();
            buildTree(m_rootNode, depth, 0);
        }
        else
            buildTree(m_rootNode, m_depthOfTree, 0);
    }

    public void buildTree(SimulationTreeNode node, int level, int nextPlayer) {
        if ((node == null) || (level == 0))
            return;

        if (nextPlayer  % 2 == 0) {
            for (Board2048.Directions direction :Board2048.Directions.values()) {
                // Create a copy
                Board2048 newBoard = new Board2048(node.getState());
                // Move
                newBoard.moveOnly(direction.getRotateValue());
                // Skip if we can't move
                if (node.getState().equals(newBoard))
                    continue;

                String newBoardKey = newBoard.toStringKey(level-1);
                if (m_memory_build.containsKey(newBoardKey))
                    node.addChild(m_memory_build.get(newBoardKey));
                else {
                    // Create a new node
                    SimulationTreeNode newNode = new SimulationTreeNode(newBoard, "chance", newBoard.getScore());
                    newNode.setDirection(direction);
                    // Expand tree of the new node
                    buildTree(newNode, level - 1, (nextPlayer + 1) % 2);
                    // Append to root node
                    node.addChild(newNode);
                    m_memory_build.put(newBoardKey, newNode);
                }
            }
        }

        else {
            int count = 0;
            for (int y = 0; y < node.getState().getBoardSize(); y++) {
                for (int x = 0; x < node.getState().getBoardSize(); x++) {
                    if (node.getState().getBoard()[x][y] == 0) {
                        count++;
                        // Placing 2 Piece
                        Board2048 newBoard = new Board2048(node.getState());
                        newBoard.getBoard()[x][y] = 1;
                        SimulationTreeNode newNode =
                                new SimulationTreeNode(newBoard, "max", node.getState().getScore());
                        // Expand tree of the new node
                        buildTree(newNode, level - 1, (nextPlayer + 1) % 2);
                        newNode.setChance(0.9);
                        node.addChild(newNode);
                        // Placing 4 Piece
                        newBoard = new Board2048(node.getState());
                        newBoard.getBoard()[x][y] = 2;
                        newNode = new SimulationTreeNode(newBoard, "max", node.getState().getScore());
                        // Expand tree of the new node
                        buildTree(newNode, level - 1, (nextPlayer + 1) % 2);
                        newNode.setChance(0.1);
                        node.addChild(newNode);
                    }
                }
            }
            double chance = 0;
            if (count != 0)
                chance = (double) 1 / (double) count;
            for (SimulationTreeNode child : node.getChildren())
                child.setChance(child.getChance() * chance);
        }
    }

    public float expectimax(SimulationTreeNode node) {
        String stateKey = node.getState().toStringKey(1);
        if (m_memory_calculate.containsKey(stateKey))
            return m_memory_calculate.get(stateKey);
        else {
            // Terminal Node
            if (node.isTerminal()) {
                float payoffValue = node.payoff();
                m_memory_calculate.put(stateKey, payoffValue);
                return payoffValue;
            }
            // Max Player Node
            else if (node.isMaxPlayer()) {
                float maxValue = -Float.MAX_VALUE;
                for (SimulationTreeNode child : node.getChildren()) {
                    float newValue = expectimax(child);
                    if (newValue > maxValue)
                        maxValue = newValue;
                }
                m_memory_calculate.put(stateKey, maxValue);
                return maxValue;
            }
            // Chance Player Node
            else if (node.isChancePlayer()) {
                float value = 0;
                for (SimulationTreeNode child : node.getChildren()) {
                    value += expectimax(child) * child.getChance();
                }
                m_memory_calculate.put(stateKey, value);
                return value;
            }
            // Nobody? Error!
            else {
                System.err.println("ERROR! Node is not terminal, max, or chance player");
                return 0;
            }
        }
    }

    public Board2048.Directions computeDecision() {
        int[][] grid = m_rootNode.getState().getBoard();
        if ((grid[3][0] == grid[2][1]) && (grid[2][1] == grid[3][1]))
            if ((grid[3][0] != 0) && (grid[2][1] != 0) && (grid[3][1] != 0))
                if ((grid[0][0] != 0) && (grid[1][0] != 0) && (grid[2][0] != 0))
                    return Board2048.Directions.RIGHT;
        initAndBuildTree();
        float maxValue = -Float.MAX_VALUE;
        Board2048.Directions maxDirection = null;
        int repeatCount = 0;
        for (SimulationTreeNode child : m_rootNode.getChildren()) {
            float value = expectimax(child);
            if (value > maxValue) {
                maxValue = value;
                maxDirection = child.getDirection();
                repeatCount = 0;
            }
            else if (value == maxValue)
                repeatCount++;
        }

        if (repeatCount == m_rootNode.getChildren().size()-1) {
            //System.out.println("Had to pick random");
            return Board2048.Directions.getRandomDirection();
        }
        return maxDirection;
    }

    public SimulationTreeNode getRootNode() {
        return m_rootNode;
    }

    public long getCurrentScore() {
        return m_currentScore;
    }

    public int getDepthOfTree() {
        return m_depthOfTree;
    }
}
