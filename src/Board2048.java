import java.util.Random;

public class Board2048 {
    private long m_score;
    private int[][] m_board;
    private int m_boardSize;

    // Default Constructor
    public Board2048() {
        this.m_score = 0;
        this.m_boardSize = 4;
        this.m_board = new int[][] {
                {0, 0, 0, 0},
                {1, 0, 0, 0},
                {0, 0, 0, 0},
                {1, 0, 0, 0}
        };
        //placeRandomTile();
        //placeRandomTile();
    }

    // Constructor with known score and board
    public Board2048(long score, int[][] board) {
        this.m_score = score;
        this.m_board = board;
    }

    // Deep Copy Constructor
    public Board2048(Board2048 board2048) {
        this.m_score = board2048.getScore();
        this.m_board = board2048.getBoard();
    }

    private void rotateMatrixClockwise() {
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
            i = random.nextInt(boardSize - 1);
            j = random.nextInt(boardSize - 1);
            if (this.m_board[i][j] == 0)
                break;
        }
        this.m_board[i][j] = 1;
    }

    public void move(int direction) {
        for (int i = 0; i < direction; i++) {
            this.rotateMatrixClockwise();
            //System.out.println("Rotating Counter Clockwise");
            //System.out.println(this.toString());
        }
        if (this.canMove()) {
            this.moveTiles();
            //System.out.println("Moved");
            //System.out.println(this.toString());
            this.mergeTiles();
            //System.out.println("Merged");
            //System.out.println(this.toString());
            this.placeRandomTile();
            //System.out.println("Place Random Piece");
            //System.out.println(this.toString());
        }
        for (int i = 0; i < ((4 - direction) % 4); i++) {
            this.rotateMatrixClockwise();
        }
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
                    this.m_score += tm[i][j];
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

    public enum Directions {
        UP(1), DOWN(3), LEFT(0), RIGHT(2);
        private final int rotateValue;

        Directions(int rotateValue) {
            this.rotateValue = rotateValue;
        }

        int getRotateValue() {
            return rotateValue;
        }
    }
}
