import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URISyntaxException;

public class Main {
    private static Player currentPlayer;

    public static void main(String[] args) {
        Game game = new Game();
        Player player1 = new Player("Player 1", 'X');
        Player player2 = new Player("Player 2", 'O');
        currentPlayer = player1;

        URI serverUri;
        try {
            serverUri = new URI("ws://localhost:52301/ws");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        JFrame frame = new JFrame("Tic Tac Toe");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 300);

        TicTacToeWebSocketClient client = new TicTacToeWebSocketClient(serverUri, game, player1, player2, currentPlayer,
                frame);
        client.connect();

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 3));

        JLabel statusLabel = new JLabel(currentPlayer.getName() + " turno (simbolo " + currentPlayer.getSymbol() + ")");
        frame.add(statusLabel, BorderLayout.NORTH);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                final int finalI = i;
                final int finalJ = j;
                JButton button = new JButton();
                button.setFont(new Font("Arial", Font.BOLD, 40));

                button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (((JButton) e.getSource()).getText().equals("") && !game.checkWinner()
                                && !game.isBoardFull()) {
                            String message = "MOVE " + finalI + " " + finalJ;
                            client.send(message);
                            button.setText(String.valueOf(currentPlayer.getSymbol()));
                            game.makeMove(finalI, finalJ);
                            if (game.checkWinner()) {
                                JOptionPane.showMessageDialog(frame, currentPlayer.getName() + " ha ganado!");
                            } else if (game.isBoardFull()) {
                                JOptionPane.showMessageDialog(frame, "Es un empate!");
                            }
                            currentPlayer = (currentPlayer == player1) ? player2 : player1;
                            statusLabel.setText(
                                    currentPlayer.getName() + " turno (simbolo " + currentPlayer.getSymbol() + ")");
                        }
                    }
                });
                panel.add(button);
            }
        }

        frame.add(panel);
        frame.setVisible(true);
    }
}