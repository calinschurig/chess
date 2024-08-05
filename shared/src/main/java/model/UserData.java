package model;

import java.lang.reflect.Field;

public record UserData(
        String username, String password, String email
) implements Identifier<String> {
    public String getId() {
        return username;
    }
    public Field getIdField() {
        try {
            return this.getClass().getDeclaredField("username");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public int compareTo(UserData o) {
        return username.compareTo(o.username);
    }

    public int compareTo(Identifier<String> o) {
        return getId().compareTo(o.getId());
    }
}
