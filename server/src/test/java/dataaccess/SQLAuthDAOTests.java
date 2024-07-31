package dataaccess;

import model.AuthData;
import testutilities.SQLParentDAOTests;

public class SQLAuthDAOTests extends SQLParentDAOTests<String, AuthData> {
    static {
        memoryDAO = new MemoryAuthDAO();
        sqlDAO = new SQLAuthDAO(new AuthData("",""));
        data = new AuthData("authtoken-oh-yeah", "my-username");
        data2 = new AuthData("authtoken-oh-yeah", "another-username");
        differentData = new AuthData("different-authtoken", "a-different-username");
        bogusData = new AuthData("Bad-authtoken", "username");
    }
}
