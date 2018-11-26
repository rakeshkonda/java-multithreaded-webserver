package org.webserver;

import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A Multi-Threaded Java Web Server capable of processing multiple simultaneous service requests in parallel.
 *
 * References:
 */
public class WebServer {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(WebServer.class);
    private static final int DEFAULT_PORT = 8080;

    private static ExecutorService executor = Executors.newFixedThreadPool(50);


    public static void main(String[] args) {
        try (ServerSocket serverConnect = new ServerSocket(DEFAULT_PORT)) {
            LOGGER.info(String.format("Java Web Server - Started. Listening to port : %d", DEFAULT_PORT));

            while (true) {
                Runnable requestHandler = new RequestHandler(serverConnect.accept());
                executor.execute(requestHandler);
            }
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("Exception running the Java Web Server. Error Msg: {}", e.getMessage());
        }
    }
}