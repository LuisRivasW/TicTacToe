import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URISyntaxException;

public class Main {
    private Player player;
    private Player[] players = new Player[2];

    public static void main(String[] args) {
        Main main = new Main();
        main.startGame();
    }

    public void startGame() {
        JButton[][] buttons = new JButton[3][3];

        URI serverUri;
        try {
            serverUri = new URI("ws://localhost:52301/ws");
        } catch (URISyntaxException e) {
            JOptionPane.showMessageDialog(null, "Error al conectar al servidor: " + e.getMessage());
            return;
        }

        JFrame frame = new JFrame("Tic Tac Toe");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 300);

        players[0] = new Player("PLAYER 1", 'X');
        players[1] = new Player("PLAYER 2", 'O');
        player = players[0];

        JLabel statusLabel = new JLabel(player.getName() + " turno (simbolo " + player.getSymbol() + ")");

        Game game = new Game(buttons, players[0], players[1]);

        TicTacToeWebSocketClient client = new TicTacToeWebSocketClient(serverUri, game, players, frame, statusLabel,
                buttons);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 3));

        frame.add(statusLabel, BorderLayout.NORTH);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                final int finalI = i;
                final int finalJ = j;
                buttons[i][j] = new JButton();
                JButton button = buttons[i][j];
                button.setFont(new Font("Arial", Font.BOLD, 40));

                button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (button.getText().equals("") && !game.isGameOver()) {
                            handleMove(finalI, finalJ, button, client, game, buttons, frame, statusLabel);
                        }
                    }
                });
                panel.add(button);
            }
        }

        frame.add(panel);
        frame.setVisible(true);
        client.connect();
    }

    private void handleMove(int x, int y, JButton button, TicTacToeWebSocketClient client, Game game,
            JButton[][] buttons, JFrame frame, JLabel statusLabel) {
        for (JButton[] row : buttons) {
            for (JButton b : row) {
                b.setEnabled(false);
            }
        }

        String message = "MOVE " + player.getName() + " " + x + " " + y;
        client.send(message);
        button.setText(String.valueOf(player.getSymbol()));

        game.makeMove(x, y, player);

        if (game.checkWinner()) {
            String loser = (player == players[0]) ? players[1].getName() : players[0].getName();
            String winnerMessage = player.getName() + " HA GANADO";
            String serverMessage = "GAME OVER " + loser + ", " + winnerMessage;
            endGame(winnerMessage, serverMessage, client, game, frame);
        } else if (game.isBoardFull()) {
            endGame("Es un empate!", "GAME OVER, ES UN EMPATE", client, game, frame);
        }

        if (player == players[0]) {
            player = players[1];
        } else {
            player = players[0];
        }
        statusLabel.setText(player.getName() + " turno (símbolo " + player.getSymbol() + ")");

        for (JButton[] row : buttons) {
            for (JButton b : row) {
                b.setEnabled(true);
            }
        }
    }

    private void endGame(String message, String serverMessage, TicTacToeWebSocketClient client, Game game,
            JFrame frame) {
        client.send(serverMessage);
        JOptionPane.showMessageDialog(frame, message);
        game.setGameOver(true);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        client.close();
    }
}