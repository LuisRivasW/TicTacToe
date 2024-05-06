import javax.swing.JButton;

public class Game {
    private char[][] board;
    private JButton[][] buttons;
    private boolean gameOver;
    private Player player1;
    private Player player2;

    public Game(JButton[][] buttons, Player player1, Player player2) {
        this.buttons = buttons;
        this.player1 = player1;
        this.player2 = player2;
        board = new char[3][3];
        gameOver = false;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = '-';
            }
        }
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean makeMove(int row, int col, Player player) {
        if (row >= 0 && col >= 0 && row < 3 && col < 3) {
            if (board[row][col] == '-') {
                board[row][col] = player.getSymbol();
                updateButton(row, col, player);
                if (checkWinner()) {
                    gameOver = true;
                } else if (isBoardFull()) {
                    gameOver = true;
                }
                return true;
            }
        }
        return false;
    }

    public boolean checkWinner() {
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == board[i][1] && board[i][1] == board[i][2] && board[i][0] != '-') {
                return true;
            }
        }

        for (int i = 0; i < 3; i++) {
            if (board[0][i] == board[1][i] && board[1][i] == board[2][i] && board[0][i] != '-') {
                return true;
            }
        }

        if (board[0][0] == board[1][1] && board[1][1] == board[2][2] && board[0][0] != '-') {
            return true;
        }

        if (board[0][2] == board[1][1] && board[1][1] == board[2][0] && board[0][2] != '-') {
            return true;
        }

        return false;
    }

    public void printBoard() {
        for (int i = 0; i < 3; i++) {
            System.out.println(board[i][0] + " " + board[i][1] + " " + board[i][2]);
        }
    }

    public boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '-') {
                    return false;
                }
            }
        }
        return true;
    }

    public char getBoardState(int row, int col) {
        if (row >= 0 && col >= 0 && row < 3 && col < 3) {
            return board[row][col];
        }
        return '-';
    }

    public void updateButton(int x, int y, Player player) {
        buttons[x][y].setText(String.valueOf(player.getSymbol()));
    }
}