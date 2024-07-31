package dataaccess;

import model.AuthData;

import java.sql.SQLException;

public class SQLAuthDAO extends SQLParentDAO<String, AuthData> implements AuthDAO {
    public SQLAuthDAO(AuthData templateVal) {
        super(templateVal);
    }
}
