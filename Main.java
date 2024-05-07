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

        JLabel statusLabel = new JLabel("Esperando al otro jugador...");

        TicTacToeWebSocketClient client = new TicTacToeWebSocketClient(serverUri, frame, statusLabel, buttons);

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
                        if (button.getText().equals("")) {
                            handleMove(finalI, finalJ, button, client, buttons, frame, statusLabel);
                        }
                    }
                });
                panel.add(button);
            }
        }

        frame.add(panel);
        frame.setVisible(true);
        client.connect();

        String[] options = { "Unirse a una sala de juego", "Crear una nueva sala de juego" };
        int response = JOptionPane.showOptionDialog(null, "¿Qué quieres hacer?", "Inicio del juego",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        if (response == 0) {
            String gameID = JOptionPane
                    .showInputDialog("Introduce el ID de la sala de juego a la que te quieres unir:");
            client.joinGame(gameID);
        } else if (response == 1) {
            String gameID = JOptionPane
                    .showInputDialog("Introduce el ID de la nueva sala de juego que quieres crear:");
            client.createGame(gameID);
        }
    }

    private void handleMove(int x, int y, JButton button, TicTacToeWebSocketClient client, JButton[][] buttons,
            JFrame frame, JLabel statusLabel) {
        for (JButton[] row : buttons) {
            for (JButton b : row) {
                b.setEnabled(false);
            }
        }

        String message = "MOVE " + x + " " + y;
        client.send(message);
    }
}