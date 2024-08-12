package server;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import spark.Spark;
import websocket.commands.CommandContainer;
import websocket.messages.LoadGameMessage;
import websocket.messages.MessageContainer;


public class WSServer {

    public static void main(String[] args) throws Exception {
        Spark.port(8080);
        Spark.webSocket("/ws", WSServer.class);
        Spark.get("/ech/:msg", (req, res) -> "HTTP response: " + req.params(":msg"));
    }


}
