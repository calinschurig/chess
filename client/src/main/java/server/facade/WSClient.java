package server.facade;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import websocket.commands.CommandContainer;

import javax.websocket.*;
import java.net.URI;
import java.util.Scanner;

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
        CommandContainer command = gson.fromJson(message, CommandContainer.class);
        switch (command.commandType()) {
            case CONNECT -> {

            }
            case MAKE_MOVE -> {

            }
            case LEAVE -> {

            }
            case RESIGN -> {

            }
            case null, default -> {

            }
        }
        System.out.println(message);
    }

    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

}
