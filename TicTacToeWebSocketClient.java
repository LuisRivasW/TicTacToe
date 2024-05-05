import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import javax.swing.*;

import java.net.URI;

public class TicTacToeWebSocketClient extends WebSocketClient {
    private Game game;
    private Player player1;
    private Player player2;
    private Player currentPlayer;
    private JFrame frame;

    public TicTacToeWebSocketClient(URI serverUri, Game game, Player player1, Player player2, Player currentPlayer,
            JFrame frame) {
        super(serverUri);
        this.game = game;
        this.player1 = player1;
        this.player2 = player2;
        this.currentPlayer = currentPlayer;
        this.frame = frame;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Conexión abierta");
    }

    @Override
    public void onMessage(String message) {
        System.out.println("Mensaje recibido: " + message);

        String[] parts = message.split(" ");
        if (parts.length == 3 && parts[0].equals("MOVE")) {
            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);

            game.makeMove(x, y);
            if (game.checkWinner()) {
                JOptionPane.showMessageDialog(frame, currentPlayer.getName() + " ha ganado!");
            } else if (game.isBoardFull()) {
                JOptionPane.showMessageDialog(frame, "Es un empate!");
            }
            currentPlayer = (currentPlayer == player1) ? player2 : player1;
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Conexión cerrada: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        System.out.println("Error: " + ex.getMessage());
    }
}