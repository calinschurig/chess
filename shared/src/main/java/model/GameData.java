package model;

import chess.ChessGame;

import java.lang.reflect.Field;

public record GameData(
        int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game
) implements Identifier<Integer> {
    public Integer getId() {
        return gameID;
    }
    public Field getIdField() {
        try {
            return this.getClass().getDeclaredField("gameID");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public int compareTo(Identifier<Integer> o) {
        return Integer.compare(this.getId(), o.getId());
    }
}
