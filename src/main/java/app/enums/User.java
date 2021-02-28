package app.enums;

public class User {

    /*
    This class encapsulates data for user that is associated with login data
     */

    private String name;
    private String login;
    private String avatar;
    private String description;

    public String getName() { return this.name; }

    public String getLogin() { return this.login; }

    public String getAvatar() { return  this.avatar; }

    public String getDescription() { return this.description; }

    public String serialize() {
        String output = "";
        return output;
    }


}
