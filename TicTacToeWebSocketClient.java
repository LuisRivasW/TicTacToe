import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import javax.swing.*;

import java.net.URI;
import java.util.Arrays;

public class TicTacToeWebSocketClient extends WebSocketClient {
    private Game game;
    private Player[] players;
    private Player player;
    private JFrame frame;
    private JLabel statusLabel;
    private JButton[][] buttons;

    public TicTacToeWebSocketClient(URI serverUri, Game game, Player[] players, JFrame frame, JLabel statusLabel,
            JButton[][] buttons) {
        super(serverUri);
        this.game = game;
        this.players = players;
        this.frame = frame;
        this.statusLabel = statusLabel;
        this.buttons = buttons;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Conexión abierta");
        send("Uniendome al juego");
    }

    @Override
    public void onMessage(String message) {
        System.out.println("Mensaje recibido: " + message);

        if (message.startsWith("UPDATE ")) {
            handleUpdateMessage(message.substring(7));
        } else if (message.equals("PLAYER 1")) {
            player = players[0];
        } else if (message.equals("PLAYER 2")) {
            player = players[1];
        } else {
            handleOtherMessage(message);
        }
    }

    private void handleUpdateMessage(String gameState) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        char symbol = gameState.charAt(i * 3 + j);
                        buttons[i][j].setText(symbol == '0' ? "" : String.valueOf(symbol));
                    }
                }
            }
        });
    }

    private void handleOtherMessage(String message) {
        String[] parts = message.split(" ");
        if (parts.length == 4 && parts[0].equals("MOVE")) {
            handleMoveMessage(parts);
        } else if (parts.length == 4 && parts[0].equals("GAME") && parts[1].equals("OVER")) {
            handleGameOverMessage(parts);
        }
    }

    private void handleMoveMessage(String[] parts) {
        Player playerInMessage = Player.valueOf(parts[1]);
        int x = Integer.parseInt(parts[2]);
        int y = Integer.parseInt(parts[3]);

        if (!playerInMessage.equals(player) && x >= 0 && y >= 0 && x < 3 && y < 3) {
            game.makeMove(x, y, playerInMessage);
            buttons[x][y].setText(String.valueOf(playerInMessage.getSymbol()));
            if (game.isGameOver()) {
                send("GAME OVER");
            }
            if (player == players[0]) {
                player = players[1];
            } else {
                player = players[0];
            }
            statusLabel.setText(player.getName() + " turno (símbolo " + player.getSymbol() + ")");
        }
    }

    private void handleGameOverMessage(String[] parts) {
        if (parts[2].equals("DRAW")) {
            JOptionPane.showMessageDialog(frame, "Es un empate!");
        } else {
            String winner = String.join(" ", Arrays.copyOfRange(parts, 2, parts.length));
            JOptionPane.showMessageDialog(frame, winner);
        }
        game.setGameOver(true);
        statusLabel.setText("Juego terminado");
        close();
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Conexión cerrada: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        System.out.println("Error: " + ex.getMessage());
        JOptionPane.showMessageDialog(frame, "Se produjo un error: " + ex.getMessage());

    }
}