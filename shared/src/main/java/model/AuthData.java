package model;

public record AuthData (
        String authToken, String username
) implements Identifier<String> {
    public String getId() {
        return authToken;
    }
}
