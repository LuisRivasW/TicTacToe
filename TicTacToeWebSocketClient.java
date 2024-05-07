import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import javax.swing.*;

import java.net.URI;
import java.util.Arrays;

public class TicTacToeWebSocketClient extends WebSocketClient {
    private JFrame frame;
    private JLabel statusLabel;
    private JButton[][] buttons;
    private boolean isPlayer1;

    public TicTacToeWebSocketClient(URI serverUri, JFrame frame, JLabel statusLabel, JButton[][] buttons) {
        super(serverUri);
        this.frame = frame;
        this.statusLabel = statusLabel;
        this.buttons = buttons;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Conexión abierta");
    }

    @Override
    public void onMessage(String message) {
        System.out.println("Mensaje recibido: " + message);

        try {
            if (message.startsWith("UPDATE ")) {
                handleUpdateMessage(message.substring(7));
            } else if (message.startsWith("JOINED GAME ")) {
                handleJoinedGameMessage(message.substring(12));
            } else if (message.startsWith("CREATED GAME ")) {
                handleCreatedGameMessage(message.substring(13));
            } else {
                handleOtherMessage(message);
            }
        } catch (Exception e) {
            System.out.println("Error al procesar el mensaje: " + e.getMessage());
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

    private void handleJoinedGameMessage(String gameID) {
        System.out.println("Te has unido a la sala de juego con ID: " + gameID);
        isPlayer1 = false;
    }

    private void handleCreatedGameMessage(String gameID) {
        System.out.println("Has creado la sala de juego con ID: " + gameID);
        isPlayer1 = true;
    }

    private void handleOtherMessage(String message) {
        String[] parts = message.split(" ");
        if (parts.length == 4 && parts[0].equals("MOVE")) {
            try {
                handleMoveMessage(parts);
            } catch (NumberFormatException e) {
                System.out.println("Error al procesar el mensaje MOVE: " + e.getMessage());
            }
        } else if (parts.length == 4 && parts[0].equals("GAME") && parts[1].equals("OVER")) {
            handleGameOverMessage(parts);
        } else {
            System.out.println("Mensaje desconocido: " + message);
        }
    }

    private void handleMoveMessage(String[] parts) {
        int x = Integer.parseInt(parts[2]);
        int y = Integer.parseInt(parts[3]);
        char symbol = parts[1].charAt(0);

        if (x < 0 || x > 2 || y < 0 || y > 2 || buttons[x][y].getText().length() > 0) {
            System.out.println("Movimiento inválido: " + Arrays.toString(parts));
            return;
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                buttons[x][y].setText(String.valueOf(symbol));
                char nextPlayer = symbol == 'X' ? 'O' : 'X';
                statusLabel.setText("Turno de " + nextPlayer);
                if ((nextPlayer == 'X' && isPlayer1) || (nextPlayer == 'O' && !isPlayer1)) {
                    for (JButton[] row : buttons) {
                        for (JButton button : row) {
                            button.setEnabled(true);
                        }
                    }
                }
            }
        });
    }

    private void handleGameOverMessage(String[] parts) {
        String winner = parts[2];

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (winner.equals("DRAW")) {
                    statusLabel.setText("El juego termino en empate");
                } else {
                    String reason = parts[3];
                    statusLabel.setText("El juego ha terminado. Ganador: " + winner + ", Razon: " + reason);
                }
                for (JButton[] row : buttons) {
                    for (JButton button : row) {
                        button.setEnabled(false);
                    }
                }
            }
        });
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

    public void joinGame(String gameID) {
        send("JOIN GAME " + gameID);
    }

    public void createGame(String gameID) {
        send("CREATE GAME " + gameID);
    }
}