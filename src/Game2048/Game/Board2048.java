package Game2048.Game;

import java.util.Random;

public class Board2048 {
    private long m_score;
    private int[][] m_board;
    private int m_boardSize;
    private final int smallerTileChance = 100;

    /**
     * Default constructor. Create 4x4 board and place 2 random tiles.
     */
    public Board2048() {
        this.m_score = 0;
        this.m_boardSize = 4;
        this.m_board = new int[][] {
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        };
        placeRandomTile();
        placeRandomTile();
    }

    /**
     * Deep copy constructor.
     * @param score initial score
     * @param board initial game board (will be deep copied)
     * @param boardSize game board size
     */
    public Board2048(long score, int[][] board, int boardSize) {
        this();
        this.m_boardSize = boardSize;
        this.m_score = score;
        this.m_board = new int[m_boardSize][m_boardSize];
        // Perform Deep Copy
        for (int i = 0; i < m_boardSize; i++) {
            for (int j = 0; j < m_boardSize; j++) {
                m_board[i][j] = board[i][j];
            }
        }
    }

    /**
     * Simpler deep copy constructor.
     * @param board2048 original Board2048 object to be deep copied
     */
    public Board2048(Board2048 board2048) {
        this(board2048.getScore(), board2048.getBoard(), board2048.getBoardSize());
    }

    /**
     * Rotate the game board clock wise
     */
    protected void rotateMatrixClockwise() {
        int[][] board = this.getBoard();
        int boardSize = this.m_boardSize;
        int halfLimit = (int) (boardSize / 2);
        for (int i = 0; i < halfLimit; i++) {
            for (int j = i; j < boardSize - i - 1; j++) {
                int temp1 = board[i][j];
                int temp2 = board[boardSize - 1 - j][i];
                int temp3 = board[boardSize - 1 - i][boardSize - 1 - j];
                int temp4 = board[j][boardSize - 1 - i];
                board[boardSize - 1 - j][i] = temp1;
                board[boardSize - 1 - i][boardSize - 1 - j] = temp2;
                board[j][boardSize - 1 - i] = temp3;
                board[i][j] = temp4;
            }
        }
    }

    /**
     * Check whether or not the board can be moved to the left.
     * Prevent placing unnecessary random tiles.
     * @return true if the board can be moved to the left, false otherwise.
     */
    public boolean canMove() {
        int[][] board = this.m_board;
        int boardSize = this.m_boardSize;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 1; j < boardSize; j++) {
                if (board[i][j-1] == 0 && board[i][j] > 0)
                    return true;
                else if ((board[i][j-1] == board[i][j]) && (board[i][j-1] != 0))
                    return true;
            }
        }
        return false;
    }

    /**
     * Check whether or not game is over.
     * @return true if game is not over, false if game is over.
     */
    public boolean checkIfCanGo() {
        int[][] tm = this.m_board;
        int range = m_boardSize * m_boardSize;
        for (int i = 0; i < range; i++) {
            if (tm[(i / m_boardSize)][i % m_boardSize] == 0)
                return true;
        }
        for (int i = 0; i < m_boardSize; i++) {
            for (int j = 0; j < m_boardSize - 1; j++) {
                if (tm[i][j] == tm[i][j + 1])
                    return true;
                else if (tm[j][i] == tm[j + 1][i])
                    return true;
            }
        }
        return false;
    }

    /**
     * Place a new tile randomly in one of the empty spaces of the board.
     */
    public void placeRandomTile() {
        int boardSize = this.m_boardSize;
        int i = 0;
        int j = 0;
        Random random = new Random();
        while (true) {
            i = random.nextInt(boardSize);
            j = random.nextInt(boardSize);
            if (this.m_board[i][j] == 0)
                break;
        }
        if (random.nextInt(100) < smallerTileChance)
            this.m_board[i][j] = 1;
        else
            this.m_board[i][j] = 2;
    }

    /**
     * Move the board in the given direction.
     * @param direction direction to move the board in
     */
    public void move(int direction) {
        for (int i = 0; i < direction; i++) {
            this.rotateMatrixClockwise();
        }
        if (this.canMove()) {
            this.moveTiles();
            this.mergeTiles();
            this.placeRandomTile();
        }
        for (int i = 0; i < ((4 - direction) % 4); i++) {
            this.rotateMatrixClockwise();
        }
    }

    /**
     * Check if the board can be moved in the given direction.
     * @param direction direction that the user want to move the board in
     * @return true if the board can be moved, false if it cannot
     */
    public boolean checkIfCanMoveDirection(Directions direction) {
        boolean result = false;
        for (int i = 0; i < direction.getRotateValue(); i++) {
            this.rotateMatrixClockwise();
        }
        if (this.canMove()) {
            result = true;
        }
        for (int i = 0; i < ((4 - direction.getRotateValue()) % 4); i++) {
            this.rotateMatrixClockwise();
        }
        return result;
    }

    /**
     * Move the board in the given direction without spawning new tile.
     * This method will be used heavily by the AI to separate user's turn
     * and the game's turn.
     * @param direction direction to move the board in
     */
    public void moveOnly(int direction) {
        for (int i = 0; i < direction; i++)
            this.rotateMatrixClockwise();
        if (this.canMove()) {
            this.moveTiles();
            this.mergeTiles();
        }
        for (int i = 0; i < ((4 - direction) % 4); i++)
            this.rotateMatrixClockwise();
    }

    /**
     * Move all the tiles to the left side of the board.
     */
    public void moveTiles() {
        int[][] tm = this.m_board;
        int boardSize = this.m_boardSize;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize - 1; j++) {
                while ((tm[i][j] == 0) && (sumList(tm[i], j) > 0)) {
                    for (int k = j; k < boardSize - 1; k++) {
                        tm[i][k] = tm[i][k + 1];
                    }
                    tm[i][boardSize - 1] = 0;
                }
            }
        }
    }

    /**
     * Merge adjacent tile with same value. Should be called after moveTiles().
     */
    public void mergeTiles() {
        int[][] tm = this.m_board;
        int boardSize = this.m_boardSize;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize - 1; j++) {
                if ((tm[i][j] == tm[i][j + 1]) && (tm[i][j] != 0)) {
                    tm[i][j] = tm[i][j] + 1;
                    tm[i][j + 1] = 0;
                    this.m_score += Math.pow(2, tm[i][j]);
                    this.moveTiles();
                }
            }
        }
    }

    /**
     * Sum all the elements of the list from the given index. Has the same
     * function as the "sum" function in Python.
     * @param list list of integers to be summed
     * @param start_index index to begin counting (inclusive).
     * @return sum of desired elements.
     */
    private int sumList(int[] list, int start_index) {
        int sum = 0;
        for (int i = start_index; i < list.length; i++) {
            sum += list[i];
        }
        return sum;
    }

    /**
     * Get rotation value from Directions object.
     * @param direction Directions object to extract value from
     * @return how many rotations needed to perform before moving boards to the left
     */
    public int getRotations(Directions direction) {
        return direction.getRotateValue();
    }

    /**
     * Get score of the current game.
     * @return score
     */
    public long getScore() {
        return m_score;
    }

    /**
     * Get the 2D  array that represent the game board.
     * @return game board represented in 2D array
     */
    public int[][] getBoard() {
        return m_board;
    }

    /**
     * Get size of size game board.
     * @return size of the game board
     */
    public int getBoardSize() {
        return m_boardSize;
    }

    /**
     * Compare this Board2048 to another object.
     * @param obj object to be compared
     * @return true if equals, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        Board2048 board = (Board2048) obj;
        if (board.getBoardSize() != m_boardSize)
            return false;
        for (int i = 0; i < m_boardSize; i++) {
            for (int j = 0; j < m_boardSize; j++) {
                // Two boards have different pieces
                if (board.m_board[i][j] != m_board[i][j])
                    return false;
            }
        }
        return true;
    }

    /**
     * Provide string representation of the game board for printing.
     * @return string representation of the game board
     */
    @Override
    public String toString() {
        String result = "";
        for (int i = 0; i < m_boardSize; i++) {
            for (int j = 0; j < m_boardSize; j++) {
                result += m_board[i][j];
                result += "\t";
            }
            result += "\n";
        }
        return result;
    }

    /**
     * Provide serialized data of the board to be used as a key in HashMap
     * to identify unique game state at the given level.
     * @param level level that the game state appear in the decision tree
     * @return serial representation of the data
     */
    public String toStringKey(int level) {
        String result = "";
        for (int i = 0; i < m_boardSize; i++) {
            for (int j = 0; j < m_boardSize; j++) {
                result += m_board[i][j];
                if (j != m_boardSize - 1)
                    result += ",";
            }
            result += ";";
        }
        result += level;
        return result;
    }

    /**
     * Enumerated class that represent directions to move the board in.
     */
    public enum Directions {
        UP(1), DOWN(3), LEFT(0), RIGHT(2);
        private final int rotateValue;

        /**
         * Constructor with integer rotation value.
         * @param rotateValue
         */
        Directions(int rotateValue) {
            this.rotateValue = rotateValue;
        }

        /**
         * Get the number of rotations to perform on the board before moving the
         * board left will achieve a move in the given direction.
         * @return
         */
        public int getRotateValue() {
            return rotateValue;
        }

        /**
         * Get random Directions object.
         * @return random Directions object
         */
        public static Directions getRandomDirection() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }
}
