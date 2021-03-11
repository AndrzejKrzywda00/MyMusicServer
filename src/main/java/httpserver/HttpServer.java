/*
    This is code for main server that is going to work with MyMusic application
    @author : Andrzej Krzywda
 */
package httpserver;


import httpserver.enums.Status;
import httpserver.config.Configuration;
import httpserver.config.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Utility;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.SortedMap;
import java.util.TreeMap;


public class HttpServer {

    private final ServerSocket serverSocket;    // all conections will be opened on this socket
    private final Thread listenerThread;        // this thread listens to comming connections
    private Boolean running = false;            // info if the server is running
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServer.class); // type is OK but sitll not sure if this is right one


    // constructor
    public HttpServer(String address, int port) throws IOException {

        final int backLogSize = 16;
        serverSocket = new ServerSocket(port, backLogSize, InetAddress.getByName(address));
        listenerThread = new Thread(this::listen);
        /*LOGGER.info("Server is starting...");
        LOGGER.info("Using adress " + configuration.getAddress());
        LOGGER.info("Using port " + configuration.getPort());
        LOGGER.info("Using webroot " + configuration.getWebroot());*/
    }


    public interface ServerHandler {    // interface declared inside the class of HttpServer
        /***
         *
         * Interprets the request and builds appropriate response
         *
         * @param request   parsed http request received from some client
         * @param response  httpserver response object that will be sent back to the client
         * @return
         */
        Boolean handle(Request request, Response response);
        // handler is an object that can handle ( request -> response )
    }

    // sorted map that has pairs -> "description" : handler
    private final SortedMap<String, ServerHandler> handlers = new TreeMap<String, ServerHandler>((String a, String b) -> {
        /*
        Sort by length of header key, but in a case of a tie it will use generic method to compare so
        In alphabetical order
         */
        if(b.length() == a.length()) {
            return b.length() - a.length();
        }
        else {
            return a.compareTo(b);
        }
    });

    /***
     *
     * @return  Address of this server in format x.x.x.x:p
     */
    public String getAddress() {
        return String.format("%s:%s", serverSocket.getInetAddress().getHostAddress(), serverSocket.getLocalPort());
    }

    /***
     * Starts the server by calling ListenerThread to start working
     */
    public void start() {
        running = true;
        listenerThread.start();
    }

    /***
     * Calls server to stop listening and go to passive state - program will end soon
     * @return  true if successfull, false if problem with closing the socket
     */
    public Boolean stop() {
        // will cause 'serverSocket.accept() to interrupt, so will stop the listenerThread
        try {
            serverSocket.close();
            running = false;
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /***
     *
     * @param URI       The URI of the handler
     * @param handler   The actual Handler object
     * @return          true if handler has been successfully added, false otherwise
     */
    public boolean addHandler(String URI, ServerHandler handler) {
        if (running) {
            // Thread listening must be stoppend before adding anything to the server
            System.out.println("Stop the server before adding handlers");
            return false;
        }

        if (handlers.containsKey(URI)) {
            System.out.println("Handler with this name already exists");
            return false;
        }

        handlers.put(URI, handler);     // adding the handler to the treemap
        return true;
    }

    /***
     *
     * @param URI   name of the handler you want to remove
     * @return      true if have been removed, false otherwise
     */
    public boolean removeHandler(String URI) {
        if (running) {
            System.out.println("Stop the server before removing handlers");
            return false;
        }

        if (!handlers.containsKey(URI)) {
            System.out.println("There is no such handler to remove");
            return false;
        }

        handlers.remove(URI);
        return true;
    }

    private void listen() {
        /*
        This thrad is listening on serverSocket for incomming connections
        This is the main functionality of the server
         */
        while(true) {
            Socket client;
            try {
                client = serverSocket.accept();     // waits till connection is established
                LOGGER.info("Connection accepted from " + client.getInetAddress() + " on clients port "+ client.getPort());
            } catch (IOException e) {
                //e.printStackTrace();
                return;
            }

            // create new Thread to handle the request
            // this is annonymous labmda thread
            new Thread(() -> {
                try {
                    handleRequest(client);
                } catch (IOException e) {
                    System.out.println("Error at handleRequest()");
                }

            }).start();
        }
    }

    private void handleRequest(Socket clientSocket) throws IOException {
        BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));

        // client can establish connection before sending data to me
        final double waitTime = 10;
        // retries to read data after [d] seconds
        double waitInterval = 0.2;
        // current waittime in seconds
        double waitedFor = 0;
        while(waitedFor < waitTime && !input.ready()) {
            try {
                Thread.sleep((long) waitInterval * 1000);
                waitedFor += waitInterval;
                waitInterval *= 1.5;
            } catch (InterruptedException e) {
                return;
            }
        }

        if(!input.ready()) {
            // do data received from client in [waitTime] so closing the connection
            clientSocket.close(); // closing the socket
            return;
        }

        StringBuilder data = new StringBuilder();
        while(input.ready()) {
            // reading all lines from the socket
            data.append(input.readLine());
            data.append("\n");
        }
        clientSocket.shutdownInput();

        Request request = null;
        Response response = new Response();
        try {
            request = new Request(data.toString()); // packing data into request
            //LOGGER.info(request.toString());    // see the actual request from browser
        } catch (Exception e) {
            System.out.println("There was a problem passing data to Request class");
            response.setStatus(Status.BadRequest_400);
            response.setBody("<h1> BAD REQUEST </h1>", Response.BodyType.HTML);
            clientSocket.getOutputStream().write(response.toByteArray());
            clientSocket.close();
            e.printStackTrace();
        }

        for (String URI : handlers.keySet()) {
            if(request.URI.startsWith(URI)) {
                if(handlers.get(URI).handle(request, response)) {
                    break;
                }
            }
        }

        response.setHeader("Requested-URI", request.URI);

         System.out.printf(
                 // formatting information
                 // https://en.wikipedia.org/wiki/ANSI_escape_code
                 "|>\033[93m%s\n\033[91m<<<<<<<< INBOUND\033[0m\n%s\n\033[32m>>>>>>>> OUTBOUND\033[0m\n%s\n\033[96m****************\033[0m\n\n",
                 LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                 Utility.leftPad(request.toString(), "\033[91m| \033[0m"),
                 Utility.leftPad(response.toString(), "\033[32m| \033[0m"));

        clientSocket.getOutputStream().write(response.toByteArray());   // data went to client
        clientSocket.shutdownOutput();
        clientSocket.close();
    }

    public static void main(String[] args) {
        // entry point of http server

        ConfigurationManager.getInstance().loadConfigurationFile("src/main/resources/http.json");
        Configuration configuration = ConfigurationManager.getInstance().getConfiuration();

        HttpServer server = null;

        // configuration will have to be passed quite different
        // beacuse HttpServer class will be administrated via AppServer instance
        try {
            server = new HttpServer(configuration.getAddress(), configuration.getPort());
        } catch (IOException e) {
            System.out.println("For some reason couldn't established the server instance");
            e.printStackTrace();
        }

        // this is default handler and should not be removed
        server.addHandler("/", (Request request, Response response) -> {
           response.setStatus(Status.OK_200);
           response.setBody("<h1>Ten serwer działa</h1>", Response.BodyType.HTML);
           return true;
        });

        // here will i have to create and pass handlers for managment
        // with different types of requests

        server.start();
    }

}
