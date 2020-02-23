package Game2048.Game;

import java.util.Random;

public class Board2048 {
    private long m_score;
    private int[][] m_board;
    private int m_boardSize;
    private final int smallerTileChance = 100;

    // Default Constructor
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

    // Constructor with known score and board
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

    // Deep Copy Constructor
    public Board2048(Board2048 board2048) {
        this(board2048.getScore(), board2048.getBoard(), board2048.getBoardSize());
    }

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


    private int sumList(int[] list, int start_index) {
        int sum = 0;
        for (int i = start_index; i < list.length; i++) {
            sum += list[i];
        }
        return sum;
    }

    public int getRotations(Directions direction) {
        return direction.getRotateValue();
    }

    public long getScore() {
        return m_score;
    }

    public int[][] getBoard() {
        return m_board;
    }

    public int getBoardSize() {
        return m_boardSize;
    }

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

    public enum Directions {
        UP(1), DOWN(3), LEFT(0), RIGHT(2);
        private final int rotateValue;

        Directions(int rotateValue) {
            this.rotateValue = rotateValue;
        }

        public int getRotateValue() {
            return rotateValue;
        }

        public static Directions getRandomDirection() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }
}
