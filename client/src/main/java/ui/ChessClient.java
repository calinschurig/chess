package ui;

import chess.ChessGame;
import chess.EscapeSequences;
import model.AuthData;
import model.GameData;
import server.facade.RejectedRequestException;
import server.facade.ServerFacade;
import server.facade.WSClient;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Scanner;

import static ui.GameClient.*;

public class ChessClient {
    private ServerFacade facade;
    private WSClient wsClient;
    private AuthData auth;
//    private GameData game = null;
    private boolean loggedIn = false;
    private boolean inGame = false;
    private boolean defaultPrompt = true;
    private int currentGame = -1;
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
            if (!inGame || defaultPrompt) {
                System.out.print(prompt());
            }
            defaultPrompt = true;

            String line = scanner.nextLine();
            String command = ClientHelper.getCommand(line);
            String[] args = ClientHelper.getArgs(line);
            try {
                String commandOut = (!loggedIn) ? runCommandPre(command, args)
                        : (inGame) ? runCommandGame(command, args)
                        : runCommandPost(command, args);
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
        return prompt(loggedIn, inGame);
    }
    private String prompt(boolean isLoggedIn, boolean isInGame) {
        String username = (isLoggedIn) ? auth.username() : "LOGGED_OUT";
        if (!isInGame) {
            return "[" + username + "]>>>";
        }
        if (wsClient.gameData == null) {
            return "[" + username + ": LOADING_GAME]>>>";
        }
        StringBuilder toReturn = new StringBuilder();
        toReturn.append("[" + username + ": " + wsClient.gameData.gameName() + " as ");
        boolean isWhite = wsClient.gameData.whiteUsername() != null && wsClient.gameData.whiteUsername().equalsIgnoreCase(username);
        boolean isBlack = wsClient.gameData.blackUsername() != null && wsClient.gameData.blackUsername().equalsIgnoreCase(username);
        if (!isWhite && !isBlack) {
            toReturn.append("OBSERVER");
        } else if (isWhite && !isBlack) {
            toReturn.append("WHITE");
        } else if (isBlack && !isWhite) {
            toReturn.append("BLACK");
        } else if (isBlack && isWhite) {
            toReturn.append("WHITE and BLACK");
        }
        toReturn.append("]>>>");
        return toReturn.toString();
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
                return LoggedInClient.helpPost(args);
            }
            case "logout" -> {
                return logout(args);
            }
            case "create" -> {
                return LoggedInClient.createGame(args, facade, auth);
            }
            case "list" -> {
                return listGames(args, facade, auth);
            }
            case "join" -> {
                return joinGame(args, facade, auth, gameIndextoId);
            }
            case "observe" -> {
                return observeGame(args, facade, auth, gameIndextoId);
            }
            case "quit" -> {
                return quit(args);
            }
            case null, default -> {
                throw new RuntimeException("Invalid command. Type 'help' for a list of available commands. ");
            }
        }
    }

    public String runCommandGame(String command, String[] args) {
        switch (command) {
            case "redraw" -> {
                defaultPrompt = false;
                connect(args, wsClient, auth, currentGame);
                return "REDRAWING BOARD";
            }
            case "leave" -> {
                if (!wsClient.session.isOpen()) {
                    inGame = false;
                }
                leave(args, wsClient, auth, currentGame);
                if (!wsClient.session.isOpen()) {
                    inGame = false;
                }
                return "LEAVING GAME";
            }
            case "resign" -> {
                return resign(args);
            }
            case "moves" -> {
                return moves(args);
            }
            case "move" -> {
                return move(args);
            }
            case "help" -> {
                return helpGame(args);
            }
            case null, default -> {
                throw new RuntimeException("Invalid command. Type 'help' for a list of available commands. ");
            }
        }
//        return "todo";
    }

    private static String helpPre(String[] args) {
        ClientHelper.checkArgs(new Class[] {}, args);
        String output = """
            register <USERNAME> <PASSWORD> <EMAIL> - to create an account
            login <USERNAME> <PASSWORD> - to play chess
            quit - playing chess
            help - with possible commands""";
        return output;
    }

    private String login(String[] args) {
        ClientHelper.checkArgs(new Class[] {String.class, String.class}, args);
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
        ClientHelper.checkArgs(new Class[] {}, args);
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
        ClientHelper.checkArgs(new Class[] {String.class, String.class, String.class}, args);
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
        ClientHelper.checkArgs(new Class[] {}, args);
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

    private String listGames(String[] args, ServerFacade facade, AuthData auth) {
        ClientHelper.checkArgs(new Class[] {}, args);
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

    private String joinGame(String[] args, ServerFacade facade, AuthData auth, HashMap<Integer, Integer> gameIndextoId) {
        ClientHelper.checkArgs(new Class[] {int.class, String.class}, args);
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
        try {
            String wsUrl = facade.getUrlAsString().replaceFirst("http://", "ws://") + "ws";
//            System.out.println(wsUrl);
            wsClient = new WSClient(wsUrl, this::prompt);
            currentGame = gameId;
            inGame = true;
            defaultPrompt = false;
            connect(new String[] {}, wsClient, auth, gameId);
        } catch (Exception e) {
//            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }


        return "Joined game " + gameNum + " to " + args[1].toUpperCase();
    }

    public static String observeGame(String[] args, ServerFacade facade, AuthData auth, HashMap<Integer, Integer> gameIndextoId) {
        ClientHelper.checkArgs(new Class[] {int.class}, args);
        int gameNum = Integer.parseInt(args[0]);
        if (gameIndextoId == null) {
            return "Please list games before attempting to observe. ";
        }
        if (!gameIndextoId.containsKey(gameNum)) {
            return "Invalid argument: please use numbers listed by the LIST command";
        }
        int gameId = gameIndextoId.get(gameNum);
//        try {
////            String out = ClientHelper.boardString(facade.gamesMap(auth).get(gameId), ChessGame.TeamColor.WHITE,
////                    EscapeSequences.SET_BG_COLOR_DARK_GREEN, EscapeSequences.SET_BG_COLOR_BLUE,
////                    EscapeSequences.SET_BG_COLOR_LIGHT_GREY)
////                    + "\n"
////                    + ClientHelper.boardString(facade.gamesMap(auth).get(gameId), ChessGame.TeamColor.BLACK,
////                    EscapeSequences.SET_BG_COLOR_DARK_GREEN, EscapeSequences.SET_BG_COLOR_BLUE,
////                    EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
//            String out = "todo";
//            return out;
//        } catch (IOException e) {
//            throw new RuntimeException(e.getMessage());
//        } catch (RejectedRequestException e) {
//            throw new RuntimeException(e.getMessage());
//        }
        return "todo";
    }

}
