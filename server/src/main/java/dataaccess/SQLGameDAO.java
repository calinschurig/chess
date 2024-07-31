package dataaccess;

import model.GameData;

import java.sql.SQLException;

public class SQLGameDAO extends SQLParentDAO<Integer, GameData> implements GameDAO {
    public SQLGameDAO(GameData templateVal) {
        super(templateVal);
    }
}