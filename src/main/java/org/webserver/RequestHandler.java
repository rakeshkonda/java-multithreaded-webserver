package org.webserver;

import org.slf4j.LoggerFactory;
import org.webserver.common.FileUtils;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.StringTokenizer;

/**
 * Handles incoming requests in its own thread & keeps connection alive based on incoming HTTP Request version & keep-alive header
 *
 * References:
 * HTTP “Connection: keep-alive” - https://blog.insightdatascience.com/learning-about-the-http-connection-keep-alive-header-7ebe0efa209d
 * https://en.wikipedia.org/wiki/HTTP_persistent_connection
 */
public class RequestHandler implements Runnable {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(RequestHandler.class);
    private static final File WEBAPP_BASE_DIR = new File("src/webapp");
    private static final String DEFAULT_FILE = "index.html";
    private static final String FILE_NOT_FOUND_PAGE = WEBAPP_BASE_DIR + "/404.html";
    private static final String SUPPORT_PAGE = WEBAPP_BASE_DIR + "/support.html";

    private Socket socket;

    RequestHandler(Socket mySocket) {
        this.socket = mySocket;
    }

    @Override
    public void run() {
        BufferedReader incomingRequest = null;
        String fileRequested = DEFAULT_FILE;
        String httpVersion = "HTTP/1.1";
        String httpMethod = null;
        boolean connectionKeepAlive = true;

        try {
            socket.setSoTimeout(50000);
            incomingRequest = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String inputURL = incomingRequest.readLine(); //read first line to check http request type (GET or HEAD or POST ...)
            StringTokenizer parsedInput = new StringTokenizer(inputURL);

            String token = null;
            while(parsedInput.hasMoreTokens())
            {
                token = parsedInput.nextToken();
                if(token.startsWith("/"))  //fetch file requested
                    fileRequested = token;
                else if(token.startsWith("HTTP")) //fetch HTTP version
                    httpVersion = token;
                else if(token.equals("Connection:")) {//check connection type
                    if (parsedInput.nextToken().equals("keep-alive"))  //checking for connection type
                        connectionKeepAlive = Boolean.valueOf(token);
                } else
                    httpMethod = token;
            }


            if(httpMethod != null && httpMethod.equals("GET")) {
                if (fileRequested.endsWith("/"))
                    fileRequested = DEFAULT_FILE;

                String sourceFile = WEBAPP_BASE_DIR + "/" + fileRequested;
                if(Files.exists(Paths.get(sourceFile))) {
                    String contentMimeType = FileUtils.getContentType(fileRequested);
                    httpVersion += " 200 OK";
                    sendFileOut(sourceFile, httpVersion, contentMimeType);
                } else {
                    httpVersion += " 404 Not Found";
                    sendFileOut(FILE_NOT_FOUND_PAGE,  httpVersion, "text/html");
                }
            } else {
                //unsupported http httpMethod
                httpVersion += " 501 Not Implemented";
                sendFileOut(SUPPORT_PAGE, httpVersion, "text/html");
            }

        }  catch (IOException ioe) {
            LOGGER.error("Internal Server error : " + ioe);
        } finally {
            closeIO(incomingRequest, httpVersion, connectionKeepAlive);
        }
    }

    private void sendFileOut(String sourceFilePath, String protocol, String contentMimeType) throws IOException {
        PrintWriter out = null;
        BufferedOutputStream dataOut = null;


        Path path = Paths.get(sourceFilePath);
        byte[] fileData = Files.readAllBytes(path);
        int fileLength = new String(fileData).length();

        out = new PrintWriter(socket.getOutputStream());
        dataOut = new BufferedOutputStream(socket.getOutputStream());

        out.println(protocol);
        out.println("Content-type: " + contentMimeType);
        out.println();
        out.flush();

        dataOut.write(fileData, 0, fileLength);
        dataOut.flush();
        LOGGER.info("Serving the Resource:{}, FileLength: {}, ContentType: {}", sourceFilePath, fileLength, contentMimeType);
    }

    private void closeIO(BufferedReader incomingRequest, String httpVersion, boolean connectionKeepAlive) {
        try {
            if(httpVersion.contains("HTTP/1.0") && !connectionKeepAlive) {
                socket.close();
                if (incomingRequest != null) {
                    incomingRequest.close();
                }
            } else if(httpVersion.contains("HTTP/1.0") && connectionKeepAlive) {
                socket.setKeepAlive(true);
                socket.setSoTimeout(5000); //add another 5 seconds
            } else if(httpVersion.contains("HTTP/1.1") && !connectionKeepAlive) {
                socket.setKeepAlive(false);
                socket.close();
                if (incomingRequest != null) {
                    incomingRequest.close();
                }
            } else if(httpVersion.contains("HTTP/1.1")) {
                socket.setKeepAlive(true);
                socket.setSoTimeout(5000);
            }

            LOGGER.info("HTTP Version: {}, Keep Alive: {}, ConnectionAlive: {}", httpVersion, socket.getSoTimeout(), socket.getKeepAlive());

            if (incomingRequest != null) {
                incomingRequest.close();
            }
        } catch (Exception e) {
            LOGGER.error("Error closing stream : " + e.getMessage());
        }
    }
}
