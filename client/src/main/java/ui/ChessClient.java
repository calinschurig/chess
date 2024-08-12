package ui;

import model.AuthData;
import model.GameData;
import server.facade.RejectedRequestException;
import server.facade.ServerFacade;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Scanner;

import static ui.GameClient.helpGame;
import static ui.GameClient.redraw;

public class ChessClient {
    private ServerFacade facade;
    private AuthData auth;
    private boolean loggedIn = false;
    private boolean inGame = false;
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
            String command = ClientHelper.getCommand(line);
            String[] args = ClientHelper.getArgs(line);
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
        return prompt(loggedIn);
    }
    private String prompt(boolean isLoggedIn) {
        String username = (isLoggedIn) ? auth.username() : "LOGGED_OUT";
        return "[" + username + "]>>>";
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
                return LoggedInClient.joinGame(args, facade, auth, gameIndextoId);
            }
            case "observe" -> {
                return LoggedInClient.observeGame(args, facade, auth, gameIndextoId);
            }
            case "quit" -> {
                return quit(args);
            }
            case null, default -> {
                throw new RuntimeException("Invalid command. Type 'help' for a list of available commands. ");
            }
        }
    }

    public static String runCommandGame(String command, String[] args) {
        switch (command) {
            case "redraw" -> {
                return redraw(args);
            }
            case "leave" -> {

            }
            case "resign" -> {

            }
            case "moves" -> {

            }
            case "move" -> {

            }
            case "help" -> {
                return helpGame(args);
            }
            case null, default -> {
                throw new RuntimeException("Invalid command. Type 'help' for a list of available commands. ");
            }
        }
        return "todo";
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

}
