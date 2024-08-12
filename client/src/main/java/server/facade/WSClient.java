package server.facade;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.net.URI;
import java.util.Scanner;

import static websocket.commands.UserGameCommand.CommandType.CONNECT;

public class WSClient extends Endpoint {
    public Session session;
    private Gson gson = new GsonBuilder().enableComplexMapKeySerialization().serializeNulls().create();
    public static void main(String[] args) throws Exception {
        var ws = new WSClient();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter a message you want to echo");
        while (true) ws.send(scanner.nextLine());
    }

    public WSClient() throws Exception {
        this("ws://localhost:8080/ws");
    }
    public WSClient(String uriString) throws Exception {
        URI uri = new URI(uriString);
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
        switch (serverMessage.getServerMessageType()) {
            case ERROR -> {
                ErrorMessage errorMessage = gson.fromJson(message, ErrorMessage.class);
                System.out.println(errorMessage.getErrorMessage());
            }
            case LOAD_GAME -> {
                LoadGameMessage loadGameMessage = gson.fromJson(message, LoadGameMessage.class);
                System.out.println("load_game todo");
            }
            case NOTIFICATION -> {
                NotificationMessage notificationMessage = gson.fromJson(message, NotificationMessage.class);
                System.out.println(notificationMessage.getMessage());
            }
            case null, default -> {
                System.out.println("error: bad message from server");
            }
        }
//        System.out.println(message);
    }

    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

}
