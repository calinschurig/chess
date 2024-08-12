package handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.CommandContainer;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.MessageContainer;

import java.io.IOException;

public class WsHandlerHelper {
    static final Gson gson = new GsonBuilder().enableComplexMapKeySerialization().serializeNulls().create();
    public static void helperOnMessage(Session session, CommandContainer commandContainer, UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO) throws IOException {

        int gameId = commandContainer.gameId;
        switch (commandContainer.commandType) {
            case CONNECT -> {
                sendGame(session, gameDAO, gameId);
            }
            case MAKE_MOVE -> {
                //TODO
                session.getRemote().sendString("todo");
            }
            case LEAVE -> {
                //TODO
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
        if (gameData == null) {
            ErrorMessage errorMessage = new ErrorMessage("Invalid game ID");
            MessageContainer messageContainer = new MessageContainer(errorMessage);
            session.getRemote().sendString(gson.toJson(messageContainer));

        } else {
            LoadGameMessage loadGameMessage = new LoadGameMessage(gameData);
            MessageContainer messageContainer = new MessageContainer(loadGameMessage);
            session.getRemote().sendString(gson.toJson(messageContainer));
        }
    }

    private static void leaveGame(Session session, GameDAO gameDAO, int gameId) throws IOException {
        GameData gameData = gameDAO.get(gameId);
        GameData updatedGame; //TODO
    }

}
