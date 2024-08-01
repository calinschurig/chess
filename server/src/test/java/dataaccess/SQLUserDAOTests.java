package dataaccess;

import model.UserData;
import org.junit.jupiter.api.*;
import mytestutilities.SQLParentDAOTests;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class SQLUserDAOTests extends SQLParentDAOTests<String, UserData> {
    static {
        memoryDAO = new MemoryUserDAO();
        sqlDAO = new SQLUserDAO(new UserData("","", ""));
        data = new UserData("my-username", "my-password", "email");
        data2 = new UserData("my-username", "another-password", "email");
        differentData = new UserData("a-different-username", "a-different-password", "email");
        bogusData = new UserData("bogus-username", "bogus-password", "email");
    }
}
