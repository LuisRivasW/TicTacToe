import java.util.Objects;

public class Player {
    private String name;
    private char symbol;

    public Player(String name, char symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    public static Player valueOf(String name) {
        if (name.equals("X")) {
            return new Player("Jugador 1", 'X');
        } else {
            return new Player("Jugador 2", 'O');
        }
    }

    public String getName() {
        return name;
    }

    public char getSymbol() {
        return symbol;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSymbol(char symbol) {
        this.symbol = symbol;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Player player = (Player) obj;
        return symbol == player.symbol && Objects.equals(name, player.name);
    }

}
