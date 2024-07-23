package model;

public record UserData(
        String username, String password, String email
) implements Identifier<String> {
    public String getId() {
        return username;
    }
}
