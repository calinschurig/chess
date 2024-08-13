package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import server.facade.WSClient;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

import javax.websocket.Session;

import static ui.ClientHelper.boardString;
import static ui.ClientHelper.checkArgs;

public class GameClient {


    public static String connect(String[] args, WSClient wsClient, AuthData auth, int gameId) {
        checkArgs(new Class[] {}, args);
//        UserGameCommand  command = new UserGameCommand();
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, auth.authToken(), gameId);
        try {
            wsClient.send(new Gson().toJson(command));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return "";
    }
    public static String redraw(String[] args, WSClient wsClient, AuthData auth, int gameId) {
        connect(args, wsClient, auth, gameId);
        return "REDRAWING BOARD";
    }
    public static String leave(String[] args, WSClient wsClient, AuthData auth, int gameId) {
        checkArgs(new Class[] {}, args);
//        UserGameCommand  command = new UserGameCommand();
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, auth.authToken(), gameId);
        try {
            wsClient.send(new Gson().toJson(command));
            wsClient.session.close();

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());

        }

        return "";
    }
    public static String resign(String[] args) {
        checkArgs(new Class[] {}, args);
        return "todo";
    }
    public static String moves(String[] args, GameData game, boolean isWhite, boolean isBlack) {
        checkArgs(new Class[] {String.class}, args);

        int row = 0;
        ChessPosition selected = stringToChessPosition(args[0]);
        ChessGame.TeamColor orientation = ChessGame.TeamColor.WHITE;
        if (isBlack && !isWhite) {
            orientation = ChessGame.TeamColor.BLACK;
        }
        return boardString(game, selected, orientation);
    }
    public static String move(String[] args, WSClient wsClient, AuthData auth, int gameId) {
        checkArgs(new Class[] {String.class, String.class}, args);
        ChessPosition start = stringToChessPosition(args[0]);
        ChessPosition end = stringToChessPosition(args[1]);
        ChessMove move = new ChessMove(start, end);
        MakeMoveCommand command = new MakeMoveCommand(auth.authToken(), gameId, move);
        try {
            wsClient.send(new Gson().toJson(command));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return "MOVING " + args[0] + " to " + args[1];
    }
    public static String helpGame(String[] args) {
        checkArgs(new Class[] {}, args);
        String output = """
                redraw - the board
                leave - the game
                resign - give up, lose the game
                moves <POSITION> - highlight possible moves. A3, F8, and D2 are all valid positions
                move <CURRENT_POSITION> <NEW_POSITION> - moves a piece from one position to the other
                help - with possible commands""";
        return output;
    }
    public static String drawGame() {

        return "todo";
    }

    public static Session openWebsocket() {

        return null;
    }

    private static ChessPosition stringToChessPosition(String input) {
        int col = 0;
        switch (input.trim().toLowerCase().toCharArray()[0]) {
            case 'a' -> col = 1;
            case 'b' -> col = 2;
            case 'c' -> col = 3;
            case 'd' -> col = 4;
            case 'e' -> col = 5;
            case 'f' -> col = 6;
            case 'g' -> col = 7;
            case 'h' -> col = 8;
            default -> throw new RuntimeException("Incorrectly formatted arguments");
        }
        int row = 0;
        switch (input.trim().toLowerCase().toCharArray()[1]) {
            case '1' -> row = 1;
            case '2' -> row = 2;
            case '3' -> row = 3;
            case '4' -> row = 4;
            case '5' -> row = 5;
            case '6' -> row = 6;
            case '7' -> row = 7;
            case '8' -> row = 8;
            default -> throw new RuntimeException("Incorrectly formatted arguments");
        }
        return new ChessPosition(row, col);

    }


}
