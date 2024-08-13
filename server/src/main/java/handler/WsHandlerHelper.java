package handler;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class WsHandlerHelper {
    static final Gson gson = new GsonBuilder().enableComplexMapKeySerialization().serializeNulls().create();
    public static void helperOnMessage(Session session, UserGameCommand command, MakeMoveCommand makeMoveCommand,
                                       GameDAO gameDAO, AuthDAO authDAO,
                                       HashMap<Integer, HashSet<Session>> gameObservers,
                                       HashMap<Session, HashSet<Integer>> sessionGames) throws IOException {

        int gameId = command.getGameID();
        switch (command.getCommandType()) {
            case CONNECT -> {
                sendGame(session, command, gameDAO, authDAO, gameId, gameObservers);
            }
            case MAKE_MOVE -> {
                makeMove(session, makeMoveCommand, gameDAO, authDAO, gameObservers);
            }
            case LEAVE -> {
                leaveGame(session, command, gameDAO, authDAO, gameObservers);

                session.getRemote().sendString("todo");
            }
            case RESIGN -> {
                //TODO
                session.getRemote().sendString("todo");
            }
            case null, default -> {
                //TODO
                session.getRemote().sendString("error? todo");
            }
        }
    }

    private static void sendGame(Session session, UserGameCommand command, GameDAO gameDAO,
                                 AuthDAO authDAO, int gameId,
                                 HashMap<Integer, HashSet<Session>> gameObservers) throws IOException {
        GameData gameData = gameDAO.get(gameId);
        session.getRemote();
        if (gameData == null) {
            ErrorMessage errorMessage = new ErrorMessage("Invalid game ID");
            session.getRemote().sendString(gson.toJson(errorMessage));

        } else {
            LoadGameMessage loadGameMessage = new LoadGameMessage(gameData);
            session.getRemote().sendString(gson.toJson(loadGameMessage));
            if (!gameObservers.get(gameId).contains(session) ) {
                String username = authDAO.get(command.getAuthToken()).username();
                String role = (Objects.equals(username, gameData.whiteUsername())) ? "WHITE"
                        : (Objects.equals(username, gameData.blackUsername())) ? "BLACK"
                        : "OBSERVER";
                sendNotificationToOthers(username + " has joined the game as " + role,
                        session, command, gameObservers);
            }
        }
    }

    private static void leaveGame(Session session, UserGameCommand command, GameDAO gameDAO, AuthDAO authDAO,
                                  HashMap<Integer, HashSet<Session>> gameObservers) throws IOException {
        GameData gameData = gameDAO.get(command.getGameID());
        String username = authDAO.get(command.getAuthToken()).username();
        if (gameData.whiteUsername() != null && gameData.whiteUsername().equalsIgnoreCase(username)) {
            gameData = gameData.changeWhiteUsername(null);
        }
        if (gameData.blackUsername() != null && gameData.blackUsername().equalsIgnoreCase(username)) {
            gameData = gameData.changeBlackUsername(null);
        }
        String msg = username + " has left the game. ";
        sendNotificationToOthers(msg, session, command, gameObservers);
        try {
            gameDAO.update(gameData.gameID(), gameData);
        } catch (DataAccessException e) {
            sendError("Invalid game id", session);
        }
        session.close();
        GameData updatedGame; //TODO
    }

    private static void makeMove(Session session, MakeMoveCommand command, GameDAO gameDAO, AuthDAO authDAO,
                                 HashMap<Integer, HashSet<Session>> gameObservers) {
        GameData gameData = gameDAO.get(command.getGameID());
        if (gameData.game().isOver) {
            sendError("Game is over", session);
        }
        try {
            ChessGame game = gameData.game();
            game.makeMove(command.getMove());
            if ( game.isInCheckmate(ChessGame.TeamColor.WHITE) ) {
                game.isOver = true;
                sendNotificationToAll(gameData.whiteUsername() + " is in checkmate", command, gameObservers);
            } else if( game.isInCheckmate(ChessGame.TeamColor.BLACK) ) {
                game.isOver = true;
                sendNotificationToAll(gameData.blackUsername() + " is in checkmate", command, gameObservers);
            }
            if ( game.isInStalemate(ChessGame.TeamColor.WHITE) || game.isInStalemate(ChessGame.TeamColor.BLACK) ) {
                game.isOver = true;
                sendNotificationToAll("Stalemate", command, gameObservers);
            }
            gameData = new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
            gameDAO.update(gameData.gameID(), gameData);
            LoadGameMessage loadGameMessage = new LoadGameMessage(gameData);
            session.getRemote().sendString(gson.toJson(loadGameMessage));
            sendToOthers(gson.toJson(loadGameMessage), session, command, gameObservers);
            String username = authDAO.get(command.getAuthToken()).username();
            sendNotificationToOthers(username + " made the move: " + command.getMove().toString(), session, command, gameObservers);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static void sendNotificationToAll(String message, UserGameCommand command,
                                              HashMap<Integer, HashSet<Session>> gameObservers) {
        NotificationMessage notificationMessage = new NotificationMessage(message);
        message = gson.toJson(notificationMessage);
        sendAll(message, command, gameObservers);
    }
    private static void sendNotificationToOthers(String message, Session session, UserGameCommand command,
                                         HashMap<Integer, HashSet<Session>> gameObservers) {
        NotificationMessage notificationMessage = new NotificationMessage(message);
        message = gson.toJson(notificationMessage);
        sendToOthers(message, session, command, gameObservers);
    }
    private static void sendToOthers (String message, Session session, UserGameCommand command,
        HashMap<Integer, HashSet<Session>> gameObservers) {
        for (Session otherSession : gameObservers.get(command.getGameID())) {
            if (otherSession.equals(session)) {
                continue;
            }
            try {
                otherSession.getRemote().sendString(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private static void sendAll(String message, UserGameCommand command,
                                HashMap<Integer,HashSet<Session>> gameObservers) {
        for (Session otherSession : gameObservers.get(command.getGameID())) {
            NotificationMessage notificationMessage = new NotificationMessage(message);
            try {
                otherSession.getRemote().sendString(gson.toJson(notificationMessage));
                otherSession.getRemote().sendString(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void sendError(String message, Session session) {
        ErrorMessage errorMessage = new ErrorMessage(message);
        try {
            session.getRemote().sendString(gson.toJson(errorMessage));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
