package app;

import app.enums.KeyEnum;
import httpserver.HttpServer;
import httpserver.Request;
import httpserver.Response;
import httpserver.config.Configuration;
import httpserver.config.ConfigurationManager;
import httpserver.enums.Status;

import java.io.IOException;
import java.util.HashMap;
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
    private final Map<String, MyMusicApplication> applicationList = new HashMap<String, MyMusicApplication>();

    // these are users at the moment
    // this list is connected with applicationList
    // the one's id is the same as user id
    private final Map<String, User> users = new HashMap<String,User>();

    public AppServer() {

        // passing configuration to the webServer
        ConfigurationManager.getInstance().loadConfigurationFile("src/main/resources/http.json");
        Configuration configuration = ConfigurationManager.getInstance().getConfiuration();

        // creating an actual object
        // configuration to webServer is injected by appServer
        try {
            webServer = new HttpServer(configuration.getAddress(), configuration.getPort());
        } catch (IOException e) {
            System.out.println("For some reason couldn't established the webServer instance");
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

    public boolean addApplication(Request request) {
        // possibly to build MyMusicApplication object
        // TODO -- define which parameters will be passed to MyMusicApplication
        if(!authorize(request)) {
            return false;
        }

        if(applicationList.containsKey(request.headers.getOrDefault(KeyEnum.userID.name, null))) {
            return false;
        }

        MyMusicApplication application = new MyMusicApplication();
        applicationList.put(request.headers.get(KeyEnum.userID.name), application);
        return true;
    }

    /***
     * This function authorizes every http transaticon between server and client
     * @param request is put into function and headers are scanned for appropriate data
     * @return  true if successfull, otherwise false
     */
    private boolean authorize(Request request) {
        // TODO -- build authorization mechanism - based on taking data from database

        String userID = request.headers.getOrDefault(KeyEnum.userID.name, null);
        String login = request.headers.getOrDefault(KeyEnum.login.name, null);

        if(userID != null && login != null) {           // there is identyfication header at all
            if(users.containsKey(userID)) {             // is there such ID assigned to active user ?
                if(users.get(userID).equals(login)) {   // is the ID -> login association correct ?
                    return true;
                }
            }
        }

        return false;   // returns false if theree is no auth data or there is wrong association
    }

    private boolean addApplicationHandler(Request request, Response response) {
        return false;
    }

    /*
    Handlers - the actual content of the application - the functions that repond to Requests
     */

    /***
     * This handler handles login operation
     * @param request contains login and password data
     * @param response contains info if authorized successfully and UniqueID in such case
     * @return true if no problems, false otherwise
     */
    private boolean loginHandler(Request request, Response response) {
        // auhtorization here is not possible because by definition user is to be authorized yet
        return false;
    }


    /***
     * This handler retrieves data from db and passes a package to client
     * @param request is a data request
     * @param response is a data response
     * @return true if successfull, false in other cases
     */
    private boolean getDataHandler(Request request, Response response) {
        // data will be packed into lines of maximum of [maximumDataCapacity]
        if(!authorize(request)) {
            response.setStatus(Status.Unauthorized_401);
            response.setBody("No userID provided or userID doesn't match provided login", Response.BodyType.Text);
            return false;
        }
        return true;

    }

    /***
     * This handler removes runtime data when user logs out
     * @param request informs about logout
     * @param response informs about removing all unnecessary data
     * @return true if successfull, false in other cases
     */
    private boolean logOutHandler(Request request, Response response) {
        if(!authorize(request)) {
            response.setStatus(Status.Unauthorized_401);
            response.setBody("No userID provided or userID doesn't match provided login", Response.BodyType.Text);
            return false;
        }
        return true;
    }

    /***
     * This handler handles track addition to database
     * @param request contains data to be added to db tracks
     * @param response informs whether operation has been successfull
     * @return true is successfull, false otherwise
     */
    private boolean addTrackHandler(Request request, Response response) {
        // accessing the database via special class
        // checking if addition is at all possible
        // adding
        // information goes back
        if(!authorize(request)) {
            response.setStatus(Status.Unauthorized_401);
            response.setBody("No userID provided or userID doesn't match provided login", Response.BodyType.Text);
            return false;
        }
        return true;
    }

    /***
     * This handler handles playlist addition to database
     * @param request contains data to be added to db playlists
     * @param response informs whether operation has been successfull
     * @return true if successfull, false otherwise
     */
    private boolean addPlaylistHandler(Request request, Response response) {
        if(!authorize(request)) {
            response.setStatus(Status.Unauthorized_401);
            response.setBody("No userID provided or userID doesn't match provided login", Response.BodyType.Text);
            return false;
        }
        return true;
    }

    public static void main(String[] args) {

        /*
        Entry point of web application
         */

        String address = "127.0.0.1";
        int port = defaultPort;

        if(args.length >= 1) {
            String[] tmp = args[0].split(":",2);
            if(tmp.length == 2) {
                try {
                    int tmpPort = Integer.parseInt(tmp[1]);
                    address = tmp[0];
                    port = tmpPort;
                } catch (Exception e) {
                    System.out.println("Wrong data provided");
                }
            }
        }
        else {
            // deafult configuration
            System.out.println("[server] ip:port - start server with custom ip/port");
            System.out.println("Example: 'java GameServer 192.168.0.1:2222'");
            System.out.println(" ");
        }

        AppServer server;   // my instance of running server that handles applications
        server = new AppServer();
        server.start();
        System.out.printf("Game server listening at %s\n\n", server.webServer.getAddress());

        // TODO -- add timer
    }

}
