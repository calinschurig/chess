import chess.*;
import dataaccess.*;
import handler.Handler;
import model.GameData;
import model.UserData;
import server.Server;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);
//        server.Server myServer = new Server(new Handler(new MemoryUserDAO(), new MemoryGameDAO(), new MemoryAuthDAO()));
        server.Server myServer = new Server();
        myServer.run(8080);
//
//        MemoryParentDAO<String, UserData> userDAO = new MemoryParentDAO<>();
//        GameDAO memDAO = new MemoryGameDAO();
//        SQLGameDAO gameDAO = new SQLGameDAO(new GameData(0, "", "", "", new ChessGame()));
//        GameData data = new GameData(2, "my-username", "my-username-again", "my-game", new ChessGame());
//        GameData data2 = new GameData(2, "another-username", "another-username-again", "my-game-again", new ChessGame());
//        GameData differentData = new GameData(5, "a-different-username", "a-different-username-again", "a-different-game", new ChessGame());
//        GameData bogusData = new GameData(666, "bogus-white", "bogus-black", "bogus-game", new ChessGame());
//
//        gameDAO.clear();
//        gameDAO.add(data);
//        gameDAO.remove(data.gameID());
//
//        memDAO.add(data);
//        memDAO.remove(data.gameID());
//        System.out.println(memDAO.get(data.gameID()));

    }
}