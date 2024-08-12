package server;

import spark.Spark;


public class WSServer {

    public static void main(String[] args) throws Exception {
        Spark.port(8080);
        Spark.webSocket("/ws", WSServer.class);
        Spark.get("/ech/:msg", (req, res) -> "HTTP response: " + req.params(":msg"));
    }


}
