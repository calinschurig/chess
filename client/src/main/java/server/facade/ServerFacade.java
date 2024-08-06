package server.facade;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.io.*;
import java.net.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.RejectedExecutionException;

public class ServerFacade {
    private URL url;
//    private AuthData auth;
    private static final Gson GSON = new GsonBuilder().enableComplexMapKeySerialization().serializeNulls().create();

    public ServerFacade(String url) throws MalformedURLException {
        this.url = new URL(url);
    }
    public ServerFacade() {
        try {
            this.url = new URL("http://localhost:8080/");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public void clear() throws IOException, RejectedExecutionException {
        HttpURLConnection con = resolve("/db");
        con.setRequestMethod("DELETE");
        con.connect();

        if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new RejectedExecutionException("Unable to delete database: " + getErrMessage(con));
        }
    }

    private record JoinGameRequest(String playerColor, int gameID) {}
    public void joinGame(int gameId, String playerColor, AuthData authToken) throws IOException, RejectedRequestException {
        joinGame(gameId, playerColor, authToken.authToken());
    }
    public void joinGame(int gameId, String playerColor, String authToken) throws IOException, RejectedRequestException {
        HttpURLConnection con = resolve("/game");
        con.setRequestMethod("PUT");
        con.setDoOutput(true);
        con.setRequestProperty("Authorization", authToken);
        con.connect();

        try (OutputStream os = con.getOutputStream()) {
            os.write(GSON.toJson(new JoinGameRequest(playerColor, gameId)).getBytes());
        }

        if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
            // good job!
        } else {
            throw new RejectedRequestException("Unable to join game: " + getErrMessage(con));
        }
    }

    private record ListGamesResponse(Collection<model.GameData> games) {}
    public Collection<GameData> listGames(AuthData authToken) throws IOException, RejectedRequestException {
        return listGames(authToken.authToken());
    }
    public Collection<GameData> listGames(String authToken) throws IOException, RejectedRequestException {
        HttpURLConnection con = resolve("/game");
        con.setRequestMethod("GET");
        con.setDoOutput(true);
        con.setRequestProperty("Authorization", authToken);
        con.connect();
        if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
//            System.out.println(new String(con.getInputStream().readAllBytes()));
            ListGamesResponse games = GSON.fromJson(new String(con.getInputStream().readAllBytes()), ListGamesResponse.class);
            return games.games();
        } else {
            throw new RejectedRequestException("Unable to list games: " + getErrMessage(con));
        }
    }
    public Map<Integer, GameData> gamesMap(AuthData authToken) throws IOException, RejectedRequestException {
        return gamesMap(authToken.authToken());
    }
    public Map<Integer, GameData> gamesMap(String authToken) throws IOException, RejectedRequestException {
        HashMap<Integer, GameData> gamesMap = new HashMap<>();
        for (GameData game: listGames(authToken)) {
            gamesMap.put(game.gameID(), game);
        }
        return gamesMap;
    }

    private record CreateGameRequest(String gameName) {}
    private record CreateGameResult(int gameID) {}
    public int createGame(String gameName, AuthData authToken) throws IOException, RejectedRequestException {
        return createGame(gameName, authToken.authToken());
    }
    public int createGame(String gameName, String authToken) throws IOException, RejectedRequestException {
        HttpURLConnection con = resolve("/game");
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Authorization", authToken);
        con.connect();
        CreateGameRequest req = new CreateGameRequest(gameName);
        try (OutputStream os = con.getOutputStream()) {
//            os.write(gson.toJson(req).getBytes());
            os.write(("{\"gameName\": \"" + gameName + "\"}").getBytes());
        }

        if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
            CreateGameResult game = parseJsonConnection(con.getInputStream(), CreateGameResult.class);
            return game.gameID();
        } else {
            throw new RejectedRequestException("Unable to create game. " + getErrMessage(con));
        }
    }

    public void logout(AuthData auth) throws IOException, RejectedRequestException {
        logout(auth.authToken());
    }
    public void logout(String authToken) throws IOException, RejectedRequestException {
        HttpURLConnection con = resolve("/session");
        con.setRequestMethod("DELETE");
        con.setDoOutput(true);
        con.setRequestProperty("Authorization", authToken);
        con.connect();

        if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new RejectedRequestException("Error: unable to logout: " + getErrMessage(con));
        }

    }

    public AuthData login(UserData user) throws IOException, RejectedRequestException {
        return login(user.username(), user.password());
    }
    public AuthData login(String username, String password) throws IOException, RejectedRequestException {
        HttpURLConnection con = resolve("/session");
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Accept", "test/html");
        con.setRequestProperty("Content-Type", "application/json");

        try (OutputStream os = con.getOutputStream()) {
            os.write(("{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}").getBytes());
        }

        con.connect();

        if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
            AuthData auth = parseJsonConnection(con.getInputStream(), AuthData.class);
            return auth;
        } else {
            throw new RejectedRequestException("Unable to log in: " + getErrMessage(con) );
        }

    }

    public AuthData register(String username, String password, String email) throws IOException, RejectedRequestException {
        return register(new UserData(username, password, email));
    }
    public AuthData register(UserData user) throws IOException, RejectedRequestException {
        HttpURLConnection con = resolve("/user");
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Accept", "test/html");
        con.setRequestProperty("Content-Type", "application/json");

        try (OutputStream os = con.getOutputStream()) {
            os.write(new Gson().toJson(user).getBytes());
        }
        con.connect();
        if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
            AuthData auth = parseJsonConnection(con.getInputStream(), AuthData.class);
            return auth;
        } else {
//            String err = con.getResponseMessage();
            String err = new String(con.getErrorStream().readAllBytes());
            throw new RejectedRequestException("Server rejected register: " + GSON.fromJson(err, Message.class).message());
        }
    }

    private HttpURLConnection resolve(String path) throws IOException {
        try {
            HttpURLConnection con = (HttpURLConnection) url.toURI().resolve(path).toURL().openConnection();
            return con;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T parseJsonConnection(InputStream is, Class<T> clazz) {
        String jsonString;
        try {
            jsonString = new String(is.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return GSON.fromJson(jsonString, clazz);
    }

    private record Message(String message) {};
    private String getErrMessage(HttpURLConnection con) throws IOException {
        String jsonStr = new String(con.getErrorStream().readAllBytes());
        return new Gson().fromJson(jsonStr, Message.class).message();
    }

}
