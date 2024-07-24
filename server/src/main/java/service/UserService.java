package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class UserService {

    // warning: stores unencrypted passwords.
    public static AuthData register(UserData user, UserDAO userDAO, AuthDAO authDAO) throws DataAccessException {
        if ( userDAO.get(user.getId()) != null ) throw new DataAccessException("Cannot register user because username is already taken: " + user.username());
        else if ( user.username().isEmpty() ) throw new DataAccessException("Cannot register user because username is blank. ");
        else userDAO.add(user);
//        System.out.println("Returning from line 19");
        return login(user.username(), user.password(), userDAO, authDAO);
    }

    // warning: stores unencrypted passwords.
    public static AuthData login(String username, String password, UserDAO userDAO, AuthDAO authDAO) throws DataAccessException {
        if ( null == userDAO.get(username) ) throw new DataAccessException("Error logging in; no user exists with username: " + username);
        if ( !password.equals(userDAO.get(username).password()) ) throw new DataAccessException("Incorrect password. ");
        String possibleAuthToken = UUID.randomUUID().toString();
        while ( null != authDAO.get(possibleAuthToken) ) possibleAuthToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(possibleAuthToken, username);
        authDAO.add(authData);
        return authData;
    }

    public static void logout(String authToken, AuthDAO authDAO) throws DataAccessException {
        if ( null == authDAO.get(authToken) ) throw new DataAccessException("Error logging out; invalid authToken of value: " + authToken);
        authDAO.remove(authToken);
    }

    public static boolean isAuthorized(String authToken, AuthDAO authDAO) {
        if ( null == authDAO.get(authToken) ) return false;
        else return true;
    }

    public static String getUsername(String authToken, AuthDAO authDAO) throws DataAccessException {
        if (!isAuthorized(authToken, authDAO)) throw new DataAccessException("Cannot getUsername with invalid authToken: " + authToken);
        else return authDAO.get(authToken).username();
    }
}
