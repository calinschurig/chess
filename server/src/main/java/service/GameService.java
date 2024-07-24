package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;

import java.util.Collection;
import java.util.Random;

public class GameService {
    public static Collection<GameData> listGames(GameDAO gameDAO) {
        return gameDAO.getAll();
    }

    // returns the gameID.
    public static int createGame(String gameName, GameDAO gameDAO) {
        return createGame(gameName, gameDAO, 50);
    }
    public static int createGame(String gameName, GameDAO gameDAO, int numTries) {
        Random rand = new Random();
        for (int i = 0; i < numTries; i++) {
            int possibleGameID = 0;
            while (possibleGameID == 0) possibleGameID = rand.nextInt();

            if (gameDAO.get(possibleGameID) == null ) {
                GameData newGame = new GameData(possibleGameID, null, null, gameName, new ChessGame());
                gameDAO.add(newGame);
                return possibleGameID;
            }
        }
        return 0;
    }

    public static void joinGame(ChessGame.TeamColor playerColor, int gameID, String userName, GameDAO gameDAO) throws DataAccessException {
        if ( null == gameDAO.get(gameID) ) throw new DataAccessException("Invalid game. No games with gameID: " + gameID + " exist. ");
        GameData game = gameDAO.get(gameID);
        String colorUserName = switch (playerColor) {
            case WHITE -> game.whiteUsername();
            case BLACK -> game.blackUsername();
        };
        if (!colorUserName.isEmpty()) throw new DataAccessException("Cannot join game as color: " + playerColor + " because it is already taken by user: " + colorUserName);
        GameData updatedGame = switch (playerColor) {
            case WHITE -> new GameData(game.gameID(), userName, game.blackUsername(), game.gameName(), game.game());
            case BLACK -> new GameData(game.gameID(), game.whiteUsername(), userName, game.gameName(), game.game());
        };
        gameDAO.update(updatedGame.getId(), updatedGame);
    }
}
