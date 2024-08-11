package ui;

import chess.ChessGame;
import chess.ChessPosition;
import chess.EscapeSequences;
import model.AuthData;
import model.GameData;
import server.facade.RejectedRequestException;
import server.facade.ServerFacade;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class ChessClient {
    private ServerFacade facade;
    private AuthData auth;
    private boolean loggedIn = false;
    private boolean shouldQuit = false;
    private HashMap<Integer, Integer> gameIndextoId;

    public ChessClient(String hostUrl) {
        try {
            facade = new ServerFacade(hostUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
    public ChessClient() {
        facade = new ServerFacade();
    }

    public void run(String hostUrl) {
        try {
            facade = new ServerFacade(hostUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        run();
    }
    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print(prompt());

            String line = scanner.nextLine();
            String command = getCommand(line);
            String[] args = getArgs(line);
            try {
                String commandOut = (loggedIn) ? runCommandPost(command, args) : runCommandPre(command, args);
                System.out.println( commandOut );
//            } catch (ConnectException ce) {
//                System.out.println(ce.getMessage());
            } catch (Exception e) {
//                throw e;
                System.out.println(e.getMessage());
            }
        }
    }

    private String prompt() {
        String username = (loggedIn) ? auth.username() : "LOGGED_OUT";
        return "[" + username + "]>>>";
    }

    private String getCommand(String inputLine) {
        return inputLine.trim().split(" +")[0].trim().toLowerCase();
    }
    private String[] getArgs(String inputLine) {
        ArrayList<String> list;
        String[] listArr = (inputLine.trim().split("[ \\s]+"));
        list = new ArrayList<>(Arrays.asList(listArr));
        list.removeFirst();
        return list.toArray(new String[0]);
    }

    private String runCommandPre(String command, String[] args) {
        switch (command) {
            case "help" -> {
                return helpPre(args);
            }
            case "quit" -> {
                return quit(args);
            }
            case "login" -> {
                return login(args);
            }
            case "register" -> {
                return register(args);
            }
            case null, default -> {
                throw new RuntimeException("Invalid command. Type 'help' for a list of available commands. ");
            }
        }
    }

    private String runCommandPost(String command, String[] args) {
        switch (command) {
            case "help" -> {
                return helpPost(args);
            }
            case "logout" -> {
                return logout(args);
            }
            case "create" -> {
                return createGame(args);
            }
            case "list" -> {
                return listGames(args);
            }
            case "join" -> {
                return joinGame(args);
            }
            case "observe" -> {
                return observeGame(args);
            }
            case "quit" -> {
                return quit(args);
            }
            case null, default -> {
                throw new RuntimeException("Invalid command. Type 'help' for a list of available commands. ");
            }
        }
    }

    private String helpPre(String[] args) {
        checkArgs(new Class[] {}, args);
        String output = """
            register <USERNAME> <PASSWORD> <EMAIL> - to create an account
            login <USERNAME> <PASSWORD> - to play chess
            quit - playing chess
            help - with possible commands""";
        return output;
    }
    private String helpPost(String[] args) {
        checkArgs(new Class[] {}, args);
        String output = """
                create <NAME> - a game
                list - games
                join <ID> [WHITE|BLACK] - a game
                observe <ID> - view a game
                logout - when you are done
                quit - playing chess
                help - with possible commands""";
        return output;
    }
    private String login(String[] args) {
        checkArgs(new Class[] {String.class, String.class}, args);
        try {
            auth = facade.login(args[0], args[1]);
            loggedIn = true;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } catch (RejectedRequestException e) {
            throw new RuntimeException(e.getMessage());
        }
        return "Welcome " + auth.username() + "!";

    }
    private String quit(String[] args) {
        checkArgs(new Class[] {}, args);
        shouldQuit = true;
        if (loggedIn) {
            logout(args);
        }
        String output = "Goodbye!";
        System.out.println(output);
        System.exit(0);
        return output;
    }
    private String register(String[] args) {
        checkArgs(new Class[] {String.class, String.class, String.class}, args);
        try {
            auth = facade.register(args[0], args[1], args[2]);
            loggedIn = true;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } catch (RejectedRequestException e) {
            throw new RuntimeException(e.getMessage());
        }
        return "Successfully registered " + auth.username() + ". Welcome " + auth.username() + "!";
    }
    private String logout(String[] args) {
        checkArgs(new Class[] {}, args);
        try {
            facade.logout(auth);
            loggedIn = false;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } catch (RejectedRequestException e) {
            throw new RuntimeException(e.getMessage());
        }
        return "Logged out user: " + auth.username();
    }
    private String createGame(String[] args) {
        checkArgs(new Class[] {String.class}, args);
        try {
            facade.createGame(args[0], auth.authToken());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } catch (RejectedRequestException e) {
            throw new RuntimeException(e.getMessage());
        }
        return "Created game: " + args[0];
    }
    private String listGames(String[] args) {
        checkArgs(new Class[] {}, args);
        GameData[] games;
        try {
            games = facade.listGames(auth.authToken()).toArray(GameData[]::new);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } catch (RejectedRequestException e) {
            throw new RuntimeException(e.getMessage());
        }

        StringBuilder sb = new StringBuilder();
        int i = 1;
        HashMap<Integer, Integer> indexToId = new HashMap<>();
        for (GameData game : games) {
            if (i >=2 ) {
                sb.append("\n");
            }
            String template = "%4d -> GAME: %-20.20s   WHITE: %-16.16s   BLACK: %-16.16s";
            String white = game.whiteUsername() == null ? "AVAILABLE" : game.whiteUsername();
            String black = game.blackUsername() == null ? "AVAILABLE" : game.blackUsername();
            sb.append(template.formatted(i, game.gameName(), white, black));
            indexToId.put(i, game.gameID());

            i++;
        }
        gameIndextoId = indexToId;
        return sb.toString();
    }
    private String joinGame(String[] args) {
        checkArgs(new Class[] {int.class, String.class}, args);
        if (gameIndextoId == null) {
            return "Please list games before attempting to join. ";
        }
        int gameNum = Integer.parseInt(args[0]);
        if (!gameIndextoId.containsKey(gameNum)) {
            return "Invalid argument: please use numbers listed by the LIST command";
        }
        if (!args[1].equalsIgnoreCase("BLACK") && !args[1].equalsIgnoreCase("WHITE") ) {
//            System.out.println("args[1]: " + args[1]);
            return "Invalid argument: color must be either WHITE or BLACK";
        }
        int gameId = gameIndextoId.get(gameNum);
        try {
            facade.joinGame(gameId, args[1].toUpperCase(), auth.authToken());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } catch (RejectedRequestException e) {
            throw new RuntimeException(e.getMessage());
        }
        return "Joined game " + gameNum + " to " + args[1].toUpperCase();
    }
    private String observeGame(String[] args) {
        checkArgs(new Class[] {int.class}, args);
        int gameNum = Integer.parseInt(args[0]);
        if (gameIndextoId == null) {
            return "Please list games before attempting to observe. ";
        }
        if (!gameIndextoId.containsKey(gameNum)) {
            return "Invalid argument: please use numbers listed by the LIST command";
        }
        int gameId = gameIndextoId.get(gameNum);
        try {
            String out = boardString(facade.gamesMap(auth).get(gameId), ChessGame.TeamColor.WHITE,
                    EscapeSequences.SET_BG_COLOR_DARK_GREEN, EscapeSequences.SET_BG_COLOR_BLUE,
                    EscapeSequences.SET_BG_COLOR_LIGHT_GREY)
                    + "\n"
                    + boardString(facade.gamesMap(auth).get(gameId), ChessGame.TeamColor.BLACK,
                    EscapeSequences.SET_BG_COLOR_DARK_GREEN, EscapeSequences.SET_BG_COLOR_BLUE,
                    EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
            return out;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } catch (RejectedRequestException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void checkArgs(Class[] argTypes, String[] args) {
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

    private String boardString(GameData game, ChessGame.TeamColor orientation,
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

    private String boardRowString(int row, GameData game, ChessGame.TeamColor orientation,
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

    private String outlineRowString(ChessGame.TeamColor orientation, String outlineColor) {
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

}
