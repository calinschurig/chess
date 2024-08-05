package server.facade;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.AuthData;
import model.UserData;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.concurrent.RejectedExecutionException;

public class ServerFacade {
    private URL url;
//    private AuthData auth;
    private static final Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();

    public ServerFacade(String url) throws MalformedURLException {
        this.url = new URL(url);
    }
    public ServerFacade() {
        try {
            this.url = new URL("http://localhost:8080/");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }



    public void logout(AuthData auth) throws IOException, RejectedRequestException {
        logout(auth.authToken());
    }
    public void logout(String authToken) throws IOException, RejectedRequestException {
        HttpURLConnection con = resolve("/session");
        con.setRequestMethod("DELETE");
        con.setDoOutput(true);
        con.setRequestProperty("Authorization", authToken);
        con.connect();

        if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new RejectedRequestException(con.getResponseMessage());
        }

    }

    public AuthData login(String username, String password) throws IOException, RejectedRequestException {
        HttpURLConnection con = resolve("/session");
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Accept", "test/html");
        con.setRequestProperty("Content-Type", "application/json");

        try (OutputStream os = con.getOutputStream()) {
            os.write(("{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}").getBytes());
        }

        con.connect();

        if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
            AuthData auth = parseJsonConnection(con.getInputStream(), AuthData.class);
            return auth;
        } else {
            throw new RejectedRequestException("Unable to log in: " + new String( con.getErrorStream().readAllBytes()) );
        }

    }

    public AuthData register(String username, String password, String email) throws IOException, RejectedRequestException {
        return register(new UserData(username, password, email));
    }
    public AuthData register(UserData user) throws IOException, RejectedRequestException {
        HttpURLConnection con = resolve("/user");
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Accept", "test/html");
        con.setRequestProperty("Content-Type", "application/json");

        try (OutputStream os = con.getOutputStream()) {
            os.write(new Gson().toJson(user).getBytes());
        }
        con.connect();
        if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
            AuthData auth = parseJsonConnection(con.getInputStream(), AuthData.class);
            return auth;
        } else {
//            String err = con.getResponseMessage();
            String err = new String(con.getErrorStream().readAllBytes());
            throw new RejectedRequestException("Server rejected register: " + err);
        }
    }

    private HttpURLConnection resolve(String path) throws IOException {
        try {
            HttpURLConnection con = (HttpURLConnection) url.toURI().resolve(path).toURL().openConnection();
            return con;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T parseJsonConnection(InputStream is, Class<T> clazz) {
        String jsonString;
        try {
            jsonString = new String(is.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return gson.fromJson(jsonString, clazz);
    }

}
