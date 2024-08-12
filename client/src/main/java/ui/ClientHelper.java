package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.EscapeSequences;
import model.GameData;

import java.util.*;

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

    public static String boardRowString(int row, GameData game, ChessPosition selectedPiece,
                                        ChessGame.TeamColor orientation, Collection<ChessPosition> possibleEnds,
                                        String whitePieceColor, String blackPieceColor,
                                        String lightBoardColor, String darkBoardColor,
                                        String movableLightBoardColor, String movableDarkBoardColor,
                                        String selectedBoardColor, String outlineColor) {
        StringBuilder sb = new StringBuilder();

        final int start = orientation == ChessGame.TeamColor.WHITE? 0 : 9;
        final int end = orientation == ChessGame.TeamColor.WHITE? 10 : -1;
        final int dir = orientation == ChessGame.TeamColor.WHITE? 1 : -1;

        for (int i = start; i != end; i+=dir) {
            if (i == 0 | i == 9) {
                sb.append(outlineColor).append("\u2005\u2004\u2005" + row + "\u2005\u2004\u2005");
            } else if ( (row + i) % 2 == 1) {
                String color = (possibleEnds.contains(new ChessPosition(row, i))) ? movableLightBoardColor : lightBoardColor;
                color = (Objects.equals(selectedPiece, new ChessPosition(row, i))) ? selectedBoardColor : color;
                sb.append(color);
                try {
                    sb.append(game.game().getBoard().getPiece(new ChessPosition(row, i)).fancyToString(whitePieceColor, blackPieceColor));
                } catch (NullPointerException e) {
                    sb.append(EscapeSequences.EMPTY);
                }
            } else {
                String color = (possibleEnds.contains(new ChessPosition(row, i))) ? movableDarkBoardColor : darkBoardColor;
                color = (Objects.equals(selectedPiece, new ChessPosition(row, i))) ? selectedBoardColor : color;
                sb.append(color);
                try {
                    sb.append(game.game().getBoard().getPiece(new ChessPosition(row, i)).fancyToString(whitePieceColor, blackPieceColor));
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

    public static String boardString(GameData game, ChessPosition selectedPiece, ChessGame.TeamColor orientation) {
        return boardString(game, selectedPiece, orientation,
                EscapeSequences.SET_TEXT_COLOR_BLUE, EscapeSequences.SET_TEXT_COLOR_RED,
                EscapeSequences.SET_BG_COLOR_LIGHT_GREY, EscapeSequences.SET_BG_COLOR_DARK_GREY,
                EscapeSequences.SET_BG_COLOR_GREEN, EscapeSequences.SET_BG_COLOR_DARK_GREEN,
                EscapeSequences.SET_BG_COLOR_YELLOW, EscapeSequences.SET_BG_COLOR_MAGENTA);
    }
    public static String boardString(GameData game, ChessPosition selectedPiece, ChessGame.TeamColor orientation,
                               String whitePieceColor, String blackPieceColor,
                               String lightBoardColor, String darkBoardColor,
                               String selectedLightBoardColor, String selectedDarkBoardColor,
                               String selectedBoardColor, String outlineColor) {
        StringBuilder sb = new StringBuilder();

        final int start = orientation == ChessGame.TeamColor.WHITE? 8 : 1;
        final int end = orientation == ChessGame.TeamColor.WHITE? 0 : 9;
        final int dir = orientation == ChessGame.TeamColor.WHITE? -1 : 1;
        Collection<ChessPosition> possibleEnds;
        if (selectedPiece != null) {
            possibleEnds = game.game().validMovesZone(selectedPiece);
        } else {
            possibleEnds = HashSet.newHashSet(0);
        }
        sb.append(outlineRowString(orientation, outlineColor)).append("\n");
        for (int i = start; i != end; i+=dir) {
            sb.append(boardRowString(i, game, selectedPiece,
                    orientation, possibleEnds,
                    whitePieceColor, blackPieceColor,
                    darkBoardColor, lightBoardColor,
                    selectedLightBoardColor, selectedDarkBoardColor,
                    selectedBoardColor, outlineColor));
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
