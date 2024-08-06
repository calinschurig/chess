package client;

import chess.ChessGame;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import handler.Handler;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import server.facade.RejectedRequestException;
import server.facade.ServerFacade;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private static AuthData auth;
    private static UserData user = new UserData("username", "password", "email");
    private static GameData game;
    private static int gameId;
    private static String nameOfGame = "New-game";

    @BeforeAll
    public static void init() {
        server = new Server(new Handler(new MemoryUserDAO(), new MemoryGameDAO(), new MemoryAuthDAO()));
        var port = server.run(0);
        try {
            facade = new ServerFacade("http://localhost:" + port);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    @Order (1)
    @DisplayName("Valid register")
    public void registerTest() throws IOException, RejectedRequestException {
        auth = facade.register(user);
        Assertions.assertNotNull(auth);
        Assertions.assertEquals(auth.username(), user.username());
        Assertions.assertNotNull(auth.authToken());
    }

    @Test
    @Order (2)
    @DisplayName("Invalid register")
    public void invalidRegisterTest() throws IOException {
        Assertions.assertThrows(RejectedRequestException.class, () -> facade.register(user));
    }

    @Test
    @Order (3)
    @DisplayName("Valid login")
    public void loginTest() throws IOException, RejectedRequestException {
        AuthData auth2 = facade.login(user.username(), user.password());
        Assertions.assertNotNull(auth2);
        Assertions.assertEquals(auth2.username(), user.username());
        Assertions.assertNotNull(auth2.authToken());
        Assertions.assertNotEquals(auth2.authToken(), auth.authToken());
    }

    @Test
    @Order (4)
    @DisplayName("Invalid login")
    public void invalidLoginTest() throws IOException, RejectedRequestException {
        Assertions.assertThrows(RejectedRequestException.class, () -> facade.login(user.username(), "bad password"));
        Assertions.assertThrows(RejectedRequestException.class, () -> facade.login("bad username", "bad password"));
    }

    @Test
    @Order(5)
    @DisplayName("Valid logout")
    public void logoutTest() throws IOException, RejectedRequestException {
        Assertions.assertDoesNotThrow(() -> facade.logout(auth));
    }

    @Test
    @Order(6)
    @DisplayName("Invalid logout")
    public void invalidLogoutTest() throws IOException, RejectedRequestException {
        Assertions.assertThrows(RejectedRequestException.class, () -> facade.logout(auth));
        Assertions.assertThrows(RejectedRequestException.class, () -> facade.logout("bogus-authToken"));
    }

    @Test
    @Order(7)
    @DisplayName("Valid create game")
    public void validCreateGame() throws IOException, RejectedRequestException {
        auth = facade.login(user);
        Assertions.assertDoesNotThrow(() -> gameId = facade.createGame("New-game", auth));
        Assertions.assertNotNull(facade.gamesMap(auth).get(gameId));
    }

    @Test
    @Order(8)
    @DisplayName("Invalid create game")
    public void invalidCreateGame() throws IOException, RejectedRequestException {
        Assertions.assertThrows(RejectedRequestException.class, () -> facade.createGame("", auth));
        Assertions.assertThrows(RejectedRequestException.class, () -> facade.createGame(nameOfGame, "bad-auth"));
    }

    @Test
    @Order(8)
    @DisplayName("Valid list games")
    public void validListGames() throws IOException, RejectedRequestException {
        GameData[] exampleGameDataList = {new GameData(gameId, null, null, nameOfGame, new ChessGame())};
        Assertions.assertArrayEquals(facade.listGames(auth).toArray(), exampleGameDataList);
    }

    @Test
    @Order(9)
    @DisplayName("Invalid list games")
    public void invalidListGames() throws IOException, RejectedRequestException {
        Assertions.assertThrows(RejectedRequestException.class, () -> facade.listGames("bad-auth"));
    }

    @Test
    @Order(10)
    @DisplayName("Valid games map")
    public void validGamesMap() throws IOException, RejectedRequestException {
        HashMap<Integer, GameData> gamesMap = new HashMap<>();
        gamesMap.put(gameId, new GameData(gameId, null, null, nameOfGame, new ChessGame()));
        Assertions.assertEquals(gamesMap, facade.gamesMap(auth));
    }

    @Test
    @Order(11)
    @DisplayName("Invalid games map")
    public void invalidGamesMap() throws IOException, RejectedRequestException {
        Assertions.assertThrows(RejectedRequestException.class, () -> facade.gamesMap("bad-auth"));
    }

    @Test
    @Order(12)
    @DisplayName("Valid join game")
    public void validJoinGame() throws IOException, RejectedRequestException {
        Assertions.assertDoesNotThrow(() -> facade.joinGame(gameId, "WHITE", auth));
    }

    @Test
    @Order(13)
    @DisplayName("Invalid join game")
    public void invalidJoinGame() throws IOException, RejectedRequestException {
        Assertions.assertThrows(RejectedRequestException.class, () -> facade.joinGame(gameId, "WHITE", auth));
        Assertions.assertThrows(RejectedRequestException.class, () -> facade.joinGame(0, "BLACK", auth));
        Assertions.assertThrows(RejectedRequestException.class, () -> facade.joinGame(0, "BLACK", "bad-auth"));
    }

    @Test
    @Order(14)
    @DisplayName("Valid clear")
    public void validClear() throws IOException, RejectedRequestException {
        Assertions.assertDoesNotThrow(() -> facade.clear());
        auth = facade.register(user);
        Assertions.assertArrayEquals(facade.listGames(auth).toArray(), new GameData[0]);
    }

    @Test
    @Order(15)
    @DisplayName("Invalid clear?")
    public void invalidClear() throws IOException, RejectedRequestException {
//        Assertions.assertThrows(RejectedRequestException.class, () -> facade.clear());
        facade.clear();
        Assertions.assertThrows(RejectedRequestException.class, () -> facade.login(user));
    }





}
