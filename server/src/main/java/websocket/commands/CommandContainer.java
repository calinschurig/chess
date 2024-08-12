package websocket.commands;

public record CommandContainer(MakeMoveCommand makeMoveCommand,
                               UserGameCommand userGameCommand,
                               UserGameCommand.CommandType commandType) {
}
