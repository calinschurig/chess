package dataaccess;

import model.UserData;
import java.sql.SQLException;

public class SQLUserDAO extends SQLParentDAO<String, UserData> implements UserDAO {
    public SQLUserDAO(UserData templateVal) {
        super(templateVal);
    }
}
