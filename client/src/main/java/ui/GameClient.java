package ui;

import com.google.gson.Gson;
import model.AuthData;
import server.facade.WSClient;
import websocket.commands.UserGameCommand;

import javax.websocket.Session;

import static ui.ClientHelper.checkArgs;

public class GameClient {


    public static String connect(String[] args, WSClient wsClient, AuthData auth, int gameId) {
        checkArgs(new Class[] {}, args);
//        UserGameCommand  command = new UserGameCommand();
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, auth.authToken(), gameId);
        try {
            wsClient.send(new Gson().toJson(command));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return "";
    }
    public static String leave(String[] args, WSClient wsClient, AuthData auth, int gameId) {
        checkArgs(new Class[] {}, args);
//        UserGameCommand  command = new UserGameCommand();
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, auth.authToken(), gameId);
        try {
            wsClient.send(new Gson().toJson(command));
            wsClient.session.close();

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());

        }

        return "";
    }
    public static String resign(String[] args) {
        checkArgs(new Class[] {}, args);
        return "todo";
    }
    public static String moves(String[] args) {
        checkArgs(new Class[] {}, args);
        return "todo";
    }
    public static String move(String[] args) {
        checkArgs(new Class[] {}, args);
        return "todo";
    }
    public static String helpGame(String[] args) {
        checkArgs(new Class[] {}, args);
        String output = """
                redraw - the board
                leave - the game
                resign - give up, lose the game
                moves <POSITION> - highlight possible moves. A3, F8, and D2 are all valid positions
                move <CURRENT_POSITION> <NEW_POSITION> - moves a piece from one position to the other
                help - with possible commands""";
        return output;
    }
    public static String drawGame() {

        return "todo";
    }

    public static Session openWebsocket() {

        return null;
    }


}
