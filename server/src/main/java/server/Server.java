package server;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.*;
import handler.BadRequestException;
import handler.InvalidAuthenticationException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import spark.*;
import handler.Handler;

import javax.xml.crypto.Data;

public class Server {
    private final Handler handler;
    public Server() {
        this.handler = new Handler(
                new SQLUserDAO(new UserData("","","")),
                new SQLGameDAO(new GameData(0, "", "", "", new ChessGame())),
                new SQLAuthDAO(new AuthData("", ""))
        );
    }
    public Server(Handler handler) {
        this.handler = handler;
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");
        Spark.webSocket("/ws", handler);
        Spark.get("/echo/:msg", (req, res) -> "HTTP response: " + req.params(":msg"));

        Spark.before("/game", handler::authenticate);
//        Spark.before("/ws", handler::authenticate);

        Spark.delete("/db", handler::clear);
        Spark.post("/user", handler::register);
        Spark.post("/session", handler::login);
        Spark.delete("/session", handler::logout);
        Spark.get("/game", handler::listGames);
        Spark.post("/game", handler::createGame);
        Spark.put("/game", handler::joinGame);


        Spark.get("/user", (req, res) -> {
            throw new DataAccessException("Testing exception handling");
        });

        Spark.exception(InvalidAuthenticationException.class, handler::authenticationError);
        Spark.exception(DataAccessException.class, handler::serverError);
        Spark.exception(JsonSyntaxException.class, handler::badRequestError);
        Spark.exception(NullPointerException.class, handler::badRequestError);
        Spark.exception(BadRequestException.class, handler::badRequestError);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
