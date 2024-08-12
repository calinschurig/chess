package websocket.commands;

import chess.ChessMove;

public class CommandContainer {
    public final MakeMoveCommand makeMoveCommand;
    public final UserGameCommand userGameCommand;
    public final UserGameCommand.CommandType commandType;
    public final int gameId;
    public CommandContainer(ChessMove move, String authToken, int gameId) {
        this.makeMoveCommand = new MakeMoveCommand(authToken, gameId, move);
        this.userGameCommand = null;
        this.commandType = UserGameCommand.CommandType.MAKE_MOVE;
        this.gameId = gameId;
    }
    public CommandContainer(UserGameCommand.CommandType commandType, String authToken, int gameId) {
        if (commandType == UserGameCommand.CommandType.MAKE_MOVE) {
            throw new RuntimeException("Invalid command type: MAKE_MOVE must be constructed with a ChessMove");
        }
        this.makeMoveCommand = null;
        this.userGameCommand = new UserGameCommand(commandType, authToken, gameId);
        this.gameId = gameId;
        this.commandType = commandType;
    }
    public UserGameCommand.CommandType commandType() {
        return commandType;
    }
}
