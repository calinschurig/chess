package model;

import java.lang.reflect.Field;

public record AuthData (
        String authToken, String username
) implements Identifier<String> {
    public String getId() {
        return authToken;
    }
    public Field getIdField() {
        try {
            return this.getClass().getDeclaredField("authToken");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public int compareTo(Identifier<String> o) {
        return this.getId().compareTo(o.getId());
    }
}
