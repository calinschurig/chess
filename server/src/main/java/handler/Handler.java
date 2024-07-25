package handler;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.UserData;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;

import java.util.Collection;

public class Handler {
    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public Handler(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    private record JoinGameRequest(String playerColor, int gameID) {}
    public Object joinGame(Request req, Response res) {
        JoinGameRequest gameRequest = new Gson().fromJson(req.body(), JoinGameRequest.class);
        String authToken = req.headers("authorization");
        String userName = authDAO.get(authToken).username();
        ChessGame.TeamColor color = null;
        if (gameRequest.playerColor().equals("WHITE")) {
            color = ChessGame.TeamColor.WHITE;
        }
        if (gameRequest.playerColor().equals("BLACK")) {
            color = ChessGame.TeamColor.BLACK;
        }

        try {
            int gameID = GameService.joinGame(color, gameRequest.gameID(), userName, gameDAO);
            return "{}";
        } catch (Exception e){
//            System.out.println(e);
            if (e.getMessage().contains("No games with gameID:")) {
                throw new BadRequestException("");
            }
            if (e.getMessage().contains("Cannot join game as color:")) {
                res.status(403);
                return new Gson().toJson(new Error("Error: already taken"));
            }
            if (e.getClass() == NullPointerException.class) {
                res.status(400);
                return new Gson().toJson(new Error("Error: bad request"));
            }
            if (e.getClass() == JsonSyntaxException.class) {
                res.status(400);
                throw new BadRequestException("");
            }
            throw new RuntimeException(e);
        }
    }

    private record CreateGameRequest(String gameName) {}
    private record CreateGameResult(int gameID) {}
    public Object createGame(Request req, Response res) throws DataAccessException {
        CreateGameRequest gameRequest = new Gson().fromJson(req.body(), CreateGameRequest.class);
        return new Gson().toJson(new CreateGameResult(GameService.createGame(gameRequest.gameName(), gameDAO)));
    }

    private record ListGamesRequest(Collection<model.GameData> games) {}
    public Object listGames(Request req, Response res) {
        return new Gson().toJson(new ListGamesRequest(GameService.listGames(gameDAO)));
    }

    public Object logout(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("authorization");
        try {
            UserService.logout(authToken, authDAO);
            return "{}";
        } catch (DataAccessException e) {
            if (e.getMessage().contains("invalid authToken")) {
                throw new InvalidAuthenticationException("");
            }
            throw e;
        }

    }

    private record LoginRequest(String username, String password) {}
    public Object login(Request req, Response res) throws DataAccessException {
        LoginRequest userInfo = new Gson().fromJson(req.body(), LoginRequest.class);
//        System.out.println(userInfo);
        try {
            return new Gson().toJson(UserService.login(userInfo.username(), userInfo.password(), userDAO, authDAO));
        } catch (DataAccessException e) {
            if (e.getMessage().contains("Incorrect password")
            || e.getMessage().contains("Error logging in")) {
                throw new InvalidAuthenticationException("");
            } else  {
                res.status(500);
                return new Gson().toJson( new Error(e.getMessage()) );
            }
        }
    }

    public Object register(Request req, Response res) throws DataAccessException {
        UserData userData = new Gson().fromJson(req.body(), UserData.class);
        if (null == userData) {
            System.out.println("userData is null");
            throw new BadRequestException("Could not parse UserData from req.body(): " + req.body());
        }
        try {
            return new Gson().toJson(UserService.register(userData, userDAO, authDAO));
        } catch (DataAccessException e) {
            if ( e.getMessage().contains("already taken") ) {
                res.status(403);
                return new Gson().toJson(new Error("Error: already taken"));
            }
            throw e;
        }
    }

    public Object clear(Request req, Response res) {
        userDAO.clear();
        gameDAO.clear();
        authDAO.clear();
        return "{}";
    }

    public void authenticate(Request req, Response res) {
        String authToken = req.headers("authorization");
        if (!UserService.isAuthorized(authToken, authDAO)) {
            throw new InvalidAuthenticationException("");
        }
    }

    public void authenticationError(Exception e, Request req, Response res) {
        res.status(401);
        res.body(new Gson().toJson(new Error("Error: unauthorized")));
    }
    public void serverError(Exception e, Request req, Response res) {
        res.status(500);
        res.body(new Gson().toJson(new Error(e.getMessage())));
    }
    public void badRequestError(Exception e, Request req, Response res) {
        res.status(400);
        res.body(new Gson().toJson(new Error("Error: bad request")));
    }

}
