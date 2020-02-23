package Game2048.AI;

import Game2048.Game.Board2048;

import java.util.HashMap;

public class ExpectiMax {
    private SimulationTreeNode m_rootNode;
    private long m_currentScore;
    private int m_depthOfTree;
    private int m_extraDepth = 2;
    private HashMap<String, SimulationTreeNode> m_memory_build;
    private HashMap<String, Double> m_memory_calculate;

    /**
     * Consturctor to create an instance of the Expectimax algorithm given the root state.
     * @param rootState original game state, will become the root of the tree
     * @param currentScore score achieved by the given game state
     * @param depth_of_tree number of levels that will be in the tree
     */
    public ExpectiMax(Board2048 rootState, long currentScore, int depth_of_tree) {
        this.m_rootNode = new SimulationTreeNode(rootState, "max", currentScore);
        this.m_currentScore = currentScore;
        this.m_depthOfTree = depth_of_tree;
        m_memory_build = new HashMap<>();
        m_memory_calculate = new HashMap<>();
    }

    /**
     * Pre-process the root state to determine parameters and build the tree.
     */
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
            int depth = m_depthOfTree + m_extraDepth;
            //System.gc();
            buildTree(m_rootNode, depth, 0);
        }
        else
            buildTree(m_rootNode, m_depthOfTree, 0);
    }

    /**
     * Recursively build the tree.
     * @param node node to build the tree off of
     * @param level current level of the tree that this node belongs to
     * @param nextPlayer next player in turn
     */
    public void buildTree(SimulationTreeNode node, int level, int nextPlayer) {
        if ((node == null) || (level == 0))
            return;

        // Build tree for max player
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

        // Build tree for random player
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
                        node.addChild(newNode);
                    }
                }
            }
            double chance = 0;
            if (count != 0)
                chance = (double) 1 / (double) count;
            for (SimulationTreeNode child : node.getChildren())
                child.setChance(chance);
        }
    }

    /**
     * Recursively perform expectimax algorithm to determine value of the given node.
     * @param node node to calculate expectimax value
     * @param level current level of the tree that this node belongs to
     * @return expectimax value of the node, will be used by parent node
     * to determine their value
     */
    public double expectimax(SimulationTreeNode node, int level) {
        String stateKey = node.getState().toStringKey(level-1);
        if (m_memory_calculate.containsKey(stateKey))
            return m_memory_calculate.get(stateKey);
        else {
            // Terminal Node
            if (node.isTerminal()) {
                double payoffValue = node.payoff();
                m_memory_calculate.put(stateKey, payoffValue);
                return payoffValue;
            }
            // Max Player Node
            else if (node.isMaxPlayer()) {
                double maxValue = -Float.MAX_VALUE;
                for (SimulationTreeNode child : node.getChildren()) {
                    double newValue = expectimax(child, level-1);
                    if (newValue > maxValue)
                        maxValue = newValue;
                }
                m_memory_calculate.put(stateKey, maxValue);
                return maxValue;
            }
            // Chance Player Node
            else if (node.isChancePlayer()) {
                double value = 0;
                for (SimulationTreeNode child : node.getChildren()) {
                    value += expectimax(child, level-1) * child.getChance();
                }
                m_memory_calculate.put(stateKey, value);
                return value;
            }
            // Nobody? Error!
            else {
                System.err.println("ERROR: Node is not terminal, max, or chance player");
                return 0;
            }
        }
    }

    /**
     * Compute optimal decision from the root state. Will build the tree and
     * perform Expectimax on the root node.
     * @return optimal move represented by a Directions object
     */
    public Board2048.Directions computeDecision() {
        initAndBuildTree();
        double maxValue = -Float.MAX_VALUE;
        Board2048.Directions maxDirection = null;
        int repeatCount = 0;
        for (SimulationTreeNode child : m_rootNode.getChildren()) {
            double value = expectimax(child, m_depthOfTree);
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

    /**
     * Get root node of the Expectimax instance.
     * @return root node
     */
    public SimulationTreeNode getRootNode() {
        return m_rootNode;
    }

    /**
     * Get current score of the current game state.
     * @return current score
     */
    public long getCurrentScore() {
        return m_currentScore;
    }

    /**
     * Get the depth of tree that AI will perform Expectimax on.
     * @return depth of tree
     */
    public int getDepthOfTree() {
        return m_depthOfTree;
    }
}
