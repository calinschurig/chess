package model;

import java.lang.reflect.Field;

public interface Identifier<T> extends Comparable<Identifier<T>> {
    T getId();
    Field getIdField();
}
