package app.enums;

public enum Headers {

    userID("userID"),
    login("login");

    public final String name;

    Headers(String name) {
        this.name = name;
    }

}
