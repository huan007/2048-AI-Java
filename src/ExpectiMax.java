public class ExpectiMax {
    private SimulationTreeNode m_rootNode;
    private long m_currentScore;
    private int mDepthOfTree;

    public ExpectiMax(Board2048 rootState, long currentScore, int depth_of_tree) {
        this.m_rootNode = new SimulationTreeNode(rootState, "max", currentScore);
        this.m_currentScore = currentScore;
        this.mDepthOfTree = depth_of_tree;
    }

    public void initAndBuildTree() {
        buildTree(m_rootNode, mDepthOfTree, 0);
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

                // Create a new node
                SimulationTreeNode newNode = new SimulationTreeNode(newBoard, "chance", newBoard.getScore());
                newNode.setDirection(direction);
                // Expand tree of the new node
                buildTree(newNode, level - 1, (nextPlayer + 1) % 2);
                // Append to root node
                node.addChild(newNode);
                // TODO: Dynamic Programming (Memory) here
            }
        }

        else {
            int count = 0;
            for (int y = 0; y < node.getState().getBoardSize(); y++) {
                for (int x = 0; x < node.getState().getBoardSize(); x++) {
                    if (node.getState().getBoard()[x][y] == 0) {
                        count++;
                        //Create a deep copy of the current state
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

    public SimulationTreeNode getRootNode() {
        return m_rootNode;
    }

    public long getCurrentScore() {
        return m_currentScore;
    }

    public int getDepthOfTree() {
        return mDepthOfTree;
    }
}
