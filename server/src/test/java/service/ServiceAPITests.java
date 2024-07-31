package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import service.GameService;
import service.UserService;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceAPITests {
    private static UserDAO userDAO;
    private static UserData testUser;
    private static GameDAO gameDAO;
    private static AuthDAO authDAO;
    private static AuthData testAuthData;
    private static int testGameID;
    private static String testGameName;

    @BeforeAll
    public static void init() {
        userDAO = new SQLUserDAO(new UserData("","",""));
        testUser = new UserData("username", "password", "email@example.com");
        gameDAO = new SQLGameDAO(new GameData(0, "", "", "", new ChessGame()));
        authDAO = new SQLAuthDAO(new AuthData("", ""));
        testGameName = "test game";
        userDAO.clear();
        gameDAO.clear();
        authDAO.clear();
    }


    @Test
    @Order(1)
    @DisplayName("Valid Register")
    public void successRegister() throws Exception {
        testAuthData = UserService.register(testUser, userDAO, authDAO);
        Assertions.assertNotNull(testAuthData);
        Assertions.assertNotNull(testAuthData.authToken());
        Assertions.assertNotNull(testAuthData.username());
    }

    @Test
    @Order(2)
    @DisplayName("Repeated Register")
    public void repeatedRegister() throws Exception {
        Assertions.assertThrows(DataAccessException.class, () -> UserService.register(testUser, userDAO, authDAO));
    }

    @Test
    @Order(3)
    @DisplayName("Logout")
    public void successLogout() throws  Exception {
        UserService.logout(testAuthData.authToken(), authDAO);
        Assertions.assertNull(authDAO.get(testAuthData.authToken()));
    }

    @Test
    @Order(4)
    @DisplayName("Repeated Logout")
    public void repeatedLogout() throws  Exception {
        Assertions.assertThrows(DataAccessException.class, () -> UserService.logout(testAuthData.authToken(), authDAO));
    }

    @Test
    @Order(5)
    @DisplayName("Login")
    public void login() throws Exception {
        testAuthData = UserService.login(testUser.username(), testUser.password(), userDAO, authDAO);
        Assertions.assertNotNull(testAuthData);
        Assertions.assertNotNull(testAuthData.authToken());
        Assertions.assertNotNull(testAuthData.username());
    }

    @Test
    @Order(6)
    @DisplayName("Bad password login")
    public void badPasswordLogin() {
        Assertions.assertThrows(DataAccessException.class, () -> UserService.login(testUser.username(), "Bad password!", userDAO, authDAO));
    }

    @Test
    @Order(7)
    @DisplayName("Valid authentication")
    public void authentication() {
        Assertions.assertTrue( UserService.isAuthorized(testAuthData.authToken(), authDAO));
    }

    @Test
    @Order(8)
    @DisplayName("Invalid authentication")
    public void invalidAuthentication() {
        Assertions.assertFalse( UserService.isAuthorized("Bogus authToken", authDAO) );
    }

    @Test
    @Order(9)
    @DisplayName("Get username")
    public void getUsername() throws DataAccessException {
        Assertions.assertEquals( testUser.username(), UserService.getUsername(testAuthData.authToken(), authDAO));
    }
    @Test
    @Order(10)
    @DisplayName("Invalid get username")
    public void invalidGetUsername() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, () -> UserService.getUsername("Bogus authToken", authDAO));
    }

    @Test
    @Order(11)
    @DisplayName("Create Game")
    public void createGame() throws DataAccessException {
        testGameID = GameService.createGame(testGameName, gameDAO);
        int anotherGameID = GameService.createGame(testGameName, gameDAO);
        Assertions.assertNotEquals(testGameID, anotherGameID);
        GameData game = gameDAO.get(testGameID);
        Assertions.assertNotNull(game);
        Assertions.assertNotNull(game.game());
        Assertions.assertNull(game.blackUsername());
        Assertions.assertNull(game.whiteUsername());
        Assertions.assertEquals(testGameName, game.gameName());
    }

    @Test
    @Order(12)
    @DisplayName("Invalid name Create Game")
    public void invalidCreateGame() {
        Assertions.assertThrows(DataAccessException.class, () -> GameService.createGame("", gameDAO));
    }

    @Test
    @Order(13)
    @DisplayName("Join game")
    public void joinGame() throws DataAccessException {
        GameService.joinGame(ChessGame.TeamColor.WHITE, testGameID, testUser.username(), gameDAO);
        GameData game = gameDAO.get(testGameID);
        Assertions.assertEquals(game.whiteUsername(), testUser.username());
        Assertions.assertNull(game.blackUsername());
    }

    @Test
    @Order(14)
    @DisplayName("Already taken team join game")
    public void alreadyTakenJoinGame() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, () -> GameService.joinGame(ChessGame.TeamColor.WHITE, testGameID, testUser.username(), gameDAO));
    }


}
