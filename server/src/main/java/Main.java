import chess.*;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryParentDAO;
import dataaccess.MemoryUserDAO;
import handler.Handler;
import model.UserData;
import server.Server;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);
        server.Server myServer = new Server(new Handler(new MemoryUserDAO(), new MemoryGameDAO(), new MemoryAuthDAO()));
        myServer.run(8080);

        MemoryParentDAO<String, UserData> userDAO = new MemoryParentDAO<>();

    }
}