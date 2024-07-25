package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import handler.BadRequestException;
import handler.InvalidAuthenticationException;
import spark.*;
import handler.Handler;

import javax.xml.crypto.Data;

public class Server {
    private final Handler handler;
    public Server() {
        this.handler = new Handler(new MemoryUserDAO(), new MemoryGameDAO(), new MemoryAuthDAO());
    }
    public Server(Handler handler) {
        this.handler = handler;
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.before("/game", handler::authenticate);

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
