package server.facade;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.GameData;
import ui.ClientHelper;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.net.URI;
import java.util.Scanner;
import java.util.function.Supplier;

import static websocket.commands.UserGameCommand.CommandType.CONNECT;

public class WSClient extends Endpoint {
    public Session session;
    public GameData gameData;
    public Supplier<String> prompt;
    private Gson gson = new GsonBuilder().enableComplexMapKeySerialization().serializeNulls().create();
    public static void main(String[] args) throws Exception {
        var ws = new WSClient(() -> {return "prompt!";});
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter a message you want to echo");
        while (true) ws.send(scanner.nextLine());
    }

    public WSClient(Supplier<String> prompt) throws Exception {
        this("ws://localhost:8080/ws", prompt);
    }
    public WSClient(String uriString, Supplier<String> prompt) throws Exception {
        URI uri = new URI(uriString);
        this.prompt = prompt;
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);
//        container.

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                messageHandler(message);
            }
        });
    }

    public void send(String msg) throws Exception {
        this.session.getBasicRemote().sendText(msg);
    }

    public void messageHandler(String message) {
        ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
        boolean reprompt = false;
        switch (serverMessage.getServerMessageType()) {
            case ERROR -> {
                ErrorMessage errorMessage = gson.fromJson(message, ErrorMessage.class);
                System.out.println("\n" + errorMessage.getErrorMessage());
            }
            case LOAD_GAME -> {
                LoadGameMessage loadGameMessage = gson.fromJson(message, LoadGameMessage.class);
                reprompt = true;
                gameData = loadGameMessage.getGame();
                ChessGame.TeamColor orientation = ChessGame.TeamColor.WHITE;
                System.out.println("load_game draw board");
                String boardString = "";
                try {
                    boardString = ClientHelper.boardString(loadGameMessage.getGame(), null, ChessGame.TeamColor.WHITE);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println(boardString);
                System.out.println("completed drawing board");
            }
            case NOTIFICATION -> {
                NotificationMessage notificationMessage = gson.fromJson(message, NotificationMessage.class);
                System.out.println("\n" + notificationMessage.getMessage());
            }
            case null, default -> {
                System.out.println("error: bad message from server");
            }
        }
        if (reprompt) {
            System.out.print(prompt.get());
        }
//        System.out.println(message);
    }

    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

}
