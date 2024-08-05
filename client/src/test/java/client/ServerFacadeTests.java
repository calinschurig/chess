package client;

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

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private static AuthData auth;
    private static UserData user = new UserData("username", "password", "email");
    private static GameData game;

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


//    @Test
//    public void sampleTest() {
//        Assertions.assertTrue(true);
//    }


    @Test
    @Order (1)
    @DisplayName("Valid register")
    public void registerTest() throws IOException, RejectedRequestException {
        auth = facade.register(user);
        System.out.println(auth);
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


}
