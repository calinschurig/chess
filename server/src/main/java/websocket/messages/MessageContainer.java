package websocket.messages;

public class MessageContainer {
    public final ErrorMessage errorMessage;
    public final LoadGameMessage loadGameMessage;
    public final NotificationMessage notificationMessage;
    public final ServerMessage.ServerMessageType serverMessageType;
    public MessageContainer(ErrorMessage errorMessage) {
        this.errorMessage = errorMessage;
        loadGameMessage = null;
        notificationMessage = null;
        serverMessageType = ServerMessage.ServerMessageType.LOAD_GAME;
    }
    public MessageContainer(LoadGameMessage loadGameMessage) {
        this.loadGameMessage = loadGameMessage;
        errorMessage = null;
        notificationMessage = null;
        serverMessageType = ServerMessage.ServerMessageType.LOAD_GAME;
    }
    public MessageContainer(NotificationMessage notificationMessage) {
        this.notificationMessage = notificationMessage;
        errorMessage = null;
        loadGameMessage = null;
        serverMessageType = ServerMessage.ServerMessageType.NOTIFICATION;
    }
}
