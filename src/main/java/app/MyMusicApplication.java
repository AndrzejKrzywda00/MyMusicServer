package app;

public class MyMusicApplication {

    /*
    This is a class representing an instance of running application
    Server will have many objcets of this type running
     */

    private int timeSinceLastActivity;
    private int amountOfConnectionsEstablished;
    private int amountOfDataSaved;   // counts the amount of tracks saved

    public MyMusicApplication() {
        timeSinceLastActivity = 0;
        amountOfConnectionsEstablished = 0;
        amountOfDataSaved = 0;
    }

}
