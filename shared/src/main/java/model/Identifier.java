package model;

import java.lang.reflect.Field;

public interface Identifier<T> {
    T getId();
    Field getIdField();
}
