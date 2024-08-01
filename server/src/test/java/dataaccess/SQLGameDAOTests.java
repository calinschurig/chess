package dataaccess;

import chess.ChessGame;
import model.GameData;
import mytestutilities.SQLParentDAOTests;


public class SQLGameDAOTests extends SQLParentDAOTests<Integer, GameData> {
    static {
        memoryDAO = new MemoryGameDAO();
        sqlDAO = new SQLGameDAO(new GameData(0,"", "", "", new ChessGame()));
        data = new GameData(2, "my-username", "my-username-again", "my-game", new ChessGame());
        data2 = new GameData(2, "another-username", "another-username-again", "my-game-again", new ChessGame());
        differentData = new GameData(5, "a-different-username", "a-different-username-again", "a-different-game", new ChessGame());
        bogusData = new GameData(666, "bogus-white", "bogus-black", "bogus-game", new ChessGame());
    }
}
