package app.enums;

public enum MessageType {

    /*
    This enum works as container for possible field values for "Content-Type" header
     */

    LoginMessage("login message"),
    NewTrackMessage("new track"),
    NewPlaylistMessage("new playlist"),
    TracksDataMessage("tracks data"),
    ArchivePlaylistMessage("archive playlist");

    private String name;

    MessageType(String name) {
        this.name = name;
    }

}
