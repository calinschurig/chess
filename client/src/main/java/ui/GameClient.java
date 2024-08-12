package ui;

import static ui.ClientHelper.checkArgs;

public class GameClient {


    public static String redraw(String[] args) {
        checkArgs(new Class[] {}, args);
        return "todo";
    }
    public static String leave(String[] args) {
        checkArgs(new Class[] {}, args);
        return "todo";
    }
    public static String resign(String[] args) {
        checkArgs(new Class[] {}, args);
        return "todo";
    }
    public static String moves(String[] args) {
        checkArgs(new Class[] {}, args);
        return "todo";
    }
    public static String move(String[] args) {
        checkArgs(new Class[] {}, args);
        return "todo";
    }
    public static String helpGame(String[] args) {
        checkArgs(new Class[] {}, args);
        String output = """
                redraw - the board
                leave - the game
                resign - give up, lose the game
                moves <POSITION> - highlight possible moves. A3, F8, and D2 are all valid positions
                move <CURRENT_POSITION> <NEW_POSITION> - moves a piece from one position to the other
                help - with possible commands""";
        return output;
    }


}
