import chess.*;
import model.AuthData;
import server.facade.ServerFacade;
import ui.ChessClient;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        ChessClient client = new ChessClient();
        if (args.length > 0) {
            client.run(args[0]);
        } else {
            client.run();
        }
    }
}