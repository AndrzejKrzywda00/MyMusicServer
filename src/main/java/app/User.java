package app;

import app.interfaces.ITextSerializable;

public class User implements ITextSerializable {

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
        StringBuilder output = new StringBuilder();

        output.append(name);
        output.append(";");
        output.append(login);
        output.append(";");
        output.append(avatar);
        output.append(";");
        output.append(description);
        output.append(";");

        return output.toString();
    }


}
