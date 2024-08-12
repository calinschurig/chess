package ui;

import chess.ChessGame;
import chess.ChessPosition;
import chess.EscapeSequences;
import model.GameData;

import java.util.ArrayList;
import java.util.Arrays;

public class ClientHelper {


    public static void checkArgs(Class[] argTypes, String[] args) {
        if (argTypes == null && args == null) {
            return;
        } else if (argTypes == null || args == null) {
            throw new RuntimeException("Invalid arguments");
        }
        if (argTypes.length != args.length) {
//            System.out.println("Incorrect number of arguments. Was: " + args.length + " but expected: " + argTypes.length);
            throw new RuntimeException("Incorrect number of arguments. Was: " + args.length + " but expected: " + argTypes.length);
        }
        try {
            for (int i = 0; i < args.length; i++) {
                if (argTypes[i] == String.class) {
                    if (args[i].matches("^[a-zA-Z]\\w*")) {
                        continue;
                    } else {
                        throw new RuntimeException("Invalid argument");
                    }
                } else if (argTypes[i] == int.class) {
                    Integer.parseInt(args[i]);
                }
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException("Incorrectly formatted arguments. ");
        }
    }

    public static String boardRowString(int row, GameData game, ChessGame.TeamColor orientation,
                                        String darkBoardColor, String lightBoardColor,
                                        String outlineColor) {
        StringBuilder sb = new StringBuilder();

        final int start = orientation == ChessGame.TeamColor.WHITE? 0 : 9;
        final int end = orientation == ChessGame.TeamColor.WHITE? 10 : -1;
        final int dir = orientation == ChessGame.TeamColor.WHITE? 1 : -1;
        for (int i = start; i != end; i+=dir) {
            if (i == 0 | i == 9) {
                sb.append(outlineColor).append("\u2005\u2004\u2005" + row + "\u2005\u2004\u2005");
            } else if ( (row + i) % 2 == 1) {
                sb.append(lightBoardColor);
                try {
                    sb.append(game.game().getBoard().getPiece(new ChessPosition(row, i)).fancyToString());
                } catch (NullPointerException e) {
                    sb.append(EscapeSequences.EMPTY);
                }
            } else {
                sb.append(darkBoardColor);
                try {
                    sb.append(game.game().getBoard().getPiece(new ChessPosition(row, i)).fancyToString());
                } catch (NullPointerException e) {
                    sb.append(EscapeSequences.EMPTY);
                }
            }
        }
        sb.append(EscapeSequences.RESET_BG_COLOR);
        return sb.toString();
    }

    public static String outlineRowString(ChessGame.TeamColor orientation, String outlineColor) {
        StringBuilder sb = new StringBuilder();
        final char start = orientation == ChessGame.TeamColor.WHITE? 'a' : 'h';
        final char end = orientation == ChessGame.TeamColor.WHITE? 'i' : '`';
        final int dir = orientation == ChessGame.TeamColor.WHITE? 1 : -1;
        sb.append(outlineColor).append(EscapeSequences.EMPTY);
        for (char i = start; i != end; i+=dir) {
            sb.append("\u2005\u2004\u2005" + i + "\u2005\u2004\u2005");
        }
        sb.append(EscapeSequences.EMPTY);
        sb.append(EscapeSequences.RESET_BG_COLOR);
        return sb.toString();
    }

    public static String boardString(GameData game, ChessGame.TeamColor orientation,
                               String darkBoardColor, String lightBoardColor,
                               String outlineColor) {
        StringBuilder sb = new StringBuilder();

        final int start = orientation == ChessGame.TeamColor.WHITE? 8 : 1;
        final int end = orientation == ChessGame.TeamColor.WHITE? 0 : 9;
        final int dir = orientation == ChessGame.TeamColor.WHITE? -1 : 1;
        sb.append(outlineRowString(orientation, outlineColor)).append("\n");
        for (int i = start; i != end; i+=dir) {
            sb.append(boardRowString(i, game, orientation, darkBoardColor, lightBoardColor, outlineColor));
            sb.append("\n");
        }
        sb.append(outlineRowString(orientation, outlineColor)).append("\n");
        return sb.toString();
    }

    public static String getCommand(String inputLine) {
        return inputLine.trim().split(" +")[0].trim().toLowerCase();
    }

    public static String[] getArgs(String inputLine) {
        ArrayList<String> list;
        String[] listArr = (inputLine.trim().split("[ \\s]+"));
        list = new ArrayList<>(Arrays.asList(listArr));
        list.removeFirst();
        return list.toArray(new String[0]);
    }
}
