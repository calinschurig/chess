package mytestutilities;

import dataaccess.DataAccessException;
import dataaccess.MemoryParentDAO;
import dataaccess.SQLParentDAO;
import model.Identifier;
import org.junit.jupiter.api.*;

import java.util.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SQLParentDAOTests<K, V extends Identifier<K>> {
    public static MemoryParentDAO memoryDAO;
    public static SQLParentDAO sqlDAO;
    public static Identifier data;
    public static Identifier data2;
    public static Identifier differentData;
    public static Identifier bogusData;

    @Test
    @Order(1)
    @DisplayName("Valid add")
    public void validAdd() {
        sqlDAO.clear();
        memoryDAO.add(data);
        sqlDAO.add(data);
        Assertions.assertArrayEquals(memoryDAO.getAll().toArray(), sqlDAO.getAll().toArray());
    }

    @Test
    @Order(2)
    @DisplayName("Repeat add")
    public void repeatAdd() {
        memoryDAO.add(data2);
        sqlDAO.add(data2);
        Assertions.assertArrayEquals(memoryDAO.getAll().toArray(), sqlDAO.getAll().toArray());
    }

    @Test
    @Order(3)
    @DisplayName("Valid get")
    public void validGet() {
        Assertions.assertEquals(memoryDAO.get(data.getId()), sqlDAO.get(data.getId()));
    }

    @Test
    @Order(4)
    @DisplayName("Invalid get")
    public void invalidGet() {
        Assertions.assertEquals(memoryDAO.get(bogusData.getId()), sqlDAO.get(bogusData.getId()));;
    }

    @Test
    @Order(5)
    @DisplayName("Invalid remove")
    public void invalidRemove() {
        memoryDAO.remove(bogusData.getId());
        sqlDAO.remove(bogusData.getId());
        Assertions.assertArrayEquals(memoryDAO.getAll().toArray(), sqlDAO.getAll().toArray());
    }

    @Test
    @Order(6)
    @DisplayName("Valid remove")
    public void validRemove() {
        memoryDAO.remove(data.getId());
        sqlDAO.remove(data.getId());
        Assertions.assertArrayEquals(memoryDAO.getAll().toArray(), sqlDAO.getAll().toArray());
    }

    @Test
    @Order(7)
    @DisplayName("Valid update")
    public void validUpdate() throws DataAccessException {
        sqlDAO.clear();
        memoryDAO.clear();
        memoryDAO.add(data);
        sqlDAO.add(data);
        memoryDAO.update(data.getId(), differentData);
        sqlDAO.update(data.getId(), differentData);
        Assertions.assertArrayEquals(memoryDAO.getAll().toArray(), sqlDAO.getAll().toArray());
    }

    @Test
    @Order(8)
    @DisplayName("Invalid update")
    public void invalidUpdate() {
        memoryDAO.add(data);
        sqlDAO.add(data);
        Assertions.assertThrows(DataAccessException.class, () -> memoryDAO.update(differentData.getId(), data));
        Assertions.assertThrows(DataAccessException.class, () -> sqlDAO.update(differentData.getId(), data));
        Assertions.assertThrows(DataAccessException.class, () -> memoryDAO.update(bogusData.getId(), differentData));
        Assertions.assertThrows(DataAccessException.class, () -> memoryDAO.update(bogusData.getId(), differentData));
        Assertions.assertArrayEquals(memoryDAO.getAll().toArray(), sqlDAO.getAll().toArray());
    }

    @Test
    @Order(9)
    @DisplayName("Get all")
    public void getAll() {
        memoryDAO.add(data);
        sqlDAO.add(data);
        memoryDAO.add(differentData);
        sqlDAO.add(differentData);
        Assertions.assertArrayEquals(memoryDAO.getAll().toArray(), sqlDAO.getAll().toArray());
    }

    @Test
    @Order(10)
    @DisplayName("Empty get all")
    public void emptyGetAll() {
        Identifier[] emptyData = {};
        memoryDAO.remove(data.getId());
        sqlDAO.remove(data.getId());
        memoryDAO.remove(differentData.getId());
        sqlDAO.remove(differentData.getId());
        Assertions.assertArrayEquals(memoryDAO.getAll().toArray(), sqlDAO.getAll().toArray());
        Assertions.assertArrayEquals(emptyData, sqlDAO.getAll().toArray());
    }

    @Test
    @Order(11)
    @DisplayName("Clear")
    public void clear() {
        memoryDAO.add(data);
        sqlDAO.add(data);
        memoryDAO.add(differentData);
        sqlDAO.add(differentData);
        memoryDAO.add(bogusData);
        sqlDAO.add(bogusData);
        Identifier[] emptyData = {};
        memoryDAO.clear();
        sqlDAO.clear();
        Assertions.assertArrayEquals(memoryDAO.getAll().toArray(), sqlDAO.getAll().toArray());
        Assertions.assertArrayEquals(emptyData, sqlDAO.getAll().toArray());
    }



}
