package handler;

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
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class WsHandlerHelper {
    static final Gson gson = new GsonBuilder().enableComplexMapKeySerialization().serializeNulls().create();
    public static void helperOnMessage(Session session, UserGameCommand command,
                                       GameDAO gameDAO, AuthDAO authDAO,
                                       HashMap<Integer, HashSet<Session>> gameObservers,
                                       HashMap<Session, HashSet<Integer>> sessionGames) throws IOException {

        int gameId = command.getGameID();
        switch (command.getCommandType()) {
            case CONNECT -> {
                sendGame(session, gameDAO, gameId);
            }
            case MAKE_MOVE -> {

                session.getRemote().sendString("todo");
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

    private static void sendGame(Session session, GameDAO gameDAO, int gameId) throws IOException {
        GameData gameData = gameDAO.get(gameId);
        session.getRemote();
        if (gameData == null) {
            ErrorMessage errorMessage = new ErrorMessage("Invalid game ID");
            session.getRemote().sendString(gson.toJson(errorMessage));

        } else {
            LoadGameMessage loadGameMessage = new LoadGameMessage(gameData);
            session.getRemote().sendString(gson.toJson(loadGameMessage));
        }
    }

    private static void leaveGame(Session session, UserGameCommand command, GameDAO gameDAO, AuthDAO authDAO,
                                  HashMap<Integer, HashSet<Session>> gameObservers) throws IOException {
        GameData gameData = gameDAO.get(command.getGameID());
        String username = authDAO.get(command.getAuthToken()).username();
        if (gameData.whiteUsername().equalsIgnoreCase(username)) {
            gameData = gameData.changeWhiteUsername(null);
        }
        if (gameData.blackUsername().equalsIgnoreCase(username)) {
            gameData = gameData.changeBlackUsername(null);
        }
        String msg = username + " has left the game. ";
        sendNotification(msg, session, command, gameObservers);
        try {
            gameDAO.update(gameData.gameID(), gameData);
        } catch (DataAccessException e) {
            sendError("Invalid game id", session);
        }
        session.close();
        GameData updatedGame; //TODO
    }

    private static void sendNotification(String message, Session session, UserGameCommand command,
                                         HashMap<Integer, HashSet<Session>> gameObservers) throws IOException {
        for (Session otherSession : gameObservers.get(command.getGameID())) {
            if (otherSession == session) {
                continue;
            }
            NotificationMessage notificationMessage = new NotificationMessage(message);
            otherSession.getRemote().sendString(gson.toJson(notificationMessage));
        }
    }

    public static void sendError(String message, Session session) throws IOException {
        ErrorMessage errorMessage = new ErrorMessage(message);
        session.getRemote().sendString(gson.toJson(errorMessage));
    }

}
