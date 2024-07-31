package dataaccess;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Identifier;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SQLParentDAO <K, V extends Identifier<K>> implements DataAccessInterface<K, V> {

//    private final String dbName = "chess_db";
    private final Gson gson = new GsonBuilder().enableComplexMapKeySerialization().serializeNulls().create();
    private final String tableName;
    private final String idName;
    private final V exVal;
    public SQLParentDAO(V templateVal) {
        exVal = templateVal;
        tableName = templateVal.getClass().getSimpleName();
        idName = templateVal.getIdField().getName();
        if (false) {
            try {
                generateVerboseTable(templateVal);
            } catch (DataAccessException e) {
                throw new RuntimeException(e);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            generateJSONTable(templateVal);
        }
    }

    private void generateJSONTable(V val) {
        try  {
            DatabaseManager.createDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        try (Connection conn = DatabaseManager.getConnection()) {
//            var createDbStatement = conn.prepareStatement(
//                    "CREATE DATABASE IF NOT EXISTS " + dbName
//            );
//            createDbStatement.executeUpdate();
//            conn.setCatalog(dbName);

            StringBuilder sb = new StringBuilder();
            sb.append("CREATE TABLE IF NOT EXISTS " + tableName + "(\n");
            sb.append(idName);
            if (val.getIdField().getType().getSimpleName().equals("String")) {
                sb.append(" VARCHAR(255)");
            } else if (val.getIdField().getType().getSimpleName().equals("int")) {
                sb.append(" INT");
            }
            sb.append(" NOT NULL,\n");
            sb.append("json TEXT(8192) NOT NULL,\n");
            sb.append("PRIMARY KEY (").append(idName).append(")\n");
            sb.append(");");

            try (var preparedStatement = conn.prepareStatement(sb.toString())) {
                preparedStatement.executeUpdate();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void generateVerboseTable(V val) throws DataAccessException, SQLException {
        val.getIdField();
        try  {
            DatabaseManager.createDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        try (Connection conn = DatabaseManager.getConnection()) {
//            var createDbStatement = conn.prepareStatement(
//                    "CREATE DATABASE IF NOT EXISTS " + dbName
//            );
//            createDbStatement.executeUpdate();
//            conn.setCatalog(dbName);

            StringBuilder sb = new StringBuilder();
            sb.append("CREATE TABLE IF NOT EXISTS " + tableName + "(\n");
//            for (Field field : Arrays.stream(val.getClass().getFields()).sorted(Comparator.comparing(Field::getName)).collect(Collectors.toSet())) {
            for (Field field: val.getClass().getFields()) {
                sb.append(field.getName() + " ");
                switch (field.getType().getSimpleName()) {
                    case "int" -> {
                        sb.append("INT");
                    }
                    case "String" -> {
                        sb.append("VARCHAR(255)");
                    }
                    case "" -> {
                        throw new IllegalStateException("Unexpected value: " + field.getType().getSimpleName());
                    }
                    default -> {
                        sb.append("VARCHAR(2047)");
                    }
                }
                if ( !field.getName().equals("whiteUsername") && !field.getName().equals("blackUsername")) {
                    sb.append(" NOT NULL");
                }
                sb.append(",\n");
            }
            sb.append("PRIMARY KEY (" + val.getIdField().getName() + ")\n);");

            try (var createTableStatement = conn.prepareStatement(sb.toString())) {
                createTableStatement.executeUpdate();
            }
        }
    }

    public V get(K id) {
        try (Connection conn = DatabaseManager.getConnection()) {

            StringBuilder sb = new StringBuilder();
            sb.append("SELECT json FROM ").append(tableName)
                    .append(" WHERE ").append(idName).append("=?;");
//            System.out.println("IMPORTANT! " + sb.toString());
            try (var preparedStatement = conn.prepareStatement(sb.toString())) {
                if (id.getClass() == String.class) {
                    preparedStatement.setString(1, (String) id);
                }  else if (id.getClass() == Integer.class) {
                    preparedStatement.setInt(1, (int) id);
                }
                try (var rs = preparedStatement.executeQuery()) {
//                    System.out.println("preparedStatement: " + preparedStatement.toString());
                    if (rs.next()) {
                        try {
                            var data = gson.fromJson(rs.getString("json"), (Class<V>) exVal.getClass());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        return gson.fromJson(rs.getString("json"), (Class<V>) exVal.getClass());
                    } else {
                        return null;
                    }
                }

            }
        } catch (Exception e) {
            return null;
        }
    }

    public void add(V value) {
        try (Connection conn = DatabaseManager.getConnection()) {
            StringBuilder sb = new StringBuilder();
            sb.append("INSERT INTO ").append(tableName).append(" (")
                    .append(idName).append(", ").append("json) ");
            sb.append("VALUES (?, ?)");
            sb.append(" ON DUPLICATE KEY UPDATE json=?");
            try (var preparedStatement = conn.prepareStatement(sb.toString())) {
                preparedStatement.setString(2, gson.toJson(value));
                preparedStatement.setString(3, gson.toJson(value));
                if (value.getId().getClass() == Integer.class) {
                    preparedStatement.setInt(1, (int) value.getId());
                } else if (value.getId().getClass() == String.class) {
                    preparedStatement.setString(1, (String) value.getId());
                }
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(K key, V value) throws DataAccessException {
//        try (Connection conn = DatabaseManager.getConnection()) {
//            StringBuilder sb = new StringBuilder();
//            sb.append("UPDATE ").append(tableName).append(" SET ").append(exVal.getIdField());
//        } catch (SQLException e) {
//            throw new DataAccessException(e.getMessage() + "Key: " + key + " isn't found in the database, unable to update it. ");
//        }

        if ( get(key) == null ) {
            throw new DataAccessException("Key: " + key + " isn't found in the database, unable to update it. ");
        }
        if ( get(value.getId()) != null && !key.equals(value.getId()) ) {
            throw new DataAccessException("Unable to update value: " + value
                    + " to key: " + value.getId() + " because that key already exists. ");
        }

        remove(key);
        add(value);
    }

    public void remove(K key) {
        try (Connection conn = DatabaseManager.getConnection()) {
            StringBuilder sb = new StringBuilder();
            sb.append("DELETE FROM ").append(tableName).append(" WHERE ")
                    .append(idName).append(" = ?;");
            try (var preparedStatement = conn.prepareStatement(sb.toString())) {
                if (key.getClass() == String.class) {
                    preparedStatement.setString(1, (String) key);
                } else if (key.getClass() == int.class) {
                    preparedStatement.setInt(1, (int) key);
                } else if (key.getClass() == Integer.class) {
                    preparedStatement.setInt(1, (int) key);
                }
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Collection<V> getAll() {
        HashSet<V> toReturn = new HashSet<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT json FROM ").append(tableName).append(";");
            try (var preparedStatement = conn.prepareStatement(sb.toString())) {
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        toReturn.add( gson.fromJson(rs.getString("json"), (Class<V>) exVal.getClass()) );
                    }
                    return toReturn;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void clear() {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("TRUNCATE TABLE " + tableName + ";")) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
