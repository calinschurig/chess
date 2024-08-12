package ui;

import model.AuthData;
import server.facade.RejectedRequestException;
import server.facade.ServerFacade;

import java.io.IOException;

public class LoggedInClient {

    public static String createGame(String[] args, ServerFacade facade, AuthData auth) {
        ClientHelper.checkArgs(new Class[] {String.class}, args);
        try {
            facade.createGame(args[0], auth.authToken());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } catch (RejectedRequestException e) {
            throw new RuntimeException(e.getMessage());
        }
        return "Created game: " + args[0];
    }

    public static String helpPost(String[] args) {
        ClientHelper.checkArgs(new Class[] {}, args);
        String output = """
                create <NAME> - a game
                list - games
                join <ID> [WHITE|BLACK] - a game
                observe <ID> - view a game
                logout - when you are done
                quit - playing chess
                help - with possible commands""";
        return output;
    }
}
