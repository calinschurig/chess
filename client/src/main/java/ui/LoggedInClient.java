package ui;

import chess.ChessGame;
import chess.EscapeSequences;
import model.AuthData;
import server.facade.RejectedRequestException;
import server.facade.ServerFacade;

import java.io.IOException;
import java.util.HashMap;

public class LoggedInClient {

    public static String createGame(String[] args, ServerFacade facade, AuthData auth) {
        ClientHelper.checkArgs(new Class[] {String.class}, args);
        try {
            facade.createGame(args[0], auth.authToken());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } catch (RejectedRequestException e) {
            throw new RuntimeException(e.getMessage());
        }
        return "Created game: " + args[0];
    }

    public static String joinGame(String[] args, ServerFacade facade, AuthData auth, HashMap<Integer, Integer> gameIndextoId) {
        ClientHelper.checkArgs(new Class[] {int.class, String.class}, args);
        if (gameIndextoId == null) {
            return "Please list games before attempting to join. ";
        }
        int gameNum = Integer.parseInt(args[0]);
        if (!gameIndextoId.containsKey(gameNum)) {
            return "Invalid argument: please use numbers listed by the LIST command";
        }
        if (!args[1].equalsIgnoreCase("BLACK") && !args[1].equalsIgnoreCase("WHITE") ) {
//            System.out.println("args[1]: " + args[1]);
            return "Invalid argument: color must be either WHITE or BLACK";
        }
        int gameId = gameIndextoId.get(gameNum);
        try {
            facade.joinGame(gameId, args[1].toUpperCase(), auth.authToken());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } catch (RejectedRequestException e) {
            throw new RuntimeException(e.getMessage());
        }
        return "Joined game " + gameNum + " to " + args[1].toUpperCase();
    }

    public static String observeGame(String[] args, ServerFacade facade, AuthData auth, HashMap<Integer, Integer> gameIndextoId) {
        ClientHelper.checkArgs(new Class[] {int.class}, args);
        int gameNum = Integer.parseInt(args[0]);
        if (gameIndextoId == null) {
            return "Please list games before attempting to observe. ";
        }
        if (!gameIndextoId.containsKey(gameNum)) {
            return "Invalid argument: please use numbers listed by the LIST command";
        }
        int gameId = gameIndextoId.get(gameNum);
        try {
            String out = ClientHelper.boardString(facade.gamesMap(auth).get(gameId), ChessGame.TeamColor.WHITE,
                    EscapeSequences.SET_BG_COLOR_DARK_GREEN, EscapeSequences.SET_BG_COLOR_BLUE,
                    EscapeSequences.SET_BG_COLOR_LIGHT_GREY)
                    + "\n"
                    + ClientHelper.boardString(facade.gamesMap(auth).get(gameId), ChessGame.TeamColor.BLACK,
                    EscapeSequences.SET_BG_COLOR_DARK_GREEN, EscapeSequences.SET_BG_COLOR_BLUE,
                    EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
            return out;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } catch (RejectedRequestException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static String helpPost(String[] args) {
        ClientHelper.checkArgs(new Class[] {}, args);
        String output = """
                create <NAME> - a game
                list - games
                join <ID> [WHITE|BLACK] - a game
                observe <ID> - view a game
                logout - when you are done
                quit - playing chess
                help - with possible commands""";
        return output;
    }
}
