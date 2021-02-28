package app;

import app.enums.KeyEnum;
import app.enums.User;
import httpserver.HttpServer;
import httpserver.Request;
import httpserver.Response;
import httpserver.config.Configuration;
import httpserver.config.ConfigurationManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class AppServer {

    /*
    This class handles the whole app-server operations
    I suggest it only has handle methods
    and communication methods with class that manages traffic
    and saves to databases with separate threads

    It uses the principles of restful-api
    so the application refers its state in a single message

    for example:
    In application user puts a new track
    when he clicks "confirm"
    a request with PUT title is created
    a message is sent through internet
    server reads it and creates thread to handle
    when the type is PUT
    it transfers body into an object of
    proper data-base oriented type
    and attempts to write to database
     */

    private static final int defaultPort = 6653;
    private static final long idleApplicationLifetime = 30 * 60 * 1000; // 30 minutes
    private static final int  purgeApplicationLifetime = 10 * 60 * 1000; // 10 minutes

    private HttpServer webServer = null;    // it HAS HttpServer as running utlity

    // these are connected apps at the moment
    // these are pairs -> uniqueID : MyMusicApplication
    private final Map<String,MyMusicApplication> applicationList = new HashMap<String,MyMusicApplication>();

    // these are users at the moment
    // this list is connected with applicationList
    // the one's id is the same as user id
    private final Map<String,User> users = new HashMap<String,User>();

    public AppServer() {

        // passing configuration
        ConfigurationManager.getInstance().loadConfigurationFile("src/main/resources/http.json");
        Configuration configuration = ConfigurationManager.getInstance().getConfiuration();

        // creating an actual object
        try {
            webServer = new HttpServer(configuration.getAddress(), configuration.getPort());
        } catch (IOException e) {
            System.out.println("For some reason couldn't established the server instance");
        }
    }

    // starting the backbone server
    public void start() {
        webServer.start();
    }

    // stoppping the backbone server
    public void stop() {
        webServer.stop();
    }

    public String addApplication() {
        // possibly to build MyMusicApplication object
        // TODO -- define which parameters will be passed to MyMusicApplication
        return "";
    }

    private boolean authorize(Request request) {
        // TODO -- build authorization mechanism
        // for now we have "accept all policy"
        // the header to tell me about first time user is
        // Register : true
        
    }

    private boolean addApplicationHandler(Request request, Response response) {
        return false;
    }
}
