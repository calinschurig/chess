package websocket.messages;

public record MessageContainer(ErrorMessage errorMessage,
                               LoadGameMessage loadGameMessage,
                               NotificationMessage notificationMessage,
                               ServerMessage.ServerMessageType serverMessageType) {
}
