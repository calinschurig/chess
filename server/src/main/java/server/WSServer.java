package server;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import spark.Spark;
import websocket.commands.CommandContainer;
import websocket.messages.LoadGameMessage;
import websocket.messages.MessageContainer;

@WebSocket
public class WSServer {
    private final Gson gson = new GsonBuilder().enableComplexMapKeySerialization().serializeNulls().create();

    public static void main(String[] args) throws Exception {
        Spark.port(8080);
        Spark.webSocket("/ws", WSServer.class);
        Spark.get("/ech/:msg", (req, res) -> "HTTP response: " + req.params(":msg"));
    }
    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        System.out.printf("Received: %s", message);
        session.getRemote().sendString("WebSocket response: " + message);

        CommandContainer commandContainer = gson.fromJson(message, CommandContainer.class);
        switch (commandContainer.commandType()) {
            case CONNECT -> {
                LoadGameMessage loadGameMessage = new LoadGameMessage(new ChessGame());
                session.getRemote().sendString(gson.toJson(loadGameMessage));
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
}
